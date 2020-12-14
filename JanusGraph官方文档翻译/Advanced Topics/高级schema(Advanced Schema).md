# 高级schema Advanced Schema

This page describes some of the advanced schema definition options that JanusGraph provides. For general information on JanusGraph’s schema and how to define it, refer to [Schema and Data Modeling](https://docs.janusgraph.org/basics/schema/).<br /><br />此页面描述了JanusGraph提供的一些高级模式定义选项。有关JanusGraph架构及其定义方式的一般信息，请参阅架构和数据建模。
<a name="static-vertices"></a>
## Static Vertices
Vertex labels can be defined as **static** which means that vertices with that label cannot be modified outside the transaction in which they were created.<br /><br />顶点标签可以定义为静态，这意味着带有该标签的顶点不能在创建它们的事务之外进行修改。
```
mgmt = graph.openManagement()
tweet = mgmt.makeVertexLabel('tweet').setStatic().make()
mgmt.commit()
```
Static vertex labels are a method of controlling the data lifecycle and useful when loading data into the graph that should not be modified after its creation.<br /><br />静态顶点标签是一种控制数据生命周期的方法，在将数据加载到图中创建后不应该对其进行修改时非常有用。
<a name="edge-and-vertex-ttl"></a>
## Edge and Vertex TTL
Edge and vertex labels can be configured with a **time-to-live (TTL)**. Edges and vertices with such labels will automatically be removed from the graph when the configured TTL has passed after their initial creation. TTL configuration is useful when loading a large amount of data into the graph that is only of temporary use. Defining a TTL removes the need for manual clean up and handles the removal very efficiently. For example, it would make sense to TTL event edges such as user-page visits when those are summarized after a certain period of time or simply no longer needed for analytics or operational query processing.<br /><br />可以使用生存时间（TTL）来配置边缘和顶点标签。初始创建后，已配置的TTL经过时，带有此类标签的边和顶点将自动从图形中删除。当将大量数据仅临时使用地加载到图形中时，TTL配置很有用。定义TTL消除了手动清理的需要，并非常有效地处理了删除操作。例如，对于TTL事件边缘（例如用户页面访问），如果在一定时间后或不再需要进行分析或操作查询处理的情况下进行汇总，将是有意义的。<br /><br />The following storage backends support edge and vertex TTL.

- Cassandra
- HBase
- BerkeleyDB - supports only hour-discrete TTL, thus the minimal TTL is one hour.
<a name="edge-ttl"></a>
### Edge TTL
Edge TTL is defined on a per-edge label basis, meaning that all edges of that label have the same time-to-live. Note that the backend must support cell level TTL. Currently only Cassandra, HBase and BerkeleyDB support this.<br /><br />边缘TTL是基于每个边缘标签定义的，这意味着该标签的所有边缘都具有相同的生存时间。请注意，后端必须支持单元级TTL。目前，只有Cassandra，HBase和BerkeleyDB支持此功能。
```
mgmt = graph.openManagement()
visits = mgmt.makeEdgeLabel('visits').make()
mgmt.setTTL(visits, Duration.ofDays(7))
mgmt.commit()
```
Note, that modifying an edge resets the TTL for that edge. Also note, that the TTL of an edge label can be modified but it might take some time for this change to propagate to all running JanusGraph instances which means that two different TTLs can be temporarily in use for the same label.<br /><br />请注意，修改边沿会重置该边沿的TTL。还要注意，可以修改边缘标签的TTL，但是此更改可能需要一些时间才能传播到所有正在运行的JanusGraph实例，这意味着可以将两个不同的TTL临时用于同一标签。
<a name="property-ttl"></a>
### Property TTL
Property TTL is very similar to edge TTL and defined on a per-property key basis, meaning that all properties of that key have the same time-to-live. Note that the backend must support cell level TTL. Currently only Cassandra, HBase and BerkeleyDB support this.<br /><br />属性TTL与边缘TTL非常相似，并且是基于每个属性的密钥定义的，这意味着该密钥的所有属性都具有相同的生存时间。请注意，后端必须支持单元级TTL。目前，只有Cassandra，HBase和BerkeleyDB支持此功能。
```
mgmt = graph.openManagement()
sensor = mgmt.makePropertyKey('sensor').cardinality(Cardinality.LIST).dataType(Double.class).make()
mgmt.setTTL(sensor, Duration.ofDays(21))
mgmt.commit()
```
As with edge TTL, modifying an existing property resets the TTL for that property and modifying the TTL for a property key might not immediately take effect.<br /><br />与边缘TTL一样，修改现有属性会重置该属性的TTL，而修改属性密钥的TTL可能不会立即生效。
<a name="vertex-ttl"></a>
### Vertex TTL
Vertex TTL is defined on a per-vertex label basis, meaning that all vertices of that label have the same time-to-live. The configured TTL applies to the vertex, its properties, and all incident edges to ensure that the entire vertex is removed from the graph. For this reason, a vertex label must be defined as _static_ before a TTL can be set to rule out any modifications that would invalidate the vertex TTL. Vertex TTL only applies to static vertex labels. Note that the backend must support store level TTL. Currently only Cassandra, HBase and BerkeleyDB support this.<br /><br />顶点TTL是在每个顶点标签的基础上定义的，这意味着该标签的所有顶点具有相同的生存时间。配置的TTL应用于顶点，其属性和所有入射边，以确保从图形中删除整个顶点。因此，必须先将顶点标签定义为静态标签，然后才能设置TTL，以排除可能会使顶点TTL无效的任何修改。顶点TTL仅适用于静态顶点标签。请注意，后端必须支持存储级别TTL。目前，只有Cassandra，HBase和BerkeleyDB支持此功能。
```
mgmt = graph.openManagement()
tweet = mgmt.makeVertexLabel('tweet').setStatic().make()
mgmt.setTTL(tweet, Duration.ofHours(36))
mgmt.commit()
```
Note, that the TTL of a vertex label can be modified but it might take some time for this change to propagate to all running JanusGraph instances which means that two different TTLs can be temporarily in use for the same label.<br /><br />请注意，可以修改顶点标签的TTL，但此更改可能需要一些时间才能传播到所有正在运行的JanusGraph实例，这意味着可以将两个不同的TTL临时用于同一标签。
<a name="multi-properties"></a>
## Multi-Properties
As discussed in [Schema and Data Modeling](https://docs.janusgraph.org/basics/schema/), JanusGraph supports property keys with SET and LIST cardinality. Hence, JanusGraph supports multiple properties with the same key on a single vertex. Furthermore, JanusGraph treats properties similarly to edges in that single-valued property annotations are allowed on properties as shown in the following example.<br /><br />如架构和数据建模中所述，JanusGraph支持具有SET和LIST基数的属性键。因此，JanusGraph在单个顶点上使用相同的键支持多个属性。此外，JanusGraph对待属性的方式与边缘类似，因为在属性上允许使用单值属性注释，如以下示例所示。
```
mgmt = graph.openManagement()
mgmt.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.LIST).make()
mgmt.commit()
v = graph.addVertex()
p1 = v.property('name', 'Dan LaRocque')
p1.property('source', 'web')
p2 = v.property('name', 'dalaro')
p2.property('source', 'github')
graph.tx().commit()
v.properties('name')
==> Iterable over all name properties
```
These features are useful in a number of applications such as those where attaching provenance information (e.g. who added a property, when and from where?) to properties is necessary. Support for higher cardinality properties and property annotations on properties is also useful in high-concurrency, scale-out design patterns as described in [Eventually-Consistent Storage Backends](https://docs.janusgraph.org/advanced-topics/eventual-consistency/).<br /><br />这些功能在许多应用程序中很有用，例如需要将出处信息（例如，谁在何时何地从何处添加了属性？）附加到属性的应用程序。支持高基数属性和属性上的属性注释在最终并发的存储后端中所述的高并发，横向扩展设计模式中也很有用。<br /><br />Vertex-centric indexes and global graph indexes are supported for properties in the same manner as they are supported for edges. Refer to [Indexing for Better Performance](https://docs.janusgraph.org/index-management/index-performance/) for information on defining these indexes for edges and use the corresponding API methods to define the same indexes for properties.<br /><br />以属性支持顶点中心索引和全局图索引的方式与支持边缘的方式相同。有关为边缘定义这些索引的信息，请参阅索引以获取更好的性能，并使用相应的API方法为属性定义相同的索引。
<a name="unidirected-edges"></a>
## Unidirected Edges
Unidirected edges are edges that can only be traversed in the out-going direction. Unidirected edges have a lower storage footprint but are limited in the types of traversals they support. Unidirected edges are conceptually similar to hyperlinks in the world-wide-web in the sense that the out-vertex can traverse through the edge, but the in-vertex is unaware of its existence.<br /><br />单向边是只能沿向外方向移动的边。单向边的存储空间较小，但受其支持的遍历类型有限。单向边缘在概念上类似于万维网中的超链接，在某种意义上，外顶点可以遍历该边缘，但是内顶点不知道其存在。
```
mgmt = graph.openManagement()
mgmt.makeEdgeLabel('author').unidirected().make()
mgmt.commit()
```
Note, that unidirected edges do not get automatically deleted when their in-vertices are deleted. The user must ensure that such inconsistencies do not arise or resolve them at query time by explicitly checking vertex existence in a transaction. See the discussion in [Ghost Vertices](https://docs.janusgraph.org/basics/common-questions/#ghost-vertices) for more information.<br /><br />请注意，删除单向边的顶点时，它们不会自动删除。用户必须通过显式检查事务中顶点的存在来确保在查询时不会出现此类不一致性或解决它们。有关更多信息，请参见“ Ghost顶点”中的讨论。
