# 数据类型和属性序列化器配置

<a name="datatype-and-attribute-serializer-configuration"></a>
# Datatype and Attribute Serializer Configuration
JanusGraph supports a number of classes for attribute values on properties. JanusGraph efficiently serializes primitives, primitive arrays and `Geoshape`, `UUID`, `Date`, `ObjectNode` and `ArrayNode`. JanusGraph supports serializing arbitrary objects as attribute values, but these require custom serializers to be defined.

To configure a custom attribute class with a custom serializer, follow these steps:

1. Implement a custom `AttributeSerializer` for the custom attribute class
1. Add the following configuration options where [X] is the custom attribute id that must be larger than all attribute ids for already configured custom attributes:
   1. `attributes.custom.attribute[X].attribute-class = [Full attribute class name]`
   1. `attributes.custom.attribute[X].serializer-class = [Full serializer class name]`

For example, suppose we want to register a special integer attribute class called `SpecialInt` and have implemented a custom serializer `SpecialIntSerializer` that implements `AttributeSerializer`. We already have 9 custom attributes configured in the configuration file, so we would add the following lines

JanusGraph支持许多属性类的属性值。 JanusGraph有效地序列化图元，图元数组以及Geoshape，UUID，Date，ObjectNode和ArrayNode。 JanusGraph支持将任意对象序列化为属性值，但是这些都需要定义自定义序列化程序。

要使用自定义序列化程序配置自定义属性类，请按照下列步骤操作：

1. 为自定义属性类实现自定义AttributeSerializer
1. 添加以下配置选项，其中[X]是自定义属性ID，必须大于已配置的自定义属性的所有属性ID：
   1. attribute.custom.attribute [X] .attribute-class = [完整属性类名称]
   1. attribute.custom.attribute [X] .serializer-class = [完整的序列化程序类名称]

例如，假设我们要注册一个称为SpecialInt的特殊整数属性类，并实现了一个实现AttributeSerializer的自定义序列化程序SpecialIntSerializer。我们已经在配置文件中配置了9个自定义属性，因此我们将添加以下几行
```
attributes.custom.attribute10.attribute-class = com.example.SpecialInt
attributes.custom.attribute10.serializer-class = com.example.SpecialIntSerializer
```
<a name="custom-object-serialization"></a>
## Custom Object Serialization 自定义对象序列化
JanusGraph supports arbitrary objects as property attributes and can serialize such objects to disk. For this default serializer to work for a custom class, the following conditions must be fulfilled:
- The class must implement AttributeSerializer
- The class must have a no-argument constructor
- The class must implement the `equals(Object)` method
The last requirement is needed because JanusGraph will test both serialization and deserialization of a custom class before persisting data to disk.

JanusGraph支持将任意对象作为属性属性，并且可以将这些对象序列化到磁盘。为了使此默认序列化程序可用于自定义类，必须满足以下条件：
- 该类必须实现AttributeSerializer
- 类必须具有无参数的构造函数
- 该类必须实现equals（Object）方法
需要最后一个要求，因为JanusGraph将在将数据持久保存到磁盘之前测试自定义类的序列化和反序列化。
