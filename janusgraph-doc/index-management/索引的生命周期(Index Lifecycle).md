# 索引的生命周期 Index Lifecycle

JanusGraph uses only indexes which have status `ENABLED`. When the index is created it will not be used by JanusGraph until it is enabled. After the index is build you should wait until it is registered (i.e. available) by JanusGraph:

JanusGraph仅使用状态为ENABLED的索引。 创建索引后，直到启用索引，JanusGraph才会使用它。 建立索引后，您应该等待直到JanusGraph注册（即可用）为止：
```java
//Wait for the index to become available (i.e. wait for status REGISTERED)
ManagementSystem.awaitGraphIndexStatus(graph, "myIndex").call();
```
After the index is registered we should either enable the index (if we are sure that the current data should not be indexed by the newly created index) or we should reindex current data so that it would be available in the newly created index.

注册索引后，我们应该启用索引（如果我们确定当前数据不应该由新创建的索引建立索引），或者应该重新索引当前数据，以便在新创建的索引中可用。

重新索引现有数据并自动启用索引示例：
```
mgmt = graph.openManagement();
mgmt.updateIndex(mgmt.getGraphIndex("myIndex"), SchemaAction.REINDEX).get();
mgmt.commit();
```
Enable the index without reindexing existing data example:

启用索引而不重新索引现有数据示例：
```
mgmt = graph.openManagement();
mgmt.updateIndex(mgmt.getGraphIndex("myAnotherIndex"), SchemaAction.ENABLE_INDEX).get();
mgmt.commit();
```

## Index states and transitions
![](https://cdn.nlark.com/yuque/0/2020/png/1209774/1604281388710-6d1d282f-508d-45f8-a042-710c0460fcc2.png#align=left&display=inline&height=370&margin=%5Bobject%20Object%5D&originHeight=370&originWidth=399&size=0&status=done&style=none&width=399)

## States (SchemaStatus) 状态
An index can be in one of the following states:

- **INSTALLED** The index is installed in the system but not yet registered with all instances in the cluster
- **REGISTERED** The index is registered with all instances in the cluster but not (yet) enabled
- **ENABLED** The index is enabled and in use
- **DISABLED** The index is disabled and no longer in use

索引可以处于以下状态之一：
- **INSTALLED **已安装在系统中，但尚未在集群中的所有实例中注册
- **REGISTERED **已在集群中的所有实例中注册，但尚未启用
- **ENABLED **已启用并正在使用
- **DISABLED** 索引已禁用，不再使用

## Actions (SchemaAction)
The following actions can be performed on an index to change its state via `mgmt.updateIndex()`:

- **REGISTER_INDEX** Registers the index with all instances in the graph cluster. After an index is installed, it must be registered with all graph instances
- **REINDEX** Re-builds the index from the graph
- **ENABLE_INDEX** Enables the index so that it can be used by the query processing engine. An index must be registered before it can be enabled.
- **DISABLE_INDEX** Disables the index in the graph so that it is no longer used.
- **REMOVE_INDEX** Removes the index from the graph (optional operation). Only on composite index.

可以对索引执行以下操作，以通过mgmt.updateIndex（）更改其状态：
- REGISTER_INDEX 向图集群中的所有实例注册索引。 安装索引后，必须将其注册到所有图形实例
- REINDEX 从图重建索引
- ENABLE_INDEX 启用索引，以便查询处理引擎可以使用它。 必须先注册索引，然后才能启用它。
- DISABLE_INDEX 禁用图形中的索引，以便不再使用它。
- REMOVE_INDEX 从图形中删除索引（可选操作）。 仅在复合索引上。
