# 架构总览 Architectural Overview

JanusGraph is a graph database engine. JanusGraph itself is focused on compact graph serialization, rich graph data modeling, and efficient query execution. In addition, JanusGraph utilizes Hadoop for graph analytics and batch graph processing. JanusGraph implements robust, modular interfaces for data persistence, data indexing, and client access. JanusGraph’s modular architecture allows it to interoperate with a wide range of storage, index, and client technologies; it also eases the process of extending JanusGraph to support new ones.

JanusGraph 是一个图数据库引擎，它本身专注于压缩图序列化，丰富的图数据建模，和有效的查询执行。此外，JanusGraph 利用 Hadoop 进行图分析和批量图处理。JanusGraph 为数据持久层，数据索引和客户端访问实现了强大的模块化接口。JanusGraph 的模块化架构使其可以与多种存储，索引和客户端技术进行交互，它还简化了扩展 JanusGraph 以支持新技术的过程。

Between JanusGraph and the disks sits one or more storage and indexing adapters. JanusGraph comes standard with the following adapters, but JanusGraph’s modular architecture supports third-party adapters.

- Data storage:

  - [Apache Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
  - [Apache HBase](https://docs.janusgraph.org/storage-backend/hbase/)
  - [Oracle Berkeley DB Java Edition](https://docs.janusgraph.org/storage-backend/bdb/)

- Indices, which speed up and enable more complex queries:

  - [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
  - [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
  - [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)

JanusGraph 和磁盘之间有一个或多个存储和索引适配器。JanusGraph 标配有以下适配器，但它的模块化架构使得它也能支持第三方适配器。

- 数据存储:

  - [Apache Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
  - [Apache HBase](https://docs.janusgraph.org/storage-backend/hbase/)
  - [Oracle Berkeley DB Java Edition](https://docs.janusgraph.org/storage-backend/bdb/)

- 索引，可以加快查询速度并允许更复杂的查询:

  - [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
  - [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
  - [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)

Broadly speaking, applications can interact with JanusGraph in two ways:

- Embed JanusGraph inside the application executing [Gremlin](https://tinkerpop.apache.org/docs/3.4.6/reference/#graph-traversal-steps) queries directly against the graph within the same JVM. Query execution, JanusGraph’s caches, and transaction handling all happen in the same JVM as the application while data retrieval from the storage backend may be local or remote.

- Interact with a local or remote JanusGraph instance by submitting Gremlin queries to the server. JanusGraph natively supports the Gremlin Server component of the [Apache TinkerPop](https://tinkerpop.apache.org/) stack.

广义上来讲，应用程序可以通过两种方式与 JanusGraph 交互：

- 将 JanusGraph 嵌入到应用程序中在同一JVM中直接执行[Gremlin](https://tinkerpop.apache.org/docs/3.4.6/reference/#graph-traversal-steps)查询。查询执行，JanusGraph 缓存和事务处理都跟应用程序在同一个JVM中进行，而从存储后端检索的数据可能是本地的或远程的。

- 通过将 Gremlin 查询提交到服务器来跟本地或远程的 JanusGraph 实例进行交互。JanusGraph 原生支持 [Apache TinkerPop](https://tinkerpop.apache.org/) 技术栈中的 Gremlin Server 组件。

![JanusGraph Architectural Overview](/images/janusgraph_architectural_overview.png)
