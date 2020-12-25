# 常见问题 Common Questions 

## 意外类型创建 Accidental type creation 
By default, JanusGraph will automatically create property keys and edge labels when a new type is encountered. It is strongly encouraged that users explicitly schemata as documented in [Schema and Data Modeling](https://docs.janusgraph.org/basics/schema/) before loading any data and disable automatic type creation by setting the option `schema.default = none`.

Automatic type creation can cause problems in multi-threaded or highly concurrent environments. Since JanusGraph needs to ensure that types are unique, multiple attempts at creating the same type will lead to locking or other exceptions. It is generally recommended to create all needed types up front or in one batch when new property keys and edge labels are needed.

默认情况下，JanusGraph将在遇到新类型时自动创建属性键和边缘标签。强烈建议用户在加载任何数据之前通过Schema和Data Modeling中明确记录的schemata，并通过设置option schema.default = none禁用自动类型创建。

自动类型创建会在多线程或高度并发的环境中引起问题。由于JanusGraph需要确保类型是唯一的，因此多次尝试创建同一类型将导致锁定或其他异常。通常建议在需要新的属性键和边缘标签时，先创建所有必需的类型，或批量创建所有类型。

## 自定义类数据类型 Custom Class Datatype 
JanusGraph supports arbitrary objects as attribute values on properties. To use a custom class as data type in JanusGraph, either register a custom serializer or ensure that the class has a no-argument constructor and implements the `equals` method because JanusGraph will verify that it can successfully de-/serialize objects of that class. Please see [Datatype and Attribute Serializer Configuration](https://docs.janusgraph.org/advanced-topics/serializer/) for more information.

JanusGraph支持将任意对象用作属性的属性值。要将自定义类用作JanusGraph中的数据类型，请注册自定义序列化程序或确保该类具有无参数构造函数并实现equals方法，因为JanusGraph会验证其是否可以成功对该类的对象进行反序列化。请参阅数据类型和属性序列化程序配置以获取更多信息。

## Edges的事务范围 Transactional Scope for Edges 
Edges should not be accessed outside the scope in which they were originally created or retrieved.

不应在最初创建或检索边缘的范围之外访问边缘。

## 锁定异常 Locking Exceptions 
When defining unique types with [locking enabled](https://docs.janusgraph.org/advanced-topics/eventual-consistency/) (i.e. requesting that JanusGraph ensures uniqueness) it is likely to encounter locking exceptions of the type `PermanentLockingException` under concurrent modifications to the graph.

Such exceptions are to be expected, since JanusGraph cannot know how to recover from a transactional state where an earlier read value has been modified by another transaction since this may invalidate the state of the transaction. In most cases it is sufficient to simply re-run the transaction. If locking exceptions are very frequent, try to analyze and remove the source of congestion.

在启用锁定的情况下定义唯一类型时（即要求JanusGraph确保唯一性），在对图进行并发修改时，很可能会遇到PermanentLockingException类型的锁定异常。

由于JanusGraph无法知道如何从另一个交易修改了较早读取值的交易状态中恢复，因此可能会出现此类异常，因为这可能会使交易状态无效。在大多数情况下，只需重新运行事务就足够了。如果锁定异常非常常见，请尝试分析并消除拥塞的根源。

## 幽灵顶点 Ghost Vertices 
When the same vertex is concurrently removed in one transaction and modified in another, both transactions will successfully commit on eventually consistent storage backends and the vertex will still exist with only the modified properties or edges. This is referred to as a ghost vertex. It is possible to guard against ghost vertices on eventually consistent backends using key [uniqueness](https://docs.janusgraph.org/basics/common-questions/#index-unique) but this is prohibitively expensive in most cases. A more scalable approach is to allow ghost vertices temporarily and clearing them out in regular time intervals.

Another option is to detect them at read-time using the option `checkInternalVertexExistence()` documented in [Transaction Configuration](https://docs.janusgraph.org/basics/common-questions/#tx-config).

当在一个事务中同时删除同一顶点并在另一事务中对其进行修改时，两个事务将成功地提交到最终一致的存储后端，并且该顶点将仍然存在，仅具有修改后的属性或边。这称为重影顶点。可以使用密钥唯一性来防止最终一致的后端上的重影顶点，但是在大多数情况下，这是非常昂贵的。一种更具可扩展性的方法是暂时允许重影顶点并以规则的时间间隔清除它们。

另一个选择是使用“事务配置”中记录的checkInternalVertexExistence()选项在读取时检测它们

## 调试级别的日志记录会降低执行速度 Debug-level Logging Slows Execution 
When the log level is set to `DEBUG` JanusGraph produces **a lot** of logging output which is useful to understand how particular queries get compiled, optimized, and executed. However, the output is so large that it will impact the query performance noticeably. Hence, use `INFO` severity or higher for production systems or benchmarking.

当日志级别设置为DEBUG时，JanusGraph会生成大量日志输出，这对于了解如何编译，优化和执行特定查询很有用。但是，输出太大，以至于将显着影响查询性能。因此，对生产系统或基准测试使用INFO严重性或更高级别。

## 内存溢出和垃圾回收异常 JanusGraph OutOfMemoryException or excessive Garbage Collection 
If you experience memory issues or excessive garbage collection while running JanusGraph it is likely that the caches are configured incorrectly. If the caches are too large, the heap may fill up with cache entries. Try reducing the size of the transaction level cache before tuning the database level cache, in particular if you have many concurrent transactions. See [JanusGraph Cache](https://docs.janusgraph.org/basics/cache/) for more information.

如果在运行JanusGraph时遇到内存问题或过多的垃圾回收，则可能是缓存配置不正确。如果缓存太大，则堆可能会填满缓存条目。在调整数据库级缓存之前，请尝试减小事务级缓存的大小，尤其是在您有多个并发事务的情况下。有关更多信息，请参见JanusGraph Cache。

## JAMM警告消息 JAMM Warning Messages 
When launching JanusGraph with embedded Cassandra, the following warnings may be displayed:

当启动带有嵌入式Cassandra的JanusGraph时，可能会显示以下警告：

`958 [MutationStage:25] WARN org.apache.cassandra.db.Memtable - MemoryMeter uninitialized (jamm not specified as java agent); assuming liveRatio of 10.0. Usually this means cassandra-env.sh disabled jamm because you are using a buggy JRE; upgrade to the Sun JRE instead`

Cassandra uses a Java agent called `MemoryMeter` which allows it to measure the actual memory use of an object, including JVM overhead. To use [JAMM](https://github.com/jbellis/jamm) (Java Agent for Memory Measurements), the path to the JAMM jar must be specific in the Java javaagent parameter when launching the JVM (e.g. `-javaagent:path/to/jamm.jar`) through either `janusgraph.sh`, `gremlin.sh`, or Gremlin Server:

Cassandra使用一个称为MemoryMeter的Java代理，该代理允许它测量对象的实际内存使用情况，包括JVM开销。要使用JAMM（用于内存测量的Java代理），在通过janusgraph.sh，gremlin启动JVM（例如-javaagent：path / to / jamm.jar）时，JAMM jar的路径必须在Java javaagent参数中特定。 sh或Gremlin Server：
```
export JANUSGRAPH_JAVA_OPTS=-javaagent:$JANUSGRAPH_HOME/lib/jamm-0.3.0.jar
```

## Cassandra Connection Problem 连接问题
By default, JanusGraph uses the Astyanax library to connect to Cassandra clusters. On EC2 and Rackspace, it has been reported that Astyanax was unable to establish a connection to the cluster. In those cases, changing the backend to `storage.backend=cassandrathrift` solved the problem.

默认情况下，JanusGraph使用Astyanax库连接到Cassandra群集。在EC2和Rackspace上，据报道Astyanax无法建立与集群的连接。在这些情况下，将后端更改为storage.backend = cassandrathrift解决了该问题。

## Elasticsearch OutOfMemoryException
When numerous clients are connecting to Elasticsearch, it is likely that an `OutOfMemoryException` occurs. This is not due to a memory issue, but to the OS not allowing more threads to be spawned by the user (the user running Elasticsearch). To circumvent this issue, increase the number of allowed processes to the user running Elasticsearch. For example, increase the `ulimit -u` from the default 1024 to 10024.

当大量客户端连接到Elasticsearch时，很可能发生OutOfMemoryException。这不是由于内存问题，而是由于操作系统不允许用户（运行Elasticsearch的用户）产生更多线程。为避免此问题，请增加运行Elasticsearch的用户允许的进程数。例如，将ulimit -u从默认的1024增加到10024。

## Dropping a Database
To drop a database using the Gremlin Console you can call `JanusGraphFactory.drop(graph)`. The graph you want to drop needs to be defined prior to running the drop method.

要使用Gremlin Console删除数据库，可以调用JanusGraphFactory.drop（graph）。在运行drop方法之前，需要先定义要删除的图形。

With ConfiguredGraphFactory
```
graph = ConfiguredGraphFactory.open('example')
ConfiguredGraphFactory.drop('example');
```
With JanusGraphFactory
```
graph = JanusGraphFactory.open('path/to/configuration.properties')
JanusGraphFactory.drop(graph);
```
Note that on JanusGraph versions prior to 0.3.0 if multiple Gremlin Server instances are connecting to the graph that has been dropped it is recommended to close the graph on all active nodes by running either `JanusGraphFactory.close(graph)` or `ConfiguredGraphFactory.close("example")` depending on which graph manager is in use. Closing and reopening the graph on all active nodes will prevent cached(stale) references to the graph that has been dropped. ConfiguredGraphFactory graphs that are dropped may need to have their configurations recreated using the [graph configuration singleton](https://docs.janusgraph.org/basics/configured-graph-factory/#graph-configurations) or [template configuration](https://docs.janusgraph.org/basics/configured-graph-factory/#template-configuration).

请注意，在0.3.0之前的JanusGraph版本上，如果多个Gremlin Server实例正在连接到已删除的图，建议通过运行JanusGraphFactory.close（graph）或ConfiguredGraphFactory.close（“关闭所有活动节点上的图。示例”），具体取决于所使用的图形管理器。在所有活动节点上关闭并重新打开图将防止对已删除图的缓存（陈旧）引用。删除的ConfiguredGraphFactory图形可能需要使用图形配置单例或模板配置来重新创建其配置。
