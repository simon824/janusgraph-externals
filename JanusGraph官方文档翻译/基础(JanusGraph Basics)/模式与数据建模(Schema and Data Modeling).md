Each JanusGraph graph has a schema comprised of the edge labels, property keys, and vertex labels used therein. A JanusGraph schema can either be explicitly or implicitly defined. Users are encouraged to explicitly define the graph schema during application development. An explicitly defined schema is an important component of a robust graph application and greatly improves collaborative software development. Note, that a JanusGraph schema can be evolved over time without any interruption of normal database operations. Extending the schema does not slow down query answering and does not require database downtime.

The schema type - i.e. edge label, property key, or vertex label - is assigned to elements in the graph - i.e. edge, properties or vertices respectively - when they are first created. The assigned schema type cannot be changed for a particular element. This ensures a stable type system that is easy to reason about.

Beyond the schema definition options explained in this section, schema types provide performance tuning options that are discussed in [Advanced Schema](https://docs.janusgraph.org/advanced-topics/advschema/).

每个JanusGraph图都有一个方案，该方案由其中使用的边缘标签，属性键和顶点标签组成。可以显式或隐式定义JanusGraph模式。鼓励用户在应用程序开发期间明确定义图模式。显式定义的架构是鲁棒图应用程序的重要组成部分，可以大大改善协作软件的开发。注意，JanusGraph模式可以随着时间的推移而演变，而不会中断正常的数据库操作。扩展架构不会降低查询的速度，也不需要数据库停机。

首次创建时，将模式类型（即边缘标签，属性键或顶点标签）分配给图形中的元素-即分别为边缘，属性或顶点。不能为特定元素更改分配的架构类型。这确保了易于推理的稳定类型系统。

除了本节中介绍的模式定义选项外，模式类型还提供了高级模式中讨论的性能调整选项。

## 显示 schema 信息 Displaying Schema Information
There are methods to view specific elements of the graph schema within the management API. These methods are `mgmt.printIndexes()`, `mgmt.printPropertyKeys()`, `mgmt.printVertexLabels()`, and `mgmt.printEdgeLabels()`. There is also a method that displays all the combined output named `printSchema()`.

有一些方法可以在管理API中查看图形架构的特定元素。 这些方法是mgmt.printIndexes()，mgmt.printPropertyKeys()，mgmt.printVertexLabels()和mgmt.printEdgeLabels()。 还有一种方法可以显示所有名为printSchema()的组合输出。
```
mgmt = graph.openManagement()
mgmt.printSchema()
```

## 定义边标签 Defining Edge Labels
Each edge connecting two vertices has a label which defines the semantics of the relationship. For instance, an edge labeled `friend` between vertices A and B encodes a friendship between the two individuals.

To define an edge label, call `makeEdgeLabel(String)` on an open graph or management transaction and provide the name of the edge label as the argument. Edge label names must be unique in the graph. This method returns a builder for edge labels that allows to define its multiplicity. The **multiplicity** of an edge label defines a multiplicity constraint on all edges of this label, that is, a maximum number of edges between pairs of vertices. JanusGraph recognizes the following multiplicity settings.

连接两个顶点的每个边都有一个标签，用于定义关系的语义。 例如，顶点A和顶点B之间的边缘标记为好友的边缘编码了两个个体之间的友情。

要定义边缘标签，请在打开的图形或管理事务上调用makeEdgeLabel（String）并提供边缘标签的名称作为参数。 边缘标签名称在图中必须唯一。 此方法返回边缘标签的生成器，该标签允许定义其多重性。 边缘标签的多重性定义了该标签所有边缘上的多重性约束，即，成对的顶点之间的最大边缘数。 JanusGraph可以识别以下多重设置。

### 边标签多样性 Edge Label Multiplicity 

- **MULTI**: Allows multiple edges of the same label between any pair of vertices. In other words, the graph is a _multi graph_ with respect to such edge label. There is no constraint on edge multiplicity.
- **SIMPLE**: Allows at most one edge of such label between any pair of vertices. In other words, the graph is a _simple graph_ with respect to the label. Ensures that edges are unique for a given label and pairs of vertices.
- **MANY2ONE**: Allows at most one outgoing edge of such label on any vertex in the graph but places no constraint on incoming edges. The edge label `mother` is an example with MANY2ONE multiplicity since each person has at most one mother but mothers can have multiple children.
- **ONE2MANY**: Allows at most one incoming edge of such label on any vertex in the graph but places no constraint on outgoing edges. The edge label `winnerOf` is an example with ONE2MANY multiplicity since each contest is won by at most one person but a person can win multiple contests.
- **ONE2ONE**: Allows at most one incoming and one outgoing edge of such label on any vertex in the graph. The edge label _marriedTo_ is an example with ONE2ONE multiplicity since a person is married to exactly one other person.

The default multiplicity is MULTI. The definition of an edge label is completed by calling the `make()` method on the builder which returns the defined edge label as shown in the following example.

- MULTI：允许在任意一对顶点之间使用同一标签的多个边。换句话说，该图是关于这种边缘标签的多重图。边缘多重性没有限制。
- **SIMPLE**：在任何一对顶点之间最多允许该标签的一个边缘。换句话说，该图是关于标签的简单图。确保给定标签和成对的顶点的边是唯一的。
- MANY2ONE：在图形的任何顶点上最多允许该标签的一个输出边缘，但对输入边缘不加任何限制。边缘标签母亲是MANY2ONE多样性的一个例子，因为每个人最多只有一个母亲，但是母亲可以有多个孩子。
- ONE2MANY：在图形的任何顶点上最多允许该标签的一个输入边缘，但对输出边缘不加任何限制。边缘标签winnerOf是一个具有ONE2MANY多重性的示例，因为每个竞赛最多只能由一个人赢得，但一个人可以赢得多个竞赛。
- ONE2ONE：在图形的任何顶点上最多允许该标签的一个输入边缘和一个输出边缘。因为一个人与另一个人结婚，所以边缘标签“ daughterTo”是ONE2ONE多重性的一个示例。

默认的多重性是MULTI。边缘标签的定义是通过在构建器上调用make()方法完成的，该方法返回定义的边缘标签，如以下示例所示。
```
mgmt = graph.openManagement()
follow = mgmt.makeEdgeLabel('follow').multiplicity(MULTI).make()
mother = mgmt.makeEdgeLabel('mother').multiplicity(MANY2ONE).make()
mgmt.commit()
```

## 定义属性键 Defining Property Keys 
Properties on vertices and edges are key-value pairs. For instance, the property `name='Daniel'` has the key `name` and the value `'Daniel'`. Property keys are part of the JanusGraph schema and can constrain the allowed data types and cardinality of values.

To define a property key, call `makePropertyKey(String)` on an open graph or management transaction and provide the name of the property key as the argument. Property key names must be unique in the graph, and it is recommended to avoid spaces or special characters in property names. This method returns a builder for the property keys.

顶点和边上的属性是键值对。例如，属性名称=“ Daniel”具有键名和值“ Daniel”。属性键是JanusGraph架构的一部分，可以限制允许的数据类型和值的基数。

要定义属性键，请在打开的图形或管理事务上调用makePropertyKey（String）并提供属性键的名称作为参数。属性键名称在图中必须是唯一的，建议避免在属性名称中使用空格或特殊字符。此方法返回属性键的生成器。

> Note
During property key creation, consider creating also graph indices for better performance, see [Index Performance](https://docs.janusgraph.org/index-management/index-performance/).
在创建属性键时，请考虑同时创建图形索引以提高性能，请参阅索引性能。

### 属性键数据类型 Property Key Data Type 
Use `dataType(Class)` to define the data type of a property key. JanusGraph will enforce that all values associated with the key have the configured data type and thereby ensures that data added to the graph is valid. For instance, one can define that the `name` key has a String data type. Note that primitive types are not supported. Use the corresponding wrapper class, e.g. `Integer` instead of `int`.

使用dataType（Class）定义属性键的数据类型。 JanusGraph将强制与键关联的所有值都具有配置的数据类型，从而确保添加到图的数据有效。例如，可以定义名称键具有String数据类型。请注意，不支持基本类型。使用相应的包装器类，例如整数而不是整数。

Define the data type as `Object.class` in order to allow any (serializable) value to be associated with a key. However, it is encouraged to use concrete data types whenever possible. Configured data types must be concrete classes and not interfaces or abstract classes. JanusGraph enforces class equality, so adding a sub-class of a configured data type is not allowed.

将数据类型定义为Object.class，以允许任何（可序列化的）值与键相关联。但是，建议尽可能使用具体的数据类型。配置的数据类型必须是具体的类，而不是接口或抽象类。 JanusGraph强制执行类相等性，因此不允许添加已配置数据类型的子类。

JanusGraph natively supports the following data types. JanusGraph本机支持以下数据类型。

Native JanusGraph Data Types 本机JanusGraph数据类型

| Name | Description |
| :--- | :--- |
| String | Character sequence |
| Character | Individual character |
| Boolean | true or false |
| Byte | byte value |
| Short | short value |
| Integer | integer value |
| Long | long value |
| Float | 4 byte floating point number |
| Double | 8 byte floating point number |
| Date | Specific instant in time (`java.util.Date`) |
| Geoshape | Geographic shape like point, circle or box |
| UUID | Universally unique identifier (`java.util.UUID`) |

### 属性键基数 Property Key Cardinality
Use `cardinality(Cardinality)` to define the allowed cardinality of the values associated with the key on any given vertex.

- **SINGLE**: Allows at most one value per element for such key. In other words, the key→value mapping is unique for all elements in the graph. The property key `birthDate` is an example with SINGLE cardinality since each person has exactly one birth date.
- **LIST**: Allows an arbitrary number of values per element for such key. In other words, the key is associated with a list of values allowing duplicate values. Assuming we model sensors as vertices in a graph, the property key `sensorReading` is an example with LIST cardinality to allow lots of (potentially duplicate) sensor readings to be recorded.
- **SET**: Allows multiple values but no duplicate values per element for such key. In other words, the key is associated with a set of values. The property key `name` has SET cardinality if we want to capture all names of an individual (including nick name, maiden name, etc).

The default cardinality setting is SINGLE. Note, that property keys used on edges and properties have cardinality SINGLE. Attaching multiple values for a single key on an edge or property is not supported.

使用基数（Cardinality）定义与任何给定顶点上的键关联的值的允许基数。
- SINGLE（单）：该键每个元素最多允许一个值。换句话说，键→值映射对于图中的所有元素都是唯一的。属性密钥birthDate是单基数的示例，因为每个人的出生日期恰好是一个。
- 列表：此键的每个元素允许任意数量的值。换句话说，键与允许重复值的值列表关联。假设我们将传感器建模为图形中的顶点，则属性键sensorReading是一个具有LIST基数的示例，可以记录很多（可能重复的）传感器读数。
- SET：此键的每个元素允许多个值，但不允许重复值。换句话说，键与一组值相关联。如果我们要捕获个人的所有名称（包括昵称，娘家姓等），则属性键名称具有SET基数。

默认基数设置为SINGLE。请注意，边缘和属性上使用的属性键的基数为SINGLE。不支持在边缘或属性上为单个键附加多个值。
```
mgmt = graph.openManagement()
birthDate = mgmt.makePropertyKey('birthDate').dataType(Long.class).cardinality(Cardinality.SINGLE).make()
name = mgmt.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.SET).make()
sensorReading = mgmt.makePropertyKey('sensorReading').dataType(Double.class).cardinality(Cardinality.LIST).make()
mgmt.commit()
```

## 关系类型 Relation Types 
Edge labels and property keys are jointly referred to as **relation types**. Names of relation types must be unique in the graph which means that property keys and edge labels cannot have the same name. There are methods in the JanusGraph API to query for the existence or retrieve relation types which encompasses both property keys and edge labels.

边缘标签和属性键共同称为关系类型。关系类型的名称在图中必须唯一，这意味着属性键和边标签不能具有相同的名称。 JanusGraph API中提供了一些方法来查询是否存在或检索关系类型，这些关系类型同时包含属性键和边标签。
```
mgmt = graph.openManagement()
if (mgmt.containsRelationType('name'))
    name = mgmt.getPropertyKey('name')
mgmt.getRelationTypes(EdgeLabel.class)
mgmt.commit()
```

## 定义顶点标签 Defining Vertex Labels 
Like edges, vertices have labels. Unlike edge labels, vertex labels are optional. Vertex labels are useful to distinguish different types of vertices, e.g. _user_ vertices and _product_ vertices.

Although labels are optional at the conceptual and data model level, JanusGraph assigns all vertices a label as an internal implementation detail. Vertices created by the `addVertex` methods use JanusGraph’s default label.

To create a label, call `makeVertexLabel(String).make()` on an open graph or management transaction and provide the name of the vertex label as the argument. Vertex label names must be unique in the graph.

像边缘一样，顶点也有标签。与边缘标签不同，顶点标签是可选的。顶点标签可用于区分不同类型的顶点，例如用户顶点和产品顶点。

尽管标签在概念和数据模型级别是可选的，但JanusGraph会为所有顶点分配标签作为内部实现细节。由addVertex方法创建的顶点使用JanusGraph的默认标签。

要创建标签，请在打开的图形或管理事务上调用makeVertexLabel（String）.make()并提供顶点标签的名称作为参数。顶点标签名称在图中必须唯一。
```
mgmt = graph.openManagement()
person = mgmt.makeVertexLabel('person').make()
mgmt.commit()
// Create a labeled vertex
person = graph.addVertex(label, 'person')
// Create an unlabeled vertex
v = graph.addVertex()
graph.tx().commit()
```

## 自动模式制作器 Automatic Schema Maker 
If an edge label, property key, or vertex label has not been defined explicitly, it will be defined implicitly when it is first used during the addition of an edge, vertex or the setting of a property. The `DefaultSchemaMaker` configured for the JanusGraph graph defines such types.

By default, implicitly created edge labels have multiplicity MULTI and implicitly created property keys have cardinality SINGLE and data type `Object.class`. Users can control automatic schema element creation by implementing and registering their own `DefaultSchemaMaker`.

When defining a cardinality for a vertex property which differs from SINGLE, the cardinality should be used for all values of the vertex property in the first query (i.e. the query which defines a new vertex property key).

It is strongly encouraged to explicitly define all schema elements and to disable automatic schema creation by setting `schema.default=none` in the JanusGraph graph configuration.

如果未明确定义边标签，属性键或顶点标签，则在添加边，顶点或属性设置期间首次使用时将隐式定义。为JanusGraph图配置的DefaultSchemaMaker定义了此类类型。

默认情况下，隐式创建的边缘标签具有多重性MULTI，而隐式创建的属性键具有基数SINGLE和数据类型Object.class。用户可以通过实现和注册自己的DefaultSchemaMaker来控制自动架构元素的创建。

在为不同于SINGLE的顶点属性定义基数时，应将基数用于第一个查询（即定义新顶点属性键的查询）中所有顶点属性值。

强烈建议通过在JanusGraph图形配置中设置schema.default = none来显式定义所有架构元素并禁用自动架构创建。

## 更改架构元素 Changing Schema Elements 
The definition of an edge label, property key, or vertex label cannot be changed once its committed into the graph. However, the names of schema elements can be changed via `JanusGraphManagement.changeName(JanusGraphSchemaElement, String)` as shown in the following example where the property key `place` is renamed to `location`.

边标签，属性键或顶点标签的定义一旦提交到图形中就无法更改。但是，可以通过JanusGraphManagement.changeName（JanusGraphSchemaElement，String）更改架构元素的名称，如以下示例所示，其中属性键位置已重命名为location。
```
mgmt = graph.openManagement()
place = mgmt.getPropertyKey('place')
mgmt.changeName(place, 'location')
mgmt.commit()
```
Note, that schema name changes may not be immediately visible in currently running transactions and other JanusGraph graph instances in the cluster. While schema name changes are announced to all JanusGraph instances through the storage backend, it may take a while for the schema changes to take effect and it may require a instance restart in the event of certain failure conditions - like network partitions - if they coincide with the rename. Hence, the user must ensure that either of the following holds:

- The renamed label or key is not currently in active use (i.e. written or read) and will not be in use until all JanusGraph instances are aware of the name change.
- Running transactions actively accommodate the brief intermediate period where either the old or new name is valid based on the specific JanusGraph instance and status of the name-change announcement. For instance, that could mean transactions query for both names simultaneously.

Should the need arise to re-define an existing schema type, it is recommended to change the name of this type to a name that is not currently (and will never be) in use. After that, a new label or key can be defined with the original name, thereby effectively replacing the old one. However, note that this would not affect vertices, edges, or properties previously written with the existing type. Redefining existing graph elements is not supported online and must be accomplished through a batch graph transformation.

请注意，架构名称更改在群集中当前正在运行的事务和其他JanusGraph图形实例中可能不会立即可见。虽然通过存储后端向所有JanusGraph实例宣布了架构名称更改，但是架构更改可能需要一段时间才能生效，并且如果某些故障情况（例如网络分区）与以下情况一致，则可能需要重新启动实例：重命名。因此，用户必须确保以下任一条件成立：

- 重命名的标签或密钥当前未处于活动状态（即，已写入或已读取），并且直到所有JanusGraph实例都知道名称更改后才会使用。
- 正在运行的事务会主动适应短暂的中间时间段，根据特定的JanusGraph实例和名称更改公告的状态，旧名称或新名称均有效。例如，这可能意味着事务同时查询两个名称。

如果需要重新定义现有的架构类型，建议将这种类型的名称更改为当前（永远不会）使用的名称。之后，可以使用原始名称定义新的标签或密钥，从而有效地替换旧的标签或密钥。但是，请注意，这不会影响以前使用现有类型编写的顶点，边或属性。在线不支持重新定义现有图形元素，必须通过批处理图形转换来完成。

## schema约束 Schema Constraints 
The definition of the schema allows users to configure explicit property and connection constraints. Properties can be bound to specific vertex label and/or edge labels. Moreover, connection constraints allow users to explicitly define which two vertex labels can be connected by an edge label. These constraints can be used to ensure that a graph matches a given domain model. For example for the graph of the gods, a `god` can be a brother of another `god`, but not of a `monster` and a `god` can have a property `age`, but `location` can not have a property `age`. These constraints are disabled by default.

Enable these schema constraints by setting `schema.constraints=true`. This setting depends on the setting `schema.default`. If config `schema.default` is set to `none`, then an `IllegalArgumentException` is thrown for schema constraint violations. If `schema.default` is not set `none`, schema constraints are automatically created, but no exception is thrown. Activating schema constraints has no impact on the existing data, because these schema constraints are only applied during the insertion process. So reading of data is not affected at all by those constraints.

Multiple properties can be bound to a vertex using `JanusGraphManagement.addProperties(VertexLabel, PropertyKey...)`, for example:

模式的定义允许用户配置显式属性和连接约束。可以将属性绑定到特定的顶点标签和/或边缘标签。此外，连接约束允许用户明确定义可以通过边缘标签连接的两个顶点标签。这些约束可用于确保图匹配给定的域模型。例如，对于神的图，一个神可以是另一个神的兄弟，但不能是怪物的兄弟，一个神可以具有财产年龄，但是位置不能具有财产年龄。默认情况下禁用这些约束。

通过设置 schema.constraints = true 启用这些架构约束。此设置取决于设置schema.default。如果config schema.default设置为none，则将因架构约束冲突而抛出IllegalArgumentException。如果未将schema.default设置为none，则将自动创建架构约束，但不会引发任何异常。激活架构约束对现有数据没有影响，因为这些架构约束仅在插入过程中应用。因此，这些约束完全不会影响数据的读取。

可以使用JanusGraphManagement.addProperties（VertexLabel，PropertyKey ...）将多个属性绑定到一个顶点，例如：
```
mgmt = graph.openManagement()
person = mgmt.makeVertexLabel('person').make()
name = mgmt.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.SET).make()
birthDate = mgmt.makePropertyKey('birthDate').dataType(Long.class).cardinality(Cardinality.SINGLE).make()
mgmt.addProperties(person, name, birthDate)
mgmt.commit()
```
Multiple properties can be bound to an edge using `JanusGraphManagement.addProperties(EdgeLabel, PropertyKey...)`, for example:

可以使用JanusGraphManagement.addProperties（EdgeLabel，PropertyKey ...）将多个属性绑定到一条边，例如：
```
mgmt = graph.openManagement()
follow = mgmt.makeEdgeLabel('follow').multiplicity(MULTI).make()
name = mgmt.makePropertyKey('name').dataType(String.class).cardinality(Cardinality.SET).make()
mgmt.addProperties(follow, name)
mgmt.commit()
```
Connections can be defined using `JanusGraphManagement.addConnection(EdgeLabel, VertexLabel out, VertexLabel in)` between an outgoing, an incoming and an edge, for example:

可以使用JanusGraphManagement.addConnection（EdgeLabel，VertexLabel输出，VertexLabel输入）在传出，传入和边缘之间定义连接，例如：
```
mgmt = graph.openManagement()
person = mgmt.makeVertexLabel('person').make()
company = mgmt.makeVertexLabel('company').make()
works = mgmt.makeEdgeLabel('works').multiplicity(MULTI).make()
mgmt.addConnection(works, person, company)
mgmt.commit()
```


