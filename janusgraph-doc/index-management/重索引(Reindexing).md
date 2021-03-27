# 重索引 Reindexing

[Graph Index](https://docs.janusgraph.org/index-management/index-performance/#graph-index) and [Vertex-centric Indexes](https://docs.janusgraph.org/index-management/index-performance/#vertex-centric-indexes) describe how to build graph-global and vertex-centric indexes to improve query performance. These indexes are immediately available if the indexed keys or labels have been newly defined in the same management transaction. In this case, there is no need to reindex the graph and this section can be skipped. If the indexed keys and labels already existed prior to index construction it is necessary to reindex the entire graph in order to ensure that the index contains previously added elements. This section describes the reindexing process.

图索引和以顶点为中心的索引描述了如何构建图全局索引和以顶点为中心的索引以提高查询性能。 如果已在同一管理事务中新定义了索引键或标签，则这些索引立即可用。 在这种情况下，无需为图形重新编制索引，并且可以跳过此部分。 如果在建立索引之前已经存在已建立索引的键和标签，则有必要重新索引整个图形，以确保索引包含先前添加的元素。 本节介绍重新索引过程。

Warning

Reindexing is a manual process comprised of multiple steps. These steps must be carefully followed in the right order to avoid index inconsistencies.

重新索引编制是一个手动过程，包含多个步骤。 必须以正确的顺序认真执行这些步骤，以避免索引不一致。

## Overview
JanusGraph can begin writing incremental index updates right after an index is defined. However, before the index is complete and usable, JanusGraph must also take a one-time read pass over all existing graph elements associated with the newly indexed schema type(s). Once this reindexing job has completed, the index is fully populated and ready to be used. The index must then be enabled to be used during query processing.

JanusGraph可以在定义索引后立即开始编写增量索引更新。 但是，在索引完成并可用之前，JanusGraph还必须对与新索引的模式类型关联的所有现有图形元素进行一次一次性读取。 一旦完成重新索引编制工作，索引将完全填充并准备使用。 然后必须启用索引以在查询处理期间使用它。

## Prior to Reindex 重新索引之前
The starting point of the reindexing process is the construction of an index. Refer to [Indexing for Better Performance](https://docs.janusgraph.org/index-management/index-performance/) for a complete discussion of global graph and vertex-centric indexes. Note, that a global graph index is uniquely identified by its name. A vertex-centric index is uniquely identified by the combination of its name and the edge label or property key on which the index is defined - the name of the latter is referred to as the **index type** in this section and only applies to vertex-centric indexes.

After building a new index against existing schema elements it is recommended to wait a few minutes for the index to be announced to the cluster. Note the index name (and the index type in case of a vertex-centric index) since this information is needed when reindexing.

重新索引过程的起点是索引的构建。 有关全局图和以顶点为中心的索引的完整讨论，请参阅为更好的性能建立索引。 注意，全局图索引由其名称唯一标识。 以顶点为中心的索引由其名称和定义该索引的边缘标签或属性键的组合唯一标识-后者的名称在本节中称为索引类型，仅适用于以顶点为中心的索引类型 索引。

针对现有架构元素构建新索引后，建议等待几分钟，以将索引发布给集群。 请注意索引名称（在以顶点为中心的索引的情况下为索引类型），因为在重新索引时需要此信息。

## Preparing to Reindex
There is a choice between two execution frameworks for reindex jobs:

- MapReduce
- JanusGraphManagement

Reindex on MapReduce supports large, horizontally-distributed databases. Reindex on JanusGraphManagement spawns a single-machine OLAP job. This is intended for convenience and speed on those databases small enough to be handled by one machine.

Reindexing requires:

- The index name (a string — the user provides this to JanusGraph when building a new index)
- The index type (a string — the name of the edge label or property key on which the vertex-centric index is built). This applies only to vertex-centric indexes - leave blank for global graph indexes.

在两个用于重新索引作业的执行框架之间可以选择：

- MapReduce
- JanusGraphManagement

MapReduce上的Reindex支持大型的，水平分布的数据库。 JanusGraphManagement上的Reindex生成单机OLAP作业。 这是为了在那些小到可以由一台机器处理的数据库上提供便利和速度。

重新索引要求：

- 索引名称（字符串-building用户在建立新索引时将其提供给JanusGraph）
- 索引类型（字符串-built构建以顶点为中心的索引的边缘标签或属性键的名称）。 这仅适用于以顶点为中心的索引-对于全局图索引，保留为空白。
<a name="executing-a-reindex-job-on-mapreduce"></a>
## Executing a Reindex Job on MapReduce
The recommended way to generate and run a reindex job on MapReduce is through the `MapReduceIndexManagement` class. Here is a rough outline of the steps to run a reindex job using this class:

- Open a `JanusGraph` instance
- Pass the graph instance into `MapReduceIndexManagement`'s constructor
- Call `updateIndex(<index>, SchemaAction.REINDEX)` on the `MapReduceIndexManagement` instance
- If the index has not yet been enabled, enable it through `JanusGraphManagement`

This class implements an `updateIndex` method that supports only the `REINDEX` and `REMOVE_INDEX` actions for its `SchemaAction` parameter. The class starts a Hadoop MapReduce job using the Hadoop configuration and jars on the classpath. Both Hadoop 1 and 2 are supported. This class gets metadata about the index and storage backend (e.g. the Cassandra partitioner) from the `JanusGraph` instance given to its constructor.

在MapReduce上生成和运行重新索引作业的推荐方法是通过MapReduceIndexManagement类。 这是使用此类运行重新索引作业的步骤的粗略概述：
- 打开一个JanusGraph实例
- 将图形实例传递到MapReduceIndexManagement的构造函数中
- 在MapReduceIndexManagement实例上调用updateIndex（，SchemaAction.REINDEX）
- 如果尚未启用索引，请通过JanusGraphManagement启用它

此类实现一个updateIndex方法，该方法的SchemaAction参数仅支持REINDEX和REMOVE_INDEX操作。 该类使用Hadoop配置和类路径上的jar启动Hadoop MapReduce作业。 Hadoop 1和2均受支持。 此类从提供给其构造函数的JanusGraph实例获取有关索引和存储后端（例如Cassandra分区器）的元数据。

```java
graph = JanusGraphFactory.open(...)
mgmt = graph.openManagement()
mr = new MapReduceIndexManagement(graph)
mr.updateIndex(mgmt.getRelationIndex(mgmt.getRelationType("battled"), "battlesByTime"), SchemaAction.REINDEX).get()
mgmt.commit()
```

### Reindex Example on MapReduce
The following Gremlin snippet outlines all steps of the MapReduce reindex process in one self-contained example using minimal dummy data against the Cassandra storage backend.

以下Gremlin代码片段在一个独立的示例中概述了MapReduce重新索引过程的所有步骤，该示例使用针对Cassandra存储后端的最少虚拟数据。
```java
// Open a graph
graph = JanusGraphFactory.open("conf/janusgraph-cql-es.properties")
g = graph.traversal()
    
// Define a property
mgmt = graph.openManagement()
desc = mgmt.makePropertyKey("desc").dataType(String.class).make()
mgmt.commit()
    
// Insert some data
graph.addVertex("desc", "foo bar")
graph.addVertex("desc", "foo baz")
graph.tx().commit()
    
// Run a query -- note the planner warning recommending the use of an index
g.V().has("desc", containsText("baz"))
    
// Create an index
mgmt = graph.openManagement()
desc = mgmt.getPropertyKey("desc")
mixedIndex = mgmt.buildIndex("mixedExample", Vertex.class).addKey(desc).buildMixedIndex("search")
mgmt.commit()
    
// Rollback or commit transactions on the graph which predate the index definition
graph.tx().rollback()
// Block until the SchemaStatus transitions from INSTALLED to REGISTERED
report = ManagementSystem.awaitGraphIndexStatus(graph, "mixedExample").call()
// Run a JanusGraph-Hadoop job to reindex
mgmt = graph.openManagement()
mr = new MapReduceIndexManagement(graph)
mr.updateIndex(mgmt.getGraphIndex("mixedExample"), SchemaAction.REINDEX).get()
    
// Enable the index
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("mixedExample"), SchemaAction.ENABLE_INDEX).get()
mgmt.commit()
    
// Block until the SchemaStatus is ENABLED
mgmt = graph.openManagement()
report = ManagementSystem.awaitGraphIndexStatus(graph, "mixedExample").status(SchemaStatus.ENABLED).call()
mgmt.rollback()
    
// Run a query -- JanusGraph will use the new index, no planner warning
g.V().has("desc", containsText("baz"))
    
// Concerned that JanusGraph could have read cache in that last query, instead of relying on the index?
// Start a new instance to rule out cache hits.  Now we're definitely using the index.
graph.close()
graph = JanusGraphFactory.open("conf/janusgraph-cql-es.properties")
g.V().has("desc", containsText("baz"))
```

## Executing a Reindex job on JanusGraphManagement
To run a reindex job on JanusGraphManagement, invoke `JanusGraphManagement.updateIndex` with the `SchemaAction.REINDEX` argument. For example:
```java
m = graph.openManagement()
i = m.getGraphIndex('indexName')
m.updateIndex(i, SchemaAction.REINDEX).get()
m.commit()
```

### Example for JanusGraphManagement
The following loads some sample data into a BerkeleyDB-backed JanusGraph database, defines an index after the fact, reindexes using JanusGraphManagement, and finally enables and uses the index:
```java
import org.janusgraph.graphdb.database.management.ManagementSystem
// Load some data from a file without any predefined schema
graph = JanusGraphFactory.open('conf/janusgraph-berkeleyje.properties')
g = graph.traversal()
m = graph.openManagement()
m.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.LIST).make()
m.makePropertyKey('lang').dataType(String.class).cardinality(Cardinality.LIST).make()
m.makePropertyKey('age').dataType(Integer.class).cardinality(Cardinality.LIST).make()
m.commit()
graph.io(IoCore.gryo()).readGraph('data/tinkerpop-modern.gio')
graph.tx().commit()
// Run a query -- note the planner warning recommending the use of an index
g.V().has('name', 'lop')
graph.tx().rollback()
// Create an index
m = graph.openManagement()
m.buildIndex('names', Vertex.class).addKey(m.getPropertyKey('name')).buildCompositeIndex()
m.commit()
graph.tx().commit()
// Block until the SchemaStatus transitions from INSTALLED to REGISTERED
ManagementSystem.awaitGraphIndexStatus(graph, 'names').status(SchemaStatus.REGISTERED).call()
// Reindex using JanusGraphManagement
m = graph.openManagement()
i = m.getGraphIndex('names')
m.updateIndex(i, SchemaAction.REINDEX)
m.commit()
// Enable the index
ManagementSystem.awaitGraphIndexStatus(graph, 'names').status(SchemaStatus.ENABLED).call()
// Run a query -- JanusGraph will use the new index, no planner warning
g.V().has('name', 'lop')
graph.tx().rollback()
// Concerned that JanusGraph could have read cache in that last query, instead of relying on the index?
// Start a new instance to rule out cache hits.  Now we're definitely using the index.
graph.close()
graph = JanusGraphFactory.open("conf/janusgraph-berkeleyje.properties")
g = graph.traversal()
g.V().has('name', 'lop')
```

## 常见问题 Common problems

### IllegalArgumentException when starting job
When a reindexing job is started shortly after a the index has been built, the job might fail with an exception like one of the following:

在建立索引后不久开始重新建立索引的作业时，该作业可能会失败，并出现以下异常之一：
```java
The index mixedExample is in an invalid state and cannot be indexed.
The following index keys have invalid status: desc has status INSTALLED
(status must be one of [REGISTERED, ENABLED])
The index mixedExample is in an invalid state and cannot be indexed.
The index has status INSTALLED, but one of [REGISTERED, ENABLED] is required
```
When an index is built, its existence is broadcast to all other JanusGraph instances in the cluster. Those must acknowledge the existence of the index before the reindexing process can be started. The acknowledgments can take a while to come in depending on the size of the cluster and the connection speed. Hence, one should wait a few minutes after building the index and before starting the reindex process.

构建索引后，其存在将广播到集群中的所有其他 JanusGraph 实例。 必须先确认索引的存在，然后才能开始重新索引过程。 确认可能要花一些时间，具体取决于群集的大小和连接速度。 因此，在建立索引之后和开始重新索引过程之前，应该等待几分钟。

Note, that the acknowledgment might fail due to JanusGraph instance failure. In other words, the cluster might wait indefinitely on the acknowledgment of a failed instance. In this case, the user must manually remove the failed instance from the cluster registry as described in [Failure & Recovery](https://docs.janusgraph.org/advanced-topics/recovery/). After the cluster state has been restored, the acknowledgment process must be reinitiated by manually registering the index again in the management system.

请注意，由于JanusGraph实例故障，确认可能会失败。 换句话说，群集可能会无限期地等待失败实例的确认。 在这种情况下，用户必须按照故障与恢复中的说明从群集注册表中手动删除失败的实例。 恢复群集状态后，必须通过在管理系统中再次手动注册索引来重新启动确认过程。
```
mgmt = graph.openManagement()
rindex = mgmt.getRelationIndex(mgmt.getRelationType("battled"),"battlesByTime")
mgmt.updateIndex(rindex, SchemaAction.REGISTER_INDEX).get()
gindex = mgmt.getGraphIndex("byName")
mgmt.updateIndex(gindex, SchemaAction.REGISTER_INDEX).get()
mgmt.commit()
```
After waiting a few minutes for the acknowledgment to arrive the reindex job should start successfully.
<a name="could-not-find-index"></a>
### Could not find index
This exception in the reindexing job indicates that an index with the given name does not exist or that the name has not been specified correctly. When reindexing a global graph index, only the name of the index as defined when building the index should be specified. When reindexing a global graph index, the name of the index must be given in addition to the name of the edge label or property key on which the vertex-centric index is defined.

重新索引作业中的此异常表明，具有给定名称的索引不存在，或者未正确指定名称。 重新索引全局图索引时，仅应指定在构建索引时定义的索引名称。 当重新索引全局图索引时，除了在其上定义以顶点为中心的索引的边标签或属性键的名称之外，还必须提供索引的名称。
<a name="cassandra-mappers-fail-with-too-many-open-files"></a>
### Cassandra Mappers Fail with "Too many open files"
The end of the exception stacktrace may look like this:
```java
java.net.SocketException: Too many open files
    at java.net.Socket.createImpl(Socket.java:447)
    at java.net.Socket.getImpl(Socket.java:510)
    at java.net.Socket.setSoLinger(Socket.java:988)
    at org.apache.thrift.transport.TSocket.initSocket(TSocket.java:118)
    at org.apache.thrift.transport.TSocket.<init>(TSocket.java:109)
```
When running Cassandra with virtual nodes enabled, the number of virtual nodes seems to set a floor under the number of mappers. Cassandra may generate more mappers than virtual nodes for clusters with lots of data, but it seems to generate at least as many mappers as there are virtual nodes even though the cluster might be empty or close to empty. The default is 256 as of this writing.

Each mapper opens and quickly closes several sockets to Cassandra. The kernel on the client side of those closed sockets goes into asynchronous TIME_WAIT, since Thrift uses SO_LINGER. Only a small number of sockets are open at any one time — usually low single digits — but potentially many lingering sockets can accumulate in TIME_WAIT. This accumulation is most pronounced when running a reindex job locally (not on a distributed MapReduce cluster), since all of those client-side TIME_WAIT sockets are lingering on a single client machine instead of being spread out across many machines in a cluster. Combined with the floor of 256 mappers, a reindex job can open thousands of sockets of the course of its execution. When these sockets all linger in TIME_WAIT on the same client, they have the potential to reach the open-files ulimit, which also controls the number of open sockets. The open-files ulimit is often set to 1024.

Here are a few suggestions for dealing with the "Too many open files" problem during reindexing on a single machine:

- Reduce the maximum size of the Cassandra connection pool. For example, consider setting the cassandrathrift storage backend’s `max-active` and `max-idle` options to 1 each, and setting `max-total` to -1. See [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) for full listings of connection pool settings on the Cassandra storage backends.
- Increase the `nofile` ulimit. The ideal value depends on the size of the Cassandra dataset and the throughput of the reindex mappers; if starting at 1024, try an order of magnitude larger: 10000. This is just necessary to sustain lingering TIME_WAIT sockets. The reindex job won’t try to open nearly that many sockets at once.
- Run the reindex task on a multi-node MapReduce cluster to spread out the socket load.



在启用了虚拟节点的情况下运行Cassandra时，虚拟节点的数量似乎在映射器数量的下方设置了下限。对于具有大量数据的集群，Cassandra可能比虚拟节点生成更多的映射器，但是，即使集群可能是空的或接近于空的，Cassandra生成的映射器也至少与虚拟节点一样多。在撰写本文时，默认值为256。

每个映射器都会打开，并迅速关闭Cassandra的多个套接字。由于Thrift使用SO_LINGER，这些封闭套接字的客户端上的内核将进入异步TIME_WAIT。任一时间只有很少的套接字打开-通常是低个位数-但在TIME_WAIT中可能会累积许多滞留的套接字。当在本地（而不是在分布式MapReduce群集上）运行重新索引作业时，这种累积最为明显，因为所有这些客户端TIME_WAIT套接字都在单个客户端计算机上徘徊，而不是分散在群集中的许多计算机上。再加上256个映射器的地板，重新索引作业可以在执行过程中打开数千个套接字。当这些套接字都在同一客户端上的TIME_WAIT中徘徊时，它们就有可能到达打开文件ulimit，这也控制着打开套接字的数量。打开文件的ulimit通常设置为1024。

以下是在单台计算机上建立索引期间如何处理“打开的文件过多”问题的一些建议：

- 减小Cassandra连接池的最大大小。例如，考虑将cassandrathrift存储后端的max-active和max-idle选项分别设置为1，并将max-total设置为-1。有关Cassandra存储后端上连接池设置的完整列表，请参阅配置参考。
- 增加nofile ulimit。理想值取决于Cassandra数据集的大小和重新索引映射器的吞吐量。如果从1024开始，请尝试大一个数量级：10000。这仅是维持持久的TIME_WAIT套接字所必需的。重新索引作业不会尝试一次打开几乎那么多的插槽。
- 在多节点MapReduce群集上运行reindex任务以分散套接字负载。
