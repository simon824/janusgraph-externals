## JanusGraph的好处 (The Benefits of JanusGraph)

JanusGraph is designed to support the processing of graphs so large that
they require storage and computational capacities beyond what a single
machine can provide. Scaling graph data processing for real time
traversals and analytical queries is JanusGraph’s foundational benefit.
This section will discuss the various specific benefits of JanusGraph
and its underlying, supported persistence solutions.
JanusGraph的设计目的是支持处理庞大的图形，这些图形需要的存储和计算能力超出了一台机器所能提供的能力。扩展实时遍历和分析查询的图形数据处理是JanusGraph的基本优点。本节将讨论JanusGraph的各种具体好处及其底层支持的持久性解决方案。

### General JanusGraph Benefits

- Support for very large graphs. JanusGraph graphs scale with the
    number of machines in the cluster.
- Support for very many concurrent transactions and operational graph
    processing. JanusGraph’s transactional capacity scales with the
    number of machines in the cluster and answers complex traversal
    queries on huge graphs in milliseconds.
- Support for global graph analytics and batch graph processing
    through the Hadoop framework.
- Support for geo, numeric range, and full text search for vertices
    and edges on very large graphs.
- Native support for the popular property graph data model exposed by
    [Apache TinkerPop](https://tinkerpop.apache.org/).
- Native support for the graph traversal language
    [Gremlin](https://tinkerpop.apache.org/gremlin.html).
  - Numerous graph-level configurations provide knobs for tuning
    performance.
- Vertex-centric indices provide vertex-level querying to alleviate
    issues with the infamous [super node problem](http://thinkaurelius.com/2012/10/25/a-solution-to-the-supernode-problem/).
- Provides an optimized disk representation to allow for efficient use
    of storage and speed of access.
- Open source under the liberal [Apache 2 license](https://en.wikipedia.org/wiki/Apache_License).

- 支持非常大的图形。JanusGraph图会随着集群中机器的数量而缩放。
- 支持大量并发事务和操作图处理。JanusGraph的事务处理能力随着集群中机器的数量而增加，并在毫秒内回答大型图上的复杂遍历查询。
- 通过Hadoop框架支持全局图分析和批处理图。
- 支持geo，数字范围，和非常大的图形上的顶点和边的全文搜索。
- 本机支持Apache TinkerPop公开的流行属性图数据模型。
- 本机支持图遍历语言Gremlin。
- 许多图形级配置提供了用于优化性能的旋钮。
- 以顶点为中心的索引提供顶点级查询，以缓解臭名昭著的超级节点问题。
- 提供优化的磁盘表示形式，以便有效地使用存储和访问速度。
- 自由的Apache 2许可下的开源。
- JanusGraph与Apache Cassandra的好处

### Benefits of JanusGraph with Apache Cassandra

<div style="float: right;">
    <img src="../images/cassandra-logo.svg">
</div>

-   [Continuously available](https://en.wikipedia.org/wiki/Continuous_availability)
    with no single point of failure.
-   No read/write bottlenecks to the graph as there is no master/slave
    architecture.

-   [Elastic scalability](https://en.wikipedia.org/wiki/Elastic_computing) allows
    for the introduction and removal of machines.
-   Caching layer ensures that continuously accessed data is available
    in memory.
-   Increase the size of the cache by adding more machines to the
    cluster.
-   Integration with [Apache Hadoop](https://hadoop.apache.org/).
-   Open source under the liberal Apache 2 license.


- 持续可用，没有单点故障。
- 图中没有读写瓶颈，因为没有主/从架构。
- 弹性的可伸缩性允许引入和移除机器。
- 缓存层确保持续访问的数据在内存中可用。
- 通过向集群中添加更多的机器来增加缓存的大小。
- 与Apache Hadoop集成。
- 自由的Apache 2许可下的开源。


### JanusGraph与HBase的好处 Benefits of JanusGraph with HBase

<div style="float: right;">
    <img src="https://hbase.apache.org/images/hbase_logo.png">
</div>

-   Tight integration with the [Apache Hadoop](https://hadoop.apache.org/) ecosystem.
-   Native support for [strong consistency](https://en.wikipedia.org/wiki/Strong_consistency).
-   Linear scalability with the addition of more machines.
-   [Strictly consistent](https://en.wikipedia.org/wiki/Strict_consistency) reads and writes.
-   Convenient base classes for backing Hadoop
    [MapReduce](https://en.wikipedia.org/wiki/MapReduce) jobs with HBase
    tables.
-   Support for exporting metrics via
    [JMX](https://en.wikipedia.org/wiki/Java_Management_Extensions).
-   Open source under the liberal Apache 2 license.

- 与Apache Hadoop生态系统紧密集成。
- 本机支持强一致性。
- 增加更多机器的线性可伸缩性。
- 读写严格一致。
- 方便的基类支持Hadoop MapReduce作业与HBase表。
- 支持通过JMX导出指标。
- 自由的Apache 2许可下的开源。

### JanusGraph and the CAP Theorem

> Despite your best efforts, your system will experience enough faults
> that it will have to make a choice between reducing yield (i.e., stop
> answering requests) and reducing harvest (i.e., giving answers based
> on incomplete data). This decision should be based on business
> requirements.
> 尽管您尽了最大的努力，您的系统还是会遇到足够多的错误，它将不得不在减产(即停止回应请求)和减产(即基于不完整的数据给出答案)之间做出选择。该决策应该基于业务需求。
>
> —  [Coda Hale](https://codahale.com/you-cant-sacrifice-partition-tolerance)

When using a database, the [CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem) should be thoroughly
considered (C=Consistency, A=Availability, P=Partitionability).
JanusGraph is distributed with 3 supporting backends: [Apache Cassandra](https://cassandra.apache.org/),
 [Apache HBase](https://hbase.apache.org/), and [Oracle Berkeley DB Java Edition](https://www.oracle.com/technetwork/database/berkeleydb/overview/index-093405.html).
Note that BerkeleyDB JE is a non-distributed database and is typically
only used with JanusGraph for testing and exploration purposes.

HBase gives preference to consistency at the expense of yield, i.e. the
probability of completing a request. Cassandra gives preference to
availability at the expense of harvest, i.e. the completeness of the
answer to the query (data available/complete data).

在使用数据库时，应该充分考虑CAP定理(C=一致性，a =可用性，P=可分割性)。JanusGraph分布有3个支持后端:Apache Cassandra、Apache HBase和Oracle Berkeley DB Java Edition。注意，BerkeleyDB JE是一个非分布式数据库，通常只与JanusGraph一起用于测试和探索目的。

HBase优先考虑一致性，以牺牲yield为代价，即完成请求的概率。Cassandra优先考虑可用性，而牺牲了获取，即查询答案的完整性(可用数据/完整数据)。
