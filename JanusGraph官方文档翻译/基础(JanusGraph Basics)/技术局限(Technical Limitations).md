# 技术局限 Technical Limitations
There are various limitations and "gotchas" that one should be aware of when using JanusGraph. Some of these limitations are necessary design choices and others are issues that will be rectified as JanusGraph development continues. Finally, the last section provides solutions to common issues.

使用JanusGraph时应注意各种限制和“陷阱”。这些限制中的一些是必需的设计选择，而其他一些问题将随着JanusGraph开发的进行而得到纠正。最后，最后一部分提供了常见问题的解决方案。

## 设计局限性 Design Limitations 
These limitations reflect long-term tradeoffs design tradeoffs which are either difficult or impractical to change. These limitations are unlikely to be removed in the near future.

这些限制反映了长期的权衡设计折衷，这是很难或不切实际的。这些限制不太可能在不久的将来消除。
<a name="size-limitation"></a>
### 大小限制 Size Limitation 
JanusGraph can store up to a quintillion edges (2^60) and half as many vertices. That limitation is imposed by JanusGraph’s id scheme.

JanusGraph 最多可以存储5百亿个边（2 ^ 60）和一半的顶点。该限制是JanusGraph的id方案强加的。
<a name="datatype-definitions"></a>
### 数据类型定义 DataType Definitions 
When declaring the data type of a property key using `dataType(Class)` JanusGraph will enforce that all properties for that key have the declared type, unless that type is `Object.class`. This is an equality type check, meaning that sub-classes will not be allowed. For instance, one cannot declare the data type to be `Number.class` and use `Integer` or `Long`. For efficiency reasons, the type needs to match exactly. Hence, use `Object.class` as the data type for type flexibility. In all other cases, declare the actual data type to benefit from increased performance and type safety.

当使用dataType（Class）声明属性键的数据类型时，JanusGraph将强制该键的所有属性都具有声明的类型，除非该类型为Object.class。这是一个相等类型检查，这意味着将不允许子类。例如，不能将数据类型声明为Number.class并使用Integer或Long。出于效率原因，类型需要完全匹配。因此，请使用Object.class作为数据类型以提高类型的灵活性。在所有其他情况下，请声明实际数据类型，以从提高的性能和类型安全性中受益。

### 边检索为O(log(k))  Edge Retrievals are O(log(k)) 
Retrieving an edge by id, e.g `tx.getEdge(edge.getId())`, is not a constant time operation because it requires an index call on one of its adjacent vertices. Hence, the cost of retrieving an individual edge by its id is `O(log(k))` where `k` is the number of incident edges on the adjacent vertex. JanusGraph will attempt to pick the adjacent vertex with the smaller degree.

This also applies to index retrievals for edges via a standard or external index.

通过id检索边缘（例如tx.getEdge（edge.getId()））不是固定时间的操作，因为它需要在其相邻顶点之一上进行索引调用。因此，通过其id检索单个边的成本为O（log（k）），其中k是相邻顶点上的入射边数。 JanusGraph将尝试选择度数较小的相邻顶点。

这也适用于通过标准索引或外部索引检索边缘的索引。

### 类型定义不能更改 Type Definitions cannot be changed 
The definition of an edge label, property key, or vertex label cannot be changed once it has been committed to the graph. However, a type can be renamed and new types can be created at runtime to accommodate an evolving schema.

边标签，属性键或顶点标签的定义一旦提交到图形，就无法更改。但是，可以重命名类型，并且可以在运行时创建新类型以适应不断发展的模式。

### 保留关键字 Reserved Keywords 
There are certain keywords that JanusGraph uses internally for types that cannot be used otherwise. These types include vertex labels, edge labels, and property keys. The following are keywords that cannot be used:

JanusGraph在内部将某些关键字用于其他类型不能使用的类型。这些类型包括顶点标签，边标签和属性键。以下是无法使用的关键字：

- vertex
- element
- edge
- property
- label
- key

For example, if you attempt to create a vertex with the label of `property`, you will receive an exception regarding protected system types.

例如，如果尝试创建带有属性标签的顶点，则会收到有关受保护系统类型的异常。

## 临时限制 Temporary Limitations 
These are limitations in JanusGraph’s current implementation. These limitations could reasonably be removed in upcoming versions of JanusGraph.

这些是JanusGraph当前实施中的限制。这些限制可以在即将发布的JanusGraph版本中合理地消除。

### 有限的混合指数支持 Limited Mixed Index Support 
Mixed indexes only support a subset of the data types that JanusGraph supports. See [Mixed Index Data Types](https://docs.janusgraph.org/index-backend/search-predicates/#data-type-support) for a current listing. Also, mixed indexes do not currently support property keys with SET or LIST cardinality.

混合索引仅支持JanusGraph支持的数据类型的子集。有关当前列表，请参见混合索引数据类型。另外，混合索引当前不支持SET或LIST基数的属性键。

### 批量加载速度  Batch Loading Speed 
JanusGraph provides a batch loading mode that can be enabled through the [graph configuration](https://docs.janusgraph.org/basics/configuration-reference/). However, this batch mode only facilitates faster loading into the storage backend, it does not use storage backend specific batch loading techniques that prepare the data in memory for disk storage. As such, batch loading in JanusGraph is currently slower than batch loading modes provided by single machine databases. [Bulk Loading](https://docs.janusgraph.org/advanced-topics/bulk-loading/) contains information on speeding up batch loading in JanusGraph.

Another limitation related to batch loading is the failure to load millions of edges into a single vertex at once or in a short time of period. Such **supernode loading** can fail for some storage backends. This limitation also applies to dense index entries.

JanusGraph提供可以通过图形配置启用的批量加载模式。但是，此批处理模式仅有助于更快地将其加载到存储后端，而不使用特定于存储后端的批处理加载技术来为磁盘存储准备内存中的数据。因此，JanusGraph中的批量加载当前比单机数据库提供的批量加载模式要慢。批量加载包含有关在JanusGraph中加快批量加载的信息。

与批量加载有关的另一个限制是无法一次或在短时间内将数百万条边加载到单个顶点中。对于某些存储后端，此类超节点加载可能会失败。此限制也适用于密集索引条目。
