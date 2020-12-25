## 缓存 Caching
JanusGraph employs multiple layers of data caching to facilitate fast graph traversals. The caching layers are listed here in the order they are accessed from within a JanusGraph transaction. The closer the cache is to the transaction, the faster the cache access and the higher the memory footprint and maintenance overhead.

JanusGraph采用多层数据缓存，以方便快速图遍历。缓存层在这里按从JanusGraph事务中访问它们的顺序列出。缓存离事务越近，缓存访问就越快，内存占用和维护开销也就越高。

## 事务级别缓存 Transaction-Level Caching
Within an open transaction, JanusGraph maintains two caches:
- Vertex Cache: Caches accessed vertices and their adjacency list (or subsets thereof) so that subsequent access is significantly faster within the same transaction. Hence, this cache speeds up iterative traversals.
- Index Cache: Caches the results for index queries so that subsequent index calls can be served from memory instead of calling the index backend and (usually) waiting for one or more network round trips.

The size of both of those is determined by the _transaction cache size_. The transaction cache size can be configured via `cache.tx-cache-size` or on a per transaction basis by opening a transaction via the transaction builder `graph.buildTransaction()` and using the `setVertexCacheSize(int)` method.

在一个开放的事务中，JanusGraph维护两个缓存：

- 顶点缓存：缓存访问的顶点及其邻接列表（或其子集），以便在同一事务中后续访问显着加快。 因此，此缓存可加速迭代遍历。
- 索引缓存：缓存索引查询的结果，以便可以从内存中为后续的索引调用提供服务，而不必调用索引后端，并且（通常）等待一次或多次网络往返。

两者的大小均由事务缓存大小确定。 可以通过cache.tx-cache-size或在每个事务的基础上配置事务缓存的大小，方法是通过事务构建器graph.buildTransaction（）并使用setVertexCacheSize（int）方法打开事务。

### 顶点缓存 Vertex Cache
The vertex cache contains vertices and the subset of their adjacency list that has been retrieved in a particular transaction. The maximum number of vertices maintained in this cache is equal to the transaction cache size. If the transaction workload is an iterative traversal, the vertex cache will significantly speed it up. If the same vertex is not accessed again in the transaction, the transaction level cache will make no difference.

Note, that the size of the vertex cache on heap is not only determined by the number of vertices it may hold but also by the size of their adjacency list. In other words, vertices with large adjacency lists (i.e. many incident edges) will consume more space in this cache than those with smaller lists.

Furthermore note, that modified vertices are _pinned_ in the cache, which means they cannot be evicted since that would entail loosing their changes. Therefore, transaction which contain a lot of modifications may end up with a larger than configured vertex cache.

顶点缓存包含顶点和在特定事务中已检索到的邻接表的子集。此缓存中维护的最大顶点数等于事务缓存大小。如果事务工作负载是迭代遍历，则顶点缓存将显着加快处理速度。如果未在事务中再次访问相同的顶点，则事务级别缓存将没有任何区别。

请注意，堆上顶点缓存的大小不仅取决于其可能容纳的顶点数量，还取决于其邻接列表的大小。换句话说，与列表较小的顶点相比，邻接列表较大的顶点（即许多入射边缘）将在此缓存中消耗更多的空间。

还要注意的是，修改后的顶点在缓存中是“固定”的，这意味着它们不能被驱逐，因为这将导致失去其更改。因此，包含大量修改的事务最终可能会比配置的顶点缓存大。

### 索引缓存 Index Cache
The index cache contains the results of index queries executed in the context of this transaction. Subsequent identical index calls will be served from this cache and are therefore significantly cheaper. If the same index call never occurs twice in the same transaction, the index cache makes no difference.

Each entry in the index cache is given a weight equal to `2 + result set size` and the total weight of the cache will not exceed half of the transaction cache size.

索引缓存包含在此事务的上下文中执行的索引查询的结果。随后的相同索引调用将从此缓存中提供，因此便宜得多。 如果同一索引调用在同一事务中再也不会发生两次，则索引缓存不会有任何区别。

索引缓存中的每个条目的权重等于“2+结果集大小”，并且缓存的总权重将不超过事务缓存大小的一半。

## 数据库级别缓存 Database Level Caching
The database level cache retains adjacency lists (or subsets thereof) across multiple transactions and beyond the duration of a single transaction. The database level cache is shared by all transactions across a database. It is more space efficient than the transaction level caches but also slightly slower to access. In contrast to the transaction level caches, the database level caches do not expire immediately after closing a transaction. Hence, the database level cache significantly speeds up graph traversals for read heavy workloads across transactions.

[Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) lists all of the configuration options that pertain to JanusGraph’s database level cache. This page attempts to explain their usage.

Most importantly, the database level cache is disabled by default in the current release version of JanusGraph. To enable it, set `cache.db-cache=true`.

数据库级高速缓存在多个事务中并在单个事务的持续时间之外保留邻接列表（或其子集）。 数据库级缓存由数据库中的所有事务共享。 它比事务级缓存更节省空间，但访问速度也稍慢。 与事务级缓存相反，数据库级缓存在关闭事务后不会立即过期。 因此，数据库级缓存显着加快了图形遍历的速度，以实现跨事务读取繁重的工作负载。

配置参考列出了与JanusGraph的数据库级缓存有关的所有配置选项。 此页面试图解释它们的用法。

最重要的是，默认情况下，JanusGraph的当前发行版中默认禁用数据库级缓存。 要启用它，请设置cache.db-cache = true。

### 缓存超时时间 Cache Expiration Time
The most important setting for performance and query behavior is the cache expiration time which is configured via `cache.db-cache-time`. The cache will hold graph elements for at most that many milliseconds. If an element expires, the data will be re-read from the storage backend on the next access.

If there is only one JanusGraph instance accessing the storage backend or if this instance is the only one modifying the graph, the cache expiration can be set to 0 which disables cache expiration. This allows the cache to hold elements indefinitely (unless they are evicted due to space constraints or on update) which provides the best cache performance. Since no other JanusGraph instance is modifying the graph, there is no danger of holding on to stale data.

If there are multiple JanusGraph instances accessing the storage backend, the time should be set to the maximum time that can be allowed between **another** JanusGraph instance modifying the graph and this JanusGraph instance seeing the data. If any change should be immediately visible to all JanusGraph instances, the database level cache should be disabled in a distributed setup. However, for most applications it is acceptable that a particular JanusGraph instance sees remote modifications with some delay. The larger the maximally allowed delay, the better the cache performance. Note, that a given JanusGraph instance will always immediately see its own modifications to the graph irrespective of the configured cache expiration time.

性能和查询行为的最重要设置是通过“ cache.db-cache-time”配置的缓存过期时间。缓存将最多保留图形元素数毫秒。如果元素过期，则下次访问时将从存储后端重新读取数据。

如果只有一个JanusGraph实例访问存储后端，或者该实例是唯一修改图形的实例，则可以将缓存过期设置为0，以禁用缓存过期。这允许高速缓存无限期地保存元素（除非由于空间限制或在更新时将其逐出），这可以提供最佳的高速缓存性能。由于没有其他JanusGraph实例正在修改图形，因此没有保留过时数据的危险。

如果有多个JanusGraph实例访问存储后端，则应将时间设置为**另一个** JanusGraph实例修改图形与该JanusGraph实例看到数据之间所允许的最大时间。如果所有JanusGraph实例都应立即看到任何更改，则应在分布式设置中禁用数据库级缓存。但是，对于大多数应用程序而言，可以将特定的JanusGraph实例看到具有某些延迟的远程修改是可以接受的。最大允许延迟越大，缓存性能越好。请注意，给定的JanusGraph实例将始终立即看到其对图形的修改，而与配置的缓存过期时间无关。

### 缓存大小 Cache Size
The configuration option `cache.db-cache-size` controls how much heap space JanusGraph’s database level cache is allowed to consume. The larger the cache, the more effective it will be. However, large cache sizes can lead to excessive GC and poor performance.

The cache size can be configured as a percentage (expressed as a decimal between 0 and 1) of the total heap space available to the JVM running JanusGraph or as an absolute number of bytes.

Note, that the cache size refers to the amount of heap space that is exclusively occupied by the cache. JanusGraph’s other data structures and each open transaction will occupy additional heap space. If additional software layers are running in the same JVM, those may occupy a significant amount of heap space as well (e.g. Gremlin Server, embedded Cassandra, etc). Be conservative in your heap memory estimation. Configuring a cache that is too large can lead to out-of-memory exceptions and excessive GC.

配置选项“ cache.db-cache-size”控制JanusGraph的数据库级缓存允许消耗多少堆空间。 缓存越大，效果越好。 但是，大的缓存大小可能导致过多的GC和较差的性能。

可以将缓存大小配置为运行JanusGraph的JVM可用的总堆空间的百分比（表示为0到1之间的十进制）或绝对字节数。

请注意，缓存大小是指缓存专门占用的堆空间量。JanusGraph的其他数据结构和每个打开的事务将占用额外的堆空间。如果其他软件层在同一JVM中运行，则这些软件层也可能占用大量的堆空间（例如Gremlin Server，嵌入式Cassandra等）。 对堆内存的估计要保守一些。配置太大的缓存会导致内存不足异常和过多的GC。

### 清除等待时间 Clean Up Wait Time
When a vertex is locally modified (e.g. an edge is added) all of the vertex’s related database level cache entries are marked as expired and eventually evicted. This will cause JanusGraph to refresh the vertex’s data from the storage backend on the next access and re-populate the cache.

However, when the storage backend is eventually consistent, the modifications that triggered the eviction may not yet be visible. By configuring `cache.db-cache-clean-wait`, the cache will wait for at least this many milliseconds before repopulating the cache with the entry retrieved from the storage backend.

If JanusGraph runs locally or against a storage backend that guarantees immediate visibility of modifications, this value can be set to 0.

在本地修改某个顶点（例如，添加一条边）后，所有与该顶点相关的数据库级缓存条目都被标记为过期，并最终被逐出。 这将导致JanusGraph在下次访问时从存储后端刷新顶点的数据，并重新填充缓存。

但是，当存储后端最终保持一致时，触发驱逐的修改可能仍不可见。 通过配置“ cache.db-cache-clean-wait”，缓存将至少等待这么多毫秒，然后再使用从存储后端检索的条目重新填充缓存。

如果JanusGraph在本地运行或针对保证可以立即看到修改的存储后端运行，则可以将该值设置为0。

## 存储后端缓存 Storage Backend Caching
Each storage backend maintains its own data caching layer. These caches benefit from compression, data compactness, coordinated expiration and are often maintained off heap which means that large caches can be used without running into garbage collection issues. While these caches can be significantly larger than the database level cache, they are also slower to access.

The exact type of caching and its properties depends on the particular [storage backend](https://docs.janusgraph.org/storage-backend/). Please refer to the respective documentation for more information about the caching infrastructure and how to optimize it.

每个存储后端都维护自己的数据缓存层。 这些缓存受益于压缩，数据紧凑性，有效的到期时间，并且通常在堆外维护，这意味着可以使用大型缓存而不会遇到垃圾回收问题。 尽管这些缓存可能比数据库级别的缓存大得多，但它们的访问速度也较慢。

缓存的确切类型及其属性取决于特定的[存储后端]（https://docs.janusgraph.org/storage-backend/）。 请参阅相应的文档，以获取有关缓存基础结构以及如何对其进行优化的更多信息。
