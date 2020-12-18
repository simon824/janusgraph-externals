# 索引提升性能 Indexing for Better Performance

JanusGraph supports two different kinds of indexing to speed up query processing: graph indexes and vertex-centric indexes. Most graph queries start the traversal from a list of vertices or edges that are identified by their properties. Graph indexes make these global retrieval operations efficient on large graphs. Vertex-centric indexes speed up the actual traversal through the graph, in particular when traversing through vertices with many incident edges.
JanusGraph支持两种不同类型的索引来加速查询处理：图形索引和以顶点为中心的索引。大多数图形查询都从由其属性标识的顶点或边列表开始遍历。图形索引使这些全局检索操作在大型图形上更为有效。以顶点为中心的索引会加速通过图形的实际遍历，尤其是在遍历具有许多入射边的顶点时。
## Graph Index 图索引
Graph indexes are global index structures over the entire graph which allow efficient retrieval of vertices or edges by their properties for sufficiently selective conditions. For instance, consider the following queries
图索引是整个图的全局索引结构，可通过其属性有效地检索顶点或边，以获取足够的选择性条件。例如，考虑以下查询
```
g.V().has('name', 'hercules')
g.E().has('reason', textContains('loves'))
```
The first query asks for all vertices with the name `hercules`. The second asks for all edges where the property reason contains the word `loves`. Without a graph index answering those queries would require a full scan over all vertices or edges in the graph to find those that match the given condition which is very inefficient and infeasible for huge graphs.
第一个查询要求名称为hercules的所有顶点。第二个要求在属性原因包含“ love”一词的所有边缘。如果没有图索引，则这些查询将需要对图的所有顶点或边进行全面扫描，以找到与给定条件匹配的那些顶点，这对于大型图而言是非常低效且不可行的。


JanusGraph distinguishes between two types of graph indexes: **composite** and **mixed** indexes. Composite indexes are very fast and efficient but limited to equality lookups for a particular, previously-defined combination of property keys. Mixed indexes can be used for lookups on any combination of indexed keys and support multiple condition predicates in addition to equality depending on the backing index store.
JanusGraph区分两种图形索引：复合索引和混合索引。复合索引非常快速高效，但仅限于对特定的，先前定义的属性键组合进行相等查找。混合索引可用于在索引键的任何组合上进行查找，并且除了根据支持索引存储库的相等性以外，还支持多个条件谓词。


Both types of indexes are created through the JanusGraph management system and the index builder returned by `JanusGraphManagement.buildIndex(String, Class)` where the first argument defines the name of the index and the second argument specifies the type of element to be indexed (e.g. `Vertex.class`). The name of a graph index must be unique. Graph indexes built against newly defined property keys, i.e. property keys that are defined in the same management transaction as the index, are immediately available. The same applies to graph indexes that are constrained to a label that is created in the same management transaction as the index. Graph indexes built against property keys that are already in use without being constrained to a newly created label require the execution of a [reindex procedure](https://docs.janusgraph.org/index-management/index-reindexing/) to ensure that the index contains all previously added elements. Until the reindex procedure has completed, the index will not be available. It is encouraged to define graph indexes in the same transaction as the initial schema.
两种类型的索引都是通过JanusGraph管理系统创建的，并且由JanusGraphManagement.buildIndex（String，Class）返回的索引构建器，其中第一个参数定义索引的名称，第二个参数指定要索引的元素的类型（例如，顶点） 。类）。图索引的名称必须唯一。根据新定义的属性键（即在与索引相同的管理事务中定义的属性键）构建的图形索引立即可用。这同样适用于图索引，该图索引被约束到与索引在同一管理事务中创建的标签。根据已经使用的属性键构建的图形索引，而没有被限制为新创建的标签，需要执行重新索引过程以确保索引包含所有先前添加的元素。在重新索引过程完成之前，索引将不可用。鼓励在与初始模式相同的事务中定义图索引。


Note
In the absence of an index, JanusGraph will default to a full graph scan in order to retrieve the desired list of vertices. While this produces the correct result set, the graph scan can be very inefficient and lead to poor overall system performance in a production environment. Enable the `force-index` configuration option in production deployments of JanusGraph to prohibit graph scans.
在没有索引的情况下，JanusGraph将默认为全图扫描，以检索所需的顶点列表。虽然这会产生正确的结果集，但是图形扫描可能会非常低效，并导致生产环境中的整体系统性能变差。在JanusGraph的生产部署中启用force-index配置选项，以禁止图形扫描。
Info
See [index lifecycle documentation](https://docs.janusgraph.org/index-management/index-lifecycle/) for more information about index states.
有关索引状态的更多信息，请参见索引生命周期文档。
### Composite Index 复合索引
Composite indexes retrieve vertices or edges by one or a (fixed) composition of multiple keys. Consider the following composite index definitions.
复合索引通过一个或多个固定键（固定）检索顶点或边。请考虑以下综合索引定义。
```
graph.tx().rollback() //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
age = mgmt.getPropertyKey('age')
mgmt.buildIndex('byNameComposite', Vertex.class).addKey(name).buildCompositeIndex()
mgmt.buildIndex('byNameAndAgeComposite', Vertex.class).addKey(name).addKey(age).buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameComposite').call()
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameAndAgeComposite').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameComposite"), SchemaAction.REINDEX).get()
mgmt.updateIndex(mgmt.getGraphIndex("byNameAndAgeComposite"), SchemaAction.REINDEX).get()
mgmt.commit()
```
First, two property keys `name` and `age` are already defined. Next, a simple composite index on just the name property key is built. JanusGraph will use this index to answer the following query.
首先，已经定义了两个属性键名称（`name`）和年龄（`age`）。接下来，仅在name属性键上构建一个简单的复合索引。 JanusGraph将使用此索引来回答以下查询。
```
g.V().has('name', 'hercules')
```
The second composite graph index includes both keys. JanusGraph will use this index to answer the following query.
第二个合成图索引包含两个键。 JanusGraph将使用此索引来回答以下查询。
```
g.V().has('age', 30).has('name', 'hercules')
```
Note, that all keys of a composite graph index must be found in the query’s equality conditions for this index to be used. For example, the following query cannot be answered with either of the indexes because it only contains a constraint on `age` but not `name`.
请注意，必须在查询的相等条件中找到合成图形索引的所有键，才能使用该索引。例如，以下查询不能用任何一个索引回答，因为它只包含年龄限制，而没有名称限制。
```
g.V().has('age', 30)
```
Also note, that composite graph indexes can only be used for equality constraints like those in the queries above. The following query would be answered with just the simple composite index defined on the `name` key because the age constraint is not an equality constraint.
还要注意，合成图索引只能用于相等约束，例如上面的查询中的约束。由于年龄约束不是相等约束，因此仅使用名称键上定义的简单复合索引即可回答以下查询。
```
g.V().has('name', 'hercules').has('age', inside(20, 50))
```
Composite indexes do not require configuration of an external indexing backend and are supported through the primary storage backend. Hence, composite index modifications are persisted through the same transaction as graph modifications which means that those changes are atomic and/or consistent if the underlying storage backend supports atomicity and/or consistency.
复合索引不需要配置外部索引后端，并且通过主存储后端得到支持。因此，复合索引修改通过与图修改相同的事务来持久化，这意味着如果基础存储后端支持原子性和/或一致性，则这些更改是原子性的和/或一致的。
Note
A composite index may comprise just one or multiple keys. A composite index with just one key is sometimes referred to as a key-index.
复合索引可以仅包含一个或多个键。仅具有一个键的复合索引有时称为键索引。
#### Index Uniqueness 索引唯一性
Composite indexes can also be used to enforce property uniqueness in the graph. If a composite graph index is defined as `unique()` there can be at most one vertex or edge for any given concatenation of property values associated with the keys of that index. For instance, to enforce that names are unique across the entire graph the following composite graph index would be defined.
复合索引也可以用于强制图形中的属性唯一性。如果将组合图索引定义为unique（），则与该索引的键关联的属性值的任何给定串联最多可以有一个顶点或边。例如，要强制在整个图上使用唯一的名称，将定义以下复合图索引。
```
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
mgmt.buildIndex('byNameUnique', Vertex.class).addKey(name).unique().buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameUnique').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameUnique"), SchemaAction.REINDEX).get()
mgmt.commit()
```
Note
To enforce uniqueness against an eventually consistent storage backend, the [consistency](https://docs.janusgraph.org/advanced-topics/eventual-consistency/) of the index must be explicitly set to enabling locking.
为了对最终一致的存储后端实施唯一性，必须将索引的一致性显式设置为启用锁定。
### Mixed Index 混合索引
Mixed indexes retrieve vertices or edges by any combination of previously added property keys. Mixed indexes provide more flexibility than composite indexes and support additional condition predicates beyond equality. On the other hand, mixed indexes are slower for most equality queries than composite indexes.
混合索引通过先前添加的属性键的任意组合来检索顶点或边。混合索引比复合索引提供了更大的灵活性，并支持除相等性之外的其他条件谓词。另一方面，对于大多数相等性查询，混合索引比复合索引慢。


Unlike composite indexes, mixed indexes require the configuration of an [indexing backend](https://docs.janusgraph.org/index-backend/search-predicates/) and use that indexing backend to execute lookup operations. JanusGraph can support multiple indexing backends in a single installation. Each indexing backend must be uniquely identified by name in the JanusGraph configuration which is called the **indexing backend name**.


与复合索引不同，模糊索引需要配置索引后端，并使用该索引后端执行查找操作。 JanusGraph可以在单个安装中支持多个索引后端。在JanusGraph配置中，每个索引后端必须通过名称唯一标识，这称为索引后端名称。


```java
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
age = mgmt.getPropertyKey('age')
mgmt.buildIndex('nameAndAge', Vertex.class).addKey(name).addKey(age).buildMixedIndex("search")
mgmt.commit()
    
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'nameAndAge').call()
    
//Reindex the existing data 对已经存在的数据重做索引
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("nameAndAge"), SchemaAction.REINDEX).get()
mgmt.commit()

```
The example above defines a mixed index containing the property keys `name` and `age`. The definition refers to the indexing backend name `search` so that JanusGraph knows which configured indexing backend it should use for this particular index. The `search` parameter specified in the buildMixedIndex call must match the second clause in the JanusGraph configuration definition like this: index.**search**.backend If the index was named _solrsearch_ then the configuration definition would appear like this: index.**solrsearch**.backend.


上面的示例定义了一个混合索引，其中包含属性键名称和年龄。该定义指的是索引后端名称搜索，因此JanusGraph知道该特定索引应使用哪个已配置的索引后端。在buildMixedIndex调用中指定的搜索参数必须与JanusGraph配置定义中的第二个子句相匹配，如下所示：index.search.backend如果索引名为solrsearch，则配置定义将如下所示：index.solrsearch.backend。


The mgmt.buildIndex example specified above uses text search as its default behavior. An index statement that explicitly defines the index as a text index can be written as follows:
上面指定的mgmt.buildIndex示例使用文本搜索作为其默认行为。将索引明确定义为文本索引的索引语句可以编写如下：
```java
mgmt.buildIndex('nameAndAge',Vertex.class)
    .addKey(name,Mapping.TEXT.asParameter())
    .addKey(age,Mapping.TEXT.asParameter())
    .buildMixedIndex("search")
```


See [Index Parameters and Full-Text Search](https://docs.janusgraph.org/index-backend/text-search/) for more information on text and string search options, and see the documentation section specific to the indexing backend in use for more details on how each backend handles text versus string searches.
有关文本和字符串搜索选项的更多信息，请参见“索引参数”和“全文本搜索”；有关每个后端如何处理文本搜索和字符串搜索的更多详细信息，请参见特定于正在使用的索引后端的文档部分。


While the index definition example looks similar to the composite index above, it provides greater query support and can answer _any_ of the following queries.
虽然索引定义示例看起来与上面的复合索引相似，但是它提供了更大的查询支持，并且可以回答以下任何查询。
```
g.V().has('name', textContains('hercules')).has('age', inside(20, 50))
g.V().has('name', textContains('hercules'))
g.V().has('age', lt(50))
g.V().has('age', outside(20, 50))
g.V().has('age', lt(50).or(gte(60)))
g.V().or(__.has('name', textContains('hercules')), __.has('age', inside(20, 50)))
```
Mixed indexes support full-text search, range search, geo search and others. Refer to [Search Predicates and Data Types](https://docs.janusgraph.org/index-backend/search-predicates/) for a list of predicates supported by a particular indexing backend.
混合索引支持全文搜索，范围搜索，地理搜索等。有关特定索引后端支持的谓词列表，请参阅搜索谓词和数据类型。
Note
Unlike composite indexes, mixed indexes do not support uniqueness.
与复合索引不同，混合索引不支持uniqueness
#### Adding Property Keys 添加属性键
Property keys can be added to an existing mixed index which allows subsequent queries to include this key in the query condition.
可以将属性键添加到现有的混合索引中，该索引允许后续查询在查询条件中包括此键。
```
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
location = mgmt.makePropertyKey('location').dataType(Geoshape.class).make()
nameAndAge = mgmt.getGraphIndex('nameAndAge')
mgmt.addIndexKey(nameAndAge, location)
mgmt.commit()
//Previously created property keys already have the status ENABLED, but
//our newly created property key "location" needs to REGISTER so we wait for both statuses
ManagementSystem.awaitGraphIndexStatus(graph, 'nameAndAge').status(SchemaStatus.REGISTERED, SchemaStatus.ENABLED).call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("nameAndAge"), SchemaAction.REINDEX).get()
mgmt.commit()
```
To add a newly defined key, we first retrieve the existing index from the management transaction by its name and then invoke the `addIndexKey` method to add the key to this index.
要添加新定义的键，我们首先按名称从管理事务中检索现有索引，然后调用addIndexKey方法将键添加到该索引中。


If the added key is defined in the same management transaction, it will be immediately available for querying. If the property key has already been in use, adding the key requires the execution of a [reindex procedure](https://docs.janusgraph.org/index-management/index-reindexing/) to ensure that the index contains all previously added elements. Until the reindex procedure has completed, the key will not be available in the mixed index.
如果添加的密钥在同一管理事务中定义，它将立即可用于查询。如果属性键已在使用中，则添加键需要执行重新索引过程，以确保索引包含所有先前添加的元素。在重新索引过程完成之前，密钥将在混合索引中不可用。
#### Mapping Parameters 映射参数
When adding a property key to a mixed index - either through the index builder or the `addIndexKey` method - a list of parameters can be optionally specified to adjust how the property value is mapped into the indexing backend. Refer to the [mapping parameters overview](https://docs.janusgraph.org/index-backend/text-search/) for a complete list of parameter types supported by each indexing backend.
当通过索引生成器或addIndexKey方法将属性键添加到混合索引时，可以选择指定参数列表，以调整如何将属性值映射到索引后端。有关每个索引后端支持的参数类型的完整列表，请参阅映射参数概述。
### Ordering 排序
The order in which the results of a graph query are returned can be defined using the `order().by()` directive. The `order().by()` method expects two parameters:


- The name of the property key by which to order the results. The results will be ordered by the value of the vertices or edges for this property key.

- The sort order: either ascending `asc` or descending `desc`

可以使用order().by() 指令定义返回图形查询结果的顺序。 order().by() 方法需要两个参数：

- 用于排序结果的属性键的名称。结果将按此属性键的顶点或边的值排序。
- 排序顺序：升序升序或降序降序



For example, the query `g.V().has('name', textContains('hercules')).order().by('age', desc).limit(10)` retrieves the ten oldest individuals with _hercules_ in their name.
When using `order().by()` it is important to note that:


- Composite graph indexes do not natively support ordering search results. All results will be retrieved and then sorted in-memory. For large result sets, this can be very expensive.

- Mixed indexes support ordering natively and efficiently. However, the property key used in the order().by() method must have been previously added to the mixed indexed for native result ordering support. This is important in cases where the the order().by() key is different from the query keys. If the property key is not part of the index, then sorting requires loading all results into memory.
- 复合图索引本身不支持对搜索结果进行排序。所有结果将被检索，然后在内存中排序。对于大型结果集，这可能会非常昂贵。
- 混合索引支持本地有效排序。但是，必须事先将order（）。by（）方法中使用的属性键添加到混合索引中，以支持本机结果排序。这在order（）。by（）键与查询键不同的情况下非常重要。如果属性键不是索引的一部分，则排序需要将所有结果加载到内存中。
### Label Constraint 标签约束
In many cases it is desirable to only index vertices or edges with a particular label. For instance, one may want to index only gods by their name and not every single vertex that has a name property. When defining an index it is possible to restrict the index to a particular vertex or edge label using the `indexOnly` method of the index builder. The following creates a composite index for the property key `name` that indexes only vertices labeled `god`.
在许多情况下，只需要索引具有特定标签的顶点或边即可。例如，可能只想按神的名字索引神，而不是每个具有name属性的顶点。定义索引时，可以使用索引构建器的indexOnly方法将索引限制为特定的顶点或边标签。下面为属性键名称创建一个复合索引，该索引仅索引标记为god的顶点。
```
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
god = mgmt.getVertexLabel('god')
mgmt.buildIndex('byNameAndLabel', Vertex.class).addKey(name).indexOnly(god).buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameAndLabel').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameAndLabel"), SchemaAction.REINDEX).get()
mgmt.commit()
```
Label restrictions similarly apply to mixed indexes. When a composite index with label restriction is defined as unique, the uniqueness constraint only applies to properties on vertices or edges for the specified label.
标签限制同样适用于混合索引。将具有标签限制的复合索引定义为唯一时，唯一性约束仅适用于指定标签的顶点或边上的属性。
### Composite versus Mixed Indexes 对比复合索引和混合索引

1. Use a composite index for exact match index retrievals. Composite indexes do not require configuring or operating an external index system and are often significantly faster than mixed indexes.
   1. As an exception, use a mixed index for exact matches when the number of distinct values for query constraint is relatively small or if one value is expected to be associated with many elements in the graph (i.e. in case of low selectivity).
2. Use a mixed indexes for numeric range, full-text or geo-spatial indexing. Also, using a mixed index can speed up the order().by() queries.



1. 使用复合索引进行精确匹配索引检索。复合索引不需要配置或操作外部索引系统，并且通常比混合索引快得多。
   1. 作为例外，当查询约束的不同值的数量相对较小时，或者如果期望一个值与图形中的许多元素相关联（即，在选择性较低的情况下），请使用混合索引进行精确匹配
2. 将混合索引用于数字范围，全文本或地理空间索引。另外，使用混合索引可以加快order().by() 查询。



## Vertex-centric Indexes 顶点为中心的索引
Vertex-centric indexes are local index structures built individually per vertex. In large graphs vertices can have thousands of incident edges. Traversing through those vertices can be very slow because a large subset of the incident edges has to be retrieved and then filtered in memory to match the conditions of the traversal. Vertex-centric indexes can speed up such traversals by using localized index structures to retrieve only those edges that need to be traversed.


Suppose that Hercules battled hundreds of monsters in addition to the three captured in the introductory [Graph of the Gods](https://docs.janusgraph.org/getting-started/basic-usage/). Without a vertex-centric index, a query asking for those monsters battled between time point `10` and `20` would require retrieving all `battled` edges even though there are only a handful of matching edges.


以顶点为中心的索引是每个顶点单独构建的局部索引结构。在大图中，顶点可以具有数千个入射边。遍历那些顶点可能非常慢，因为必须检索入射边缘的大子集，然后在内存中进行过滤以匹配遍历的条件。以顶点为中心的索引可以通过使用**局部索引结构**仅检索那些需要遍历的边来加速此类遍历。


假设大力神除了介绍《众神图》中捕获的三个怪物外还与数百个怪物战斗。如果没有以顶点为中心的索引，那么查询那些在时间点10和20之间战斗的怪物的查询将需要检索所有战斗的边缘，即使只有少数匹配的边缘也是如此。
```java
h = g.V().has('name', 'hercules').next()
g.V(h).outE('battled').has('time', inside(10, 20)).inV()
```
Building a vertex-centric index by time speeds up such traversal queries. Note, this initial index example already exists in the _Graph of the Gods_ as an index named `edges`. As a result, running the steps below will result in a uniqueness constraint error.
通过时间建立以顶点为中心的索引可加快此类遍历查询的速度。请注意，此初始索引示例已在众神之图中作为名为edge的索引存在。结果，执行以下步骤将导致唯一性约束错误。
```java
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
time = mgmt.getPropertyKey('time')
battled = mgmt.getEdgeLabel('battled')
mgmt.buildEdgeIndex(battled, 'battlesByTime', Direction.BOTH, Order.desc, time)
mgmt.commit()
    
//Wait for the index to become available
ManagementSystem.awaitRelationIndexStatus(graph, 'battlesByTime').call()
    
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getRelationIndex(battled, "battlesByTime"), SchemaAction.REINDEX).get()
mgmt.commit()
```


This example builds a vertex-centric index which indexes `battled` edges in both direction by time in descending order. A vertex-centric index is built against a particular edge label which is the first argument to the index construction method `JanusGraphManagement.buildEdgeIndex()`. The index only applies to edges of this label - `battled` in the example above. The second argument is a unique name for the index. The third argument is the edge direction in which the index is built. The index will only apply to traversals along edges in this direction. In this example, the vertex-centric index is built in both direction which means that time restricted traversals along `battled` edges can be served by this index in both the `IN` and `OUT` direction. JanusGraph will maintain a vertex-centric index on both the in- and out-vertex of `battled` edges. Alternatively, one could define the index to apply to the `OUT` direction only which would speed up traversals from Hercules to the monsters but not in the reverse direction. This would only require maintaining one index and hence half the index maintenance and storage cost. The last two arguments are the sort order of the index and a list of property keys to index by. The sort order is optional and defaults to ascending order (i.e. `Order.ASC`). The list of property keys must be non-empty and defines the keys by which to index the edges of the given label. A vertex-centric index can be defined with multiple keys.


此示例构建了一个以顶点为中心的索引，该索引以时间降序索引了两个方向上的`battled`边。针对特定的边缘标签构建以顶点为中心的索引，该边缘标签是索引构建方法 JanusGraphManagement.buildEdgeIndex() 的第一个参数。索引仅适用于该标签的边缘-在上例中已作斗争。第二个参数是索引的唯一名称。第三个参数是建立索引的边缘方向。该索引仅适用于沿该方向沿边的遍历。


在此示例中，以顶点为中心的索引是在两个方向上构建的，这意味着沿索引的边界可以在 IN 和 OUT 方向上沿受时间限制的遍历遍历。 JanusGraph 将在`battled`边的内外顶点均保持以顶点为中心的索引。或者，可以将索引定义为仅适用于 OUT 方向，这样可以加快从大力神到怪物的遍历，但不能反向。这仅需要维护一个索引，因此只需一半的索引维护和存储成本。最后两个参数是索引的排序顺序以及用于索引的属性键列表。排序顺序是可选的，默认为升序（即 Order.ASC ）。属性键列表必须为非空，并定义用于索引给定标签边缘的键。可以使用多个键定义以顶点为中心的索引。


```
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
time = mgmt.getPropertyKey('time')
rating = mgmt.makePropertyKey('rating').dataType(Double.class).make()
battled = mgmt.getEdgeLabel('battled')
mgmt.buildEdgeIndex(battled, 'battlesByRatingAndTime', Direction.OUT, Order.desc, rating, time)
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitRelationIndexStatus(graph, 'battlesByRatingAndTime', 'battled').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getRelationIndex(battled, 'battlesByRatingAndTime'), SchemaAction.REINDEX).get()
mgmt.commit()
```


This example extends the schema by a `rating` property on `battled` edges and builds a vertex-centric index which indexes `battled` edges in the out-going direction by rating and time in descending order. Note, that the order in which the property keys are specified is important because vertex-centric indexes are prefix indexes. This means, that `battled` edges are indexed by `rating` _first_ and `time` _second_.


本示例通过在战斗边缘上的评级属性扩展架构，并构建一个以顶点为中心的索引，该索引通过评级和时间以降序对外出方向上的战斗边缘进行索引。请注意，指定属性键的顺序很重要，因为以顶点为中心的索引是前缀索引。这意味着，首先通过评分，然后通过时间索引来索引战斗的边缘。


```
h = g.V().has('name', 'hercules').next()
g.V(h).outE('battled').property('rating', 5.0) //Add some rating properties

g.V(h).outE('battled').has('rating', gt(3.0)).inV()
g.V(h).outE('battled').has('rating', 5.0).has('time', inside(10, 50)).inV()
g.V(h).outE('battled').has('time', inside(10, 50)).inV()
```


Hence, the `battlesByRatingAndTime` index can speed up the first two but not the third query.
Multiple vertex-centric indexes can be built for the same edge label in order to support different constraint traversals. JanusGraph’s query optimizer attempts to pick the most efficient index for any given traversal. Vertex-centric indexes only support equality and range/interval constraints.


因此，battlesByRatingAndTime索引可以加快前两个查询的速度，但不能加快第三个查询的速度。
可以为同一边缘标签构建多个以顶点为中心的索引，以支持不同的约束遍历。 JanusGraph的查询优化器尝试为任何给定遍历选择最有效的索引。以顶点为中心的索引仅支持相等性和范围/间隔约束。
Note
The property keys used in a vertex-centric index must have an explicitly defined data type (i.e. _not_ `Object.class`) which supports a native sort order. This means not only that they must implement `Comparable` but that their serializer must implement `OrderPreservingSerializer`. The types that are currently supported are `Boolean`, `UUID`, `Byte`, `Float`, `Long`, `String`, `Integer`, `Date`, `Double`, `Character`, and `Short`
以顶点为中心的索引中使用的属性键必须具有支持本机排序顺序的显式定义的数据类型（即非Object.class）。这不仅意味着它们必须实现Comparable，而且其序列化程序必须实现OrderPreservingSerializer。当前支持的类型是布尔值，UUID，字节，浮点数，长整数，字符串，整数，日期，双精度型，字符型和短型


If the vertex-centric index is built against either an edge label or at least one property key that is defined in the same management transaction, the index will be immediately available for querying. If both the edge label and all of the indexed property keys have already been in use, building a vertex-centric index against it requires the execution of a [reindex procedure](https://docs.janusgraph.org/index-management/index-reindexing/) to ensure that the index contains all previously added edges. Until the reindex procedure has completed, the index will not be available.


如果以边缘标签或在同一管理事务中定义的至少一个属性键为基础构建以顶点为中心的索引，则该索引将立即可用于查询。如果边缘标签和所有索引的属性键都已被使用，则针对其建立以顶点为中心的索引需要执行重新索引过程，以确保索引包含所有先前添加的边缘。在重新索引过程完成之前，索引将不可用。
Note
JanusGraph automatically builds vertex-centric indexes per edge label and property key. That means, even with thousands of incident `battled` edges, queries like `g.V(h).out('mother')` or `g.V(h).values('age')` are efficiently answered by the local index.
Vertex-centric indexes cannot speed up unconstrained traversals which require traversing through all incident edges of a particular label. Those traversals will become slower as the number of incident edges increases. Often, such traversals can be rewritten as constrained traversals that can utilize a vertex-centric index to ensure acceptable performance at scale.
JanusGraph自动为每个边标签和属性键构建以顶点为中心的索引。这意味着，即使有成千上万的事件发生冲突，诸如g.V（h）.out（'mother'）或g.V（h）.values（'age'）之类的查询也可以通过本地索引得到有效回答。


以顶点为中心的索引无法加快无约束遍历，而无约束遍历需要遍历特定标签的所有入射边。随着入射边缘数量的增加，这些遍历将变慢。通常，此类遍历可以重写为可以利用顶点为中心的索引以确保在规模上可接受的性能的受约束遍历。
### Ordered Traversals 有序遍历
The following queries specify an order in which the incident edges are to be traversed. Use the `localLimit` command to retrieve a subset of the edges (in a given order) for EACH vertex that is traversed.
以下查询指定要遍历入射边的顺序。使用localLimit命令为遍历的每个EACH顶点检索边的子集（以给定的顺序）。
```
h = g..V().has('name', 'hercules').next()
g.V(h).local(outE('battled').order().by('time', desc).limit(10)).inV().values('name')
g.V(h).local(outE('battled').has('rating', 5.0).order().by('time', desc).limit(10)).values('place')
```
The first query asks for the names of the 10 most recently battled monsters by Hercules. The second query asks for the places of the 10 most recent battles of Hercules that are rated 5 stars. In both cases, the query is constrained by an order on a property key with a limit on the number of elements to be returned.
Such queries can also be efficiently answered by vertex-centric indexes if the order key matches the key of the index and the requested order (i.e. ascending or descending) is the same as the one defined for the index. The `battlesByTime` index would be used to answer the first query and `battlesByRatingAndTime` applies to the second. Note, that the `battlesByRatingAndTime` index cannot be used to answer the first query because an equality constraint on `rating` must be present for the second key in the index to be effective.


第一个查询将询问大力神（Hercules）最近战斗的10个怪物的名称。第二个查询将询问赫拉克勒斯最近10场被评为5星的战斗的地点。在这两种情况下，查询都受属性键顺序的限制，该顺序限制了要返回的元素数。
如果顺序键与索引的键匹配并且请求的顺序（即升序或降序）与为索引定义的键相同，则也可以通过以顶点为中心的索引有效地回答此类查询。 BattlesByTime索引将用于回答第一个查询，而BattlesByRatingAndTime则适用于第二个查询。请注意，因为必须存在对等级的相等约束才能使索引中的第二个键生效，所以无法使用BattlesByRatingAndTime索引来回答第一个查询。
