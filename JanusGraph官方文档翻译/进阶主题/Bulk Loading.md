# Bulk Loading

<a name="bulk-loading"></a>
# Bulk Loading
There are a number of configuration options and tools that make ingesting large amounts of graph data into JanusGraph more efficient. Such ingestion is referred to as _bulk loading_ in contrast to the default _transactional loading_ where small amounts of data are added through individual transactions.<br />There are a number of use cases for bulk loading data into JanusGraph, including:

- Introducing JanusGraph into an existing environment with existing data and migrating or duplicating this data into a new JanusGraph cluster.<br />
- Using JanusGraph as an end point of an [ETL](https://en.wikipedia.org/wiki/Extract,_transform,_load) process.<br />
- Adding an existing or external graph datasets (e.g. publicly available [RDF datasets](http://linkeddata.org/)) to a running JanusGraph cluster.<br />
- Updating a JanusGraph graph with results from a graph analytics job.<br />

This page describes configuration options and tools that make bulk loading more efficient in JanusGraph. Please observe the limitations and assumptions for each option carefully before proceeding to avoid data loss or data corruption.<br />This documentation focuses on JanusGraph specific optimization. In addition, consider improving the chosen storage backend and (optional) index backend for high write performance. Please refer to the documentation of the respective backend for more information.<br />
<br />有许多配置选项和工具可以使向JanusGraph提取大量图形数据更加高效。与通过单个事务添加少量数据的默认事务加载相比，这种提取称为批量加载。<br />将数据批量加载到JanusGraph中有许多用例，包括：

- 将JanusGraph引入具有现有数据的现有环境中，并将此数据迁移或复制到新的JanusGraph集群中。
- 使用JanusGraph作为ETL过程的终点。
- 将现有或外部图形数据集（例如，公开可用的RDF数据集）添加到正在运行的JanusGraph集群中。
- 使用图分析作业的结果更新JanusGraph图。

该页面介绍了配置选项和工具，这些选项和工具使JanusGraph中的批量加载更加高效。在继续操作之前，请仔细遵守每个选项的限制和假设，以免丢失数据或损坏数据。<br />本文档重点介绍JanusGraph特定的优化。另外，请考虑改进所选的存储后端和（可选）索引后端以提高写入性能。有关更多信息，请参阅相应后端的文档。
<a name="configuration-options"></a>
## Configuration Options
<a name="batch-loading"></a>
### Batch Loading
Enabling the `storage.batch-loading` configuration option will have the biggest positive impact on bulk loading times for most applications. Enabling batch loading disables JanusGraph internal consistency checks in a number of places. Most importantly, it disables locking. In other words, JanusGraph assumes that the data to be loaded into JanusGraph is consistent with the graph and hence disables its own checks in the interest of performance.<br />In many bulk loading scenarios it is significantly cheaper to ensure data consistency prior to loading the data then ensuring data consistency while loading it into the database. The `storage.batch-loading` configuration option exists because of this observation.<br />For example, consider the use case of bulk loading existing user profiles into JanusGraph. Furthermore, assume that the username property key has a unique composite index defined on it, i.e. usernames must be unique across the entire graph. If the user profiles are imported from another database, username uniqueness might already guaranteed. If not, it is simple to sort the profiles by name and filter out duplicates or writing a Hadoop job that does such filtering. Now, we can enable `storage.batch-loading` which significantly reduces the bulk loading time because JanusGraph does not have to check for every added user whether the name already exists in the database.<br />**Important**: Enabling `storage.batch-loading` requires the user to ensure that the loaded data is internally consistent and consistent with any data already in the graph. In particular, concurrent type creation can lead to severe data integrity issues when batch loading is enabled. Hence, we **strongly** encourage disabling automatic type creation by setting `schema.default = none` in the graph configuration.<br />启用storage.batch-loading配置选项将对大多数应用程序的批量加载时间产生最大的积极影响。启用批处理加载会在许多地方禁用JanusGraph内部一致性检查。最重要的是，它禁用锁定。换句话说，JanusGraph假定要加载到JanusGraph中的数据与图形一致，因此出于性能考虑禁用了自己的检查。<br />在许多批量加载方案中，在加载数据之前确保数据一致性，然后在将数据加载到数据库中时确保数据一致性，要便宜得多。由于这种观察，存在storage.batch-loading配置选项。<br />例如，考虑将现有用户配置文件批量加载到JanusGraph中的用例。此外，假设用户名属性键具有定义的唯一复合索引，即用户名在整个图中必须是唯一的。如果用户配置文件是从另一个数据库导入的，则用户名唯一性可能已经得到保证。如果没有，那么很容易按名称对配置文件进行排序并过滤出重复项，或者编写执行此类过滤的Hadoop作业。现在，我们可以启用storage.batch-loading，这将大大减少批量加载时间，因为JanusGraph不必检查每个添加的用户该名称是否已存在于数据库中。<br />重要提示：启用storage.batch-loading要求用户确保所加载的数据在内部是一致的，并且与图中已存在的任何数据一致。特别是，启用批处理加载时，并发类型创建会导致严重的数据完整性问题。因此，我们强烈建议通过在图形配置中设置schema.default = none来禁用自动类型创建。
<a name="optimizing-id-allocation"></a>
### Optimizing ID Allocation 优化ID分配
<a name="id-block-size"></a>
#### ID Block Size
Each newly added vertex or edge is assigned a unique id. JanusGraph’s id pool manager acquires ids in blocks for a particular JanusGraph instance. The id block acquisition process is expensive because it needs to guarantee globally unique assignment of blocks. Increasing `ids.block-size` reduces the number of acquisitions but potentially leaves many ids unassigned and hence wasted. For transactional workloads the default block size is reasonable, but during bulk loading vertices and edges are added much more frequently and in rapid succession. Hence, it is generally advisable to increase the block size by a factor of 10 or more depending on the number of vertices to be added per machine.<br />每个新添加的顶点或边都分配有唯一的ID。 JanusGraph的ID池管理器以块为单位获取特定JanusGraph实例的ID。 id块获取过程很昂贵，因为它需要保证块的全局唯一分配。 ids.block-size的增加会减少获取次数，但可能会使许多id未被分配，从而造成浪费。对于事务性工作负载，默认块大小是合理的，但是在批量加载期间，顶点和边的添加要频繁得多，而且要快速连续。因此，通常建议将块大小增加10倍或更多，具体取决于每台机器要添加的顶点数量。<br />**Rule of thumb**: Set `ids.block-size` to the number of vertices you expect to add per JanusGraph instance per hour.<br />**Important:** All JanusGraph instances MUST be configured with the same value for `ids.block-size` to ensure proper id allocation. Hence, be careful to shut down all JanusGraph instances prior to changing this value.<br />**经验法则：**将ids.block-size设置为您希望每小时为每个JanusGraph实例添加的顶点数。<br />**重要提示：**所有JanusGraph实例必须为ids.block-size配置相同的值，以确保正确的id分配。因此，在更改此值之前，请小心关闭所有JanusGraph实例。
<a name="id-acquisition-process"></a>
#### ID Acquisition Process 身份证获取流程
When id blocks are frequently allocated by many JanusGraph instances in parallel, allocation conflicts between instances will inevitably arise and slow down the allocation process. In addition, the increased write load due to bulk loading may further slow down the process to the point where JanusGraph considers it failed and throws an exception. There are three configuration options that can be tuned to avoid this.<br />当许多JanusGraph实例频繁并行分配id块时，不可避免地会出现实例之间的分配冲突，从而减慢了分配过程。另外，由于大容量加载而导致的增加的写入负载可能会使该过程进一步减慢到JanusGraph认为失败并引发异常的地步。可以调整三个配置选项来避免这种情况。<br />1）ids.authority.wait-time配置以毫秒为单位的时间，id池管理器等待存储后端确认ID块应用程序的时间。这段时间越短，应用程序在拥挤的存储群集上发生故障的可能性就越大。<br />经验法则：将其设置为在负载下在存储后端群集上测得的第95个百分位读取和写入时间的总和。重要说明：在所有JanusGraph实例中，该值都应该相同。<br />2）ids.renew-timeout配置毫秒数，JanusGraph的ID池管理器在尝试获取新的ID块而失败之前将等待的总时间。<br />经验法则：将该值设置为尽可能大，不必为不可恢复的故障等待太久。增加它的唯一缺点是JanusGraph将在不可用的存储后端群集上尝试很长时间。<br />
<br />1) `ids.authority.wait-time` configures the time in milliseconds the id pool manager waits for an id block application to be acknowledged by the storage backend. The shorter this time, the more likely it is that an application will fail on a congested storage cluster.<br />**Rule of thumb**: Set this to the sum of the 95th percentile read and write times measured on the storage backend cluster under load. **Important**: This value should be the same across all JanusGraph instances.<br />2) `ids.renew-timeout` configures the number of milliseconds JanusGraph’s id pool manager will wait in total while attempting to acquire a new id block before failing.<br />**Rule of thumb**: Set this value to be as large feasible to not have to wait too long for unrecoverable failures. The only downside of increasing it is that JanusGraph will try for a long time on an unavailable storage backend cluster.
<a name="optimizing-writes-and-reads"></a>
### Optimizing Writes and Reads 优化读写
<a name="buffer-size"></a>
#### Buffer Size 缓冲区大小
JanusGraph buffers writes and executes them in small batches to reduce the number of requests against the storage backend. The size of these batches is controlled by `storage.buffer-size`. When executing a lot of writes in a short period of time, it is possible that the storage backend can become overloaded with write requests. In that case, increasing `storage.buffer-size` can avoid failure by increasing the number of writes per request and thereby lowering the number of requests.<br />However, increasing the buffer size increases the latency of the write request and its likelihood of failure. Hence, it is not advisable to increase this setting for transactional loads and one should carefully experiment with this setting during bulk loading.<br />JanusGraph缓冲区以小批量写入和执行它们，以减少针对存储后端的请求数。这些批次的大小由storage.buffer-size控制。在短时间内执行大量写操作时，存储后端可能会因写请求而变得超载。在那种情况下，增加storage.buffer-size可以通过增加每个请求的写入次数来减少失败，从而避免失败。<br />但是，增加缓冲区大小会增加写请求的等待时间及其失败的可能性。因此，不建议为事务性负载增加此设置，并且应该在批量加载期间仔细尝试此设置。
<a name="read-and-write-robustness"></a>
#### Read and Write Robustness
During bulk loading, the load on the cluster typically increases making it more likely for read and write operations to fail (in particular if the buffer size is increased as described above). `storage.read-attempts` and `storage.write-attempts` configure how many times JanusGraph will attempt to execute a read or write operation against the storage backend before giving up. If it is expected that there is a high load on the backend during bulk loading, it is generally advisable to increase these configuration options.<br />`storage.attempt-wait` specifies the number of milliseconds that JanusGraph will wait before re-attempting a failed backend operation. A higher value can ensure that operation re-tries do not further increase the load on the backend.<br />在批量加载期间，群集上的负载通常会增加，从而使读和写操作失败的可能性更大（尤其是如上所述，如果缓冲区大小增加了）。 storage.read-attempts和storage.write-attempts配置在放弃之前，JanusGraph将尝试对存储后端执行读取或写入操作的次数。如果期望在批量加载期间后端上有很高的负载，通常建议增加这些配置选项。<br />storage.attempt-wait指定JanusGraph在重新尝试失败的后端操作之前将等待的毫秒数。较高的值可以确保重试操作不会进一步增加后端的负载。
<a name="strategies"></a>
## Strategies 策略
<a name="parallelizing-the-load"></a>
### Parallelizing the Load 负载并行化
如果JanusGraph的存储后端集群足够大，可以满足其他请求，则通过并行处理多台计算机上的批量加载，可以大大减少加载时间。本质上，这是TinkerPop的Hadoop-Gremlin使用JanusGraph的方法，即使用MapReduce将数据批量加载到JanusGraph中。<br />如果无法使用Hadoop并行化批量加载过程，则以下是一些用于有效并行化加载过程的高级指南：

- 在某些情况下，图数据可以分解为多个断开连接的子图。这些子图可以在多台计算机上并行并行加载（例如，使用如上所述的BatchGraph）。
- 如果无法分解图形，则分多个步骤加载通常是有益的，其中最后两个步骤可以在多台计算机上并行执行：
   1. 确保顶点和边数据集已重复数据删除且一致。
   1. 设置batch-loading = true。可能会优化上述其他配置设置。
   1. 将所有顶点及其属性添加到图中（但不包含边）。从顶点ID（由加载的数据定义）到JanusGraph的内部顶点ID（即vertex.getId（））维护一个（分布式）映射，该ID为64位长。
   1. 使用地图添加所有边，以查找JanusGraph的顶点ID，并使用该ID检索顶点。


<br />By parallelizing the bulk loading across multiple machines, the load time can be greatly reduced if JanusGraph’s storage backend cluster is large enough to serve the additional requests. This is essentially the approach [JanusGraph with TinkerPop’s Hadoop-Gremlin](https://docs.janusgraph.org/advanced-topics/hadoop/) takes to bulk loading data into JanusGraph using MapReduce.<br />If Hadoop cannot be used for parallelizing the bulk loading process, here are some high level guidelines for effectively parallelizing the loading process:

- In some cases, the graph data can be decomposed into multiple disconnected subgraphs. Those subgraphs can be loaded independently in parallel across multiple machines (for instance, using BatchGraph as described above).<br />
- If the graph cannot be decomposed, it is often beneficial to load in multiple steps where the last two steps can be parallelized across multiple machines:
   1. Make sure the vertex and edge data sets are de-duplicated and consistent.<br />
   1. Set `batch-loading=true`. Possibly optimize additional configuration settings described above.<br />
   1. Add all the vertices with their properties to the graph (but no edges). Maintain a (distributed) map from vertex id (as defined by the loaded data) to JanusGraph’s internal vertex id (i.e. `vertex.getId()`) which is a 64 bit long id.<br />
   1. Add all the edges using the map to look-up JanusGraph’s vertex id and retrieving the vertices using that id.<br />
<a name="qa"></a>
## Q&A

- **What should I do to avoid the following exception during batch-loading:** `java.io.IOException: ID renewal thread on partition [X] did not complete in time.`? This exception is mostly likely caused by repeated time-outs during the id allocation phase due to highly stressed storage backend. Refer to the section on _ID Allocation Optimization_ above.

问答环节<br />•应该如何避免在批量加载期间发生以下异常：java.io.IOException：分区[X]上的ID更新线程未及时完成。此异常很可能是由于id分配阶段由于高度后端的存储后端重复超时而引起的。请参阅上面有关ID分配优化的部分。
