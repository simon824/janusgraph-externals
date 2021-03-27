# 删索引 Removal

> Warning
Index removal is a manual process comprised of multiple steps. These steps must be carefully followed in the right order to avoid index inconsistencies.
索引删除是一个手动过程，包含多个步骤。必须以正确的顺序认真执行这些步骤，以避免索引不一致。

## 总览 Overview
Index removal is a two-stage process. In the first stage, one JanusGraph signals to all others via the storage backend that the index is slated for deletion. This changes the index’s state to `DISABLED`. At that point, JanusGraph stops using the index to answer queries and stops incrementally updating the index. Index-related data in the storage backend remains present but ignored.

The second stage depends on whether the index is mixed or composite. A composite index can be deleted via JanusGraph. As with reindexing, removal can be done through either MapReduce or JanusGraphManagement. However, a mixed index must be manually dropped in the index backend; JanusGraph does not provide an automated mechanism to delete an index from its index backend.

Index removal deletes everything associated with the index except its schema definition and its `DISABLED` state. This schema stub for the index remains even after deletion, though its storage footprint is negligible and fixed.

索引删除是一个分为两个阶段的过程。

- 在第一阶段，一个JanusGraph通过存储后端向所有其他信号通知该索引将要删除。这会将索引的状态更改为DISABLED。那时，JanusGraph停止使用索引来回答查询，并停止以增量方式更新索引。存储后端中与索引相关的数据仍然存在，但被忽略。
- 第二阶段取决于索引是混合索引还是复合索引。可以通过JanusGraph删除复合索引。与重新索引一样，可以通过MapReduce或JanusGraphManagement进行删除。但是，必须手动将混合索引放置在索引后端中。 JanusGraph不提供从索引后端删除索引的自动机制。

索引删除将删除与索引关联的所有内容，但其schema定义和DISABLED状态除外。索引的此模式存根即使在删除后仍会保留，尽管其存储空间可以忽略不计并已修复。

## 索引删除准备阶段 Preparing for Index Removal
If the index is currently enabled, it should first be disabled. This is done through the `ManagementSystem`.

如果当前启用了索引，则应首先将其禁用。 这是通过管理系统完成的。
```
mgmt = graph.openManagement()
rindex = mgmt.getRelationIndex(mgmt.getRelationType("battled"), "battlesByTime")
mgmt.updateIndex(rindex, SchemaAction.DISABLE_INDEX).get()
gindex = mgmt.getGraphIndex("byName")
mgmt.updateIndex(gindex, SchemaAction.DISABLE_INDEX).get()
mgmt.commit()
```
Once the status of all keys on the index changes to `DISABLED`, the index is ready to be removed. A utility in ManagementSystem can automate the wait-for-`DISABLED` step:

一旦索引上所有键的状态更改为DISABLED，就可以删除索引了。 ManagementSystem中的实用程序可以自动执行等待禁用步骤：
```
ManagementSystem.awaitGraphIndexStatus(graph, 'byName').status(SchemaStatus.DISABLED).call()
```
After a composite index is `DISABLED`, there is a choice between two execution frameworks for its removal:

- MapReduce
- JanusGraphManagement

Index removal on MapReduce supports large, horizontally-distributed databases. Index removal on JanusGraphManagement spawns a single-machine OLAP job. This is intended for convenience and speed on those databases small enough to be handled by one machine.

Index removal requires:

- The index name (a string — the user provides this to JanusGraph when building a new index)
- The index type (a string — the name of the edge label or property key on which the vertex-centric index is built). This applies only to vertex-centric indexes - leave blank for global graph indexes.

As noted in the overview, a mixed index must be manually dropped from the indexing backend. Neither the MapReduce framework nor the JanusGraphManagement framework will delete a mixed backend from the indexing backend.

禁用组合索引后，可以在两个执行框架之间进行选择以将其删除：

- MapReduce
- JanusGraphManagement

MapReduce上的索引删除支持大型的，水平分布的数据库。 JanusGraphManagement上的索引删除会生成单机OLAP作业。 这是为了在那些小到可以由一台机器处理的数据库上提供便利和速度。

索引删除要求：

- 索引名称（字符串-building用户在建立新索引时将其提供给JanusGraph）
- 索引类型（字符串-built构建以顶点为中心的索引的边缘标签或属性键的名称）。 这仅适用于以顶点为中心的索引-对于全局图索引，保留为空白。

如概述中所述，必须从索引后端手动删除混合索引。 MapReduce框架和JanusGraphManagement框架都不会从索引后端删除混合后端。

## 在 MR 上执行索引删除任务 Executing an Index Removal Job on MapReduce
As with reindexing, the recommended way to generate and run an index removal job on MapReduce is through the `MapReduceIndexManagement` class. Here is a rough outline of the steps to run an index removal job using this class:

- Open a `JanusGraph` instance
- If the index has not yet been disabled, disable it through `JanusGraphManagement`
- Pass the graph instance into `MapReduceIndexManagement`'s constructor
- Call `updateIndex(<index>, SchemaAction.REMOVE_INDEX)`

A commented code example follows in the next subsection.

与重新索引一样，在MapReduce上生成和运行索引删除作业的推荐方法是通过MapReduceIndexManagement类。 这是使用此类运行索引删除作业的步骤的粗略概述：

- 打开一个JanusGraph实例
- 如果尚未禁用索引，请通过JanusGraphManagement禁用它
- 将图形实例传递到MapReduceIndexManagement的构造函数中
- 调用updateIndex（<index>，SchemaAction.REMOVE_INDEX）

下一节将介绍一个注释的代码示例。

### Example for MapReduce
```java
import org.janusgraph.graphdb.database.management.ManagementSystem
// Load the "Graph of the Gods" sample data
graph = JanusGraphFactory.open('conf/janusgraph-cql-es.properties')
g = graph.traversal()
GraphOfTheGodsFactory.load(graph)
g.V().has('name', 'jupiter')
// Disable the "name" composite index
m = graph.openManagement()
nameIndex = m.getGraphIndex('name')
m.updateIndex(nameIndex, SchemaAction.DISABLE_INDEX).get()
m.commit()
graph.tx().commit()
// Block until the SchemaStatus transitions from INSTALLED to REGISTERED
ManagementSystem.awaitGraphIndexStatus(graph, 'name').status(SchemaStatus.DISABLED).call()
// Delete the index using MapReduceIndexJobs
m = graph.openManagement()
mr = new MapReduceIndexManagement(graph)
future = mr.updateIndex(m.getGraphIndex('name'), SchemaAction.REMOVE_INDEX)
m.commit()
graph.tx().commit()
future.get()
// Index still shows up in management interface as DISABLED -- this is normal
m = graph.openManagement()
idx = m.getGraphIndex('name')
idx.getIndexStatus(m.getPropertyKey('name'))
m.rollback()
// JanusGraph should issue a warning about this query requiring a full scan
g.V().has('name', 'jupiter')
```

## 在 JanusGraphManagement 执行索引删除任务 Executing an Index Removal job on JanusGraphManagement
To run an index removal job on JanusGraphManagement, invoke `JanusGraphManagement.updateIndex` with the `SchemaAction.REMOVE_INDEX` argument. For example:
```
m = graph.openManagement()
i = m.getGraphIndex('indexName')
m.updateIndex(i, SchemaAction.REMOVE_INDEX).get()
m.commit()
```

### Example for JanusGraphManagement
The following loads some indexed sample data into a BerkeleyDB-backed JanusGraph database, then disables and removes the index through JanusGraphManagement:
```java
import org.janusgraph.graphdb.database.management.ManagementSystem
// Load the "Graph of the Gods" sample data
graph = JanusGraphFactory.open('conf/janusgraph-cql-es.properties')
g = graph.traversal()
GraphOfTheGodsFactory.load(graph)
g.V().has('name', 'jupiter')
// Disable the "name" composite index
m = graph.openManagement()
nameIndex = m.getGraphIndex('name')
m.updateIndex(nameIndex, SchemaAction.DISABLE_INDEX).get()
m.commit()
graph.tx().commit()
// Block until the SchemaStatus transitions from INSTALLED to REGISTERED
ManagementSystem.awaitGraphIndexStatus(graph, 'name').status(SchemaStatus.DISABLED).call()
// Delete the index using JanusGraphManagement
m = graph.openManagement()
nameIndex = m.getGraphIndex('name')
future = m.updateIndex(nameIndex, SchemaAction.REMOVE_INDEX)
m.commit()
graph.tx().commit()
future.get()
m = graph.openManagement()
nameIndex = m.getGraphIndex('name')
g.V().has('name', 'jupiter')
```
