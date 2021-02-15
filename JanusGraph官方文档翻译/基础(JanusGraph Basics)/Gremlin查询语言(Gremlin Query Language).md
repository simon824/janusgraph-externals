# Gremlin查询语言 Gremlin Query Language

[Gremlin](https://tinkerpop.apache.org/gremlin.html) is JanusGraph’s query language used to retrieve data from and modify data in the graph. Gremlin is a path-oriented language which succinctly expresses complex graph traversals and mutation operations. Gremlin is a [functional language](https://en.wikipedia.org/wiki/Functional_programming) whereby traversal operators are chained together to form path-like expressions. For example, "from Hercules, traverse to his father and then his father’s father and return the grandfather’s name."

[Gremlin](https://tinkerpop.apache.org/gremlin.html) 是 JanusGraph 的查询语言，用于从图中检索数据和修改图中的数据。Gremlin 是一种面向路径的语言，它可以简洁地表达复杂的图遍历和图更改操作。Gremlin 是一个函数式语言，使得图遍历运算符被连接在一起，形成一个类似路径的表达式。例如，“从大力神出发，先走到他的父亲，再走到他父亲的父亲，然后返回其祖父的名字。”

Gremlin is a component of [Apache TinkerPop](https://tinkerpop.apache.org/). It is developed independently from JanusGraph and is supported by most graph databases. By building applications on top of JanusGraph through the Gremlin query language, users avoid vendor-lock in because their application can be migrated to other graph databases supporting Gremlin.

Gremlin 是 [Apache TinkerPop](https://tinkerpop.apache.org/) 的一个组件。它独是立于 JanusGraph 开发的，而且被大多数图数据库支持。使用 Gremlin 查询语言基于 JanusGraph 构建应用，用户可以避免被图数据库供应商套牢，因为他们的应用可以迁移到其它支持 Gremlin 的图数据。

This section is a brief overview of the Gremlin query language. For more information on Gremlin, refer to the following resources:

- [Practical Gremlin](https://kelvinlawrence.net/book/Gremlin-Graph-Guide.html): An online book by Kelvin R. Lawrence providing an in-depth overview of Gremlin and it's interaction with JanusGraph.

- [Complete Gremlin Manual](https://tinkerpop.apache.org/docs/3.4.6/reference/): Reference manual for all of the Gremlin steps.

- [Gremlin Console Tutorial](https://tinkerpop.apache.org/docs/3.4.6/tutorials/the-gremlin-console/): Learn how to use the Gremlin Console effectively to traverse and analyze a graph interactively.

- [Gremlin Recipes](https://tinkerpop.apache.org/docs/3.4.6/recipes/): A collection of best practices and common traversal patterns for Gremlin.

- [Gremlin Language Drivers](https://tinkerpop.apache.org/index.html#language-drivers): Connect to a Gremlin Server with different programming languages, including Go, JavaScript, .NET/C#, PHP, Python, Ruby, Scala, and TypeScript.

- [Gremlin Language Variants](https://tinkerpop.apache.org/docs/3.4.6/tutorials/gremlin-language-variants/): Learn how to embed Gremlin in a host programming language.

- [Gremlin for SQL developers](http://sql2gremlin.com/): Learn Gremlin using typical patterns found when querying data with SQL.

本章节是 Gremlin 查询语言的简要概述，关于 Gremlin 的更多信息，请参考下列资源：

- [Practical Gremlin](https://kelvinlawrence.net/book/Gremlin-Graph-Guide.html): Kelvin R. Lawrence 的在线书籍，提供了 Gremlin 和其与 JanusGraph 交互的的深入概述。

- [Complete Gremlin Manual](https://tinkerpop.apache.org/docs/3.4.6/reference/): 所有 Gremlin 单步的参考手册。

- [Gremlin Console Tutorial](https://tinkerpop.apache.org/docs/3.4.6/tutorials/the-gremlin-console/): 学习怎样通过 Gremlin 控制台以交互的方式遍历和分析图谱。

- [Gremlin Recipes](https://tinkerpop.apache.org/docs/3.4.6/recipes/): Gremlin 最佳实践和常见遍历模式集合。

- [Gremlin Language Drivers](https://tinkerpop.apache.org/index.html#language-drivers): 通过不同的编程语言连接到 Gremlin Server，包括：Go, JavaScript, .NET/C#, PHP, Python, Ruby, Scala, and TypeScript。

- [Gremlin Language Variants](https://tinkerpop.apache.org/docs/3.4.6/tutorials/gremlin-language-variants/): 了解如何将 Gremlin 嵌入宿主编程语言。

- [Gremlin for SQL developers](http://sql2gremlin.com/): 用通过 SQL 查询数据时发现的经典模式来学习 Gremlin。

In addition to these resources, [Connecting to JanusGraph](https://docs.janusgraph.org/connecting/) explains how Gremlin can be used in different programming languages to query a JanusGraph Server.

除了这些资源之外，[连接到JanusGraph](https://docs.janusgraph.org/connecting/) 章节还介绍了如何在不同的编程语言中使用 Gremlin 来查询 JanusGraph Server。

## 图遍历入门 Introductory Traversals

A Gremlin query is a chain of operations/functions that are evaluated from left to right. A simple grandfather query is provided below over the *Graph of the Gods* dataset discussed in [Getting Started](https://docs.janusgraph.org/getting-started/installation/).

Gremlin 查询是一个从左到右计算的操作/函数链。以下是在[起步](https://docs.janusgraph.org/getting-started/installation/)中讨论的一个简单的查询祖父的示例：

```groovy
gremlin> g.V().has('name', 'hercules').out('father').out('father').values('name')
==>saturn
```

The query above can be read:

1. `g`: for the current graph traversal.

1. `V`: for all vertices in the graph

1. `has('name', 'hercules')`: filters the vertices down to those with name property "hercules" (there is only one).

1. `out('father')`: traverse outgoing father edge’s from Hercules.

1. `out('father')`: traverse outgoing father edge’s from Hercules’ father’s vertex (i.e. Jupiter).

1. `name`: get the name property of the "hercules" vertex’s grandfather.

上述查询可以这么理解：

1. `g`: 表示当前的图遍历

1. `V`: 表示图谱中所有的节点

1. `has('name', 'hercules')`: 将节点过滤为 `name` 属性为 `hercules` 的节点（只有一个）

1. `out('father')`: 从`hercules`开始遍历边为`father`的外向指出的节点

1. `out('father')`: 从'hercules'的父亲的节点(即Jupiter)开始遍历边为`father`的外向指出的节点

1. `name`: 返回'hercules'节点的祖父节点的`name`属性

Taken together, these steps form a path-like traversal query. Each step can be decomposed and its results demonstrated. This style of building up a traversal/query is useful when constructing larger, complex query chains.

这些步骤加在一起构成了一个类似路径的遍历查询。每个步骤都可以分解，并且可以证明其结果。这种构造遍历/查询的方式在构造较大，复杂的查询链时很有用。

```groovy
gremlin> g
==>graphtraversalsource[janusgraph[cql:127.0.0.1], standard]
gremlin> g.V().has('name', 'hercules')
==>v[24]
gremlin> g.V().has('name', 'hercules').out('father')
==>v[16]
gremlin> g.V().has('name', 'hercules').out('father').out('father')
==>v[20]
gremlin> g.V().has('name', 'hercules').out('father').out('father').values('name')
==>saturn
```

For a sanity check, it is usually good to look at the properties of each return, not the assigned long id.

对于健全性检查，通常最好查看每次return结果的属性，而不是默认分配的long类型id。

```groovy
gremlin> g.V().has('name', 'hercules').values('name')
==>hercules
gremlin> g.V().has('name', 'hercules').out('father').values('name')
==>jupiter
gremlin> g.V().has('name', 'hercules').out('father').out('father').values('name')
==>saturn
```

Note the related traversal that shows the entire father family tree branch of Hercules. This more complicated traversal is provided in order to demonstrate the flexibility and expressivity of the language. A competent grasp of Gremlin provides the JanusGraph user the ability to fluently navigate the underlying graph structure.

请注意这种关联的遍历显示了'Hercules'的整个父亲家谱分支。这种更复杂的遍历是为了演示语言的灵活性和表达性。对 Gremlin 的熟练掌握使得 JanusGraph 用户可以流畅地浏览底层图数据结构。

```groovy
gremlin> g.V().has('name', 'hercules').repeat(out('father')).emit().values('name')
==>jupiter
==>saturn
```

Some more traversal examples are provided below.

如下提供了更多的遍历示例：

```groovy
gremlin> hercules = g.V().has('name', 'hercules').next()
==>v[1536]
gremlin> g.V(hercules).out('father', 'mother').label()
==>god
==>human
gremlin> g.V(hercules).out('battled').label()
==>monster
==>monster
==>monster
gremlin> g.V(hercules).out('battled').valueMap()
==>{name=nemean}
==>{name=hydra}
==>{name=cerberus}
```

Each step (denoted by a separating .) is a function that operates on the objects emitted from the previous step. There are numerous steps in the Gremlin language (see [Gremlin Steps](https://tinkerpop.apache.org/docs/3.4.6/reference/#graph-traversal-steps)). By simply changing a step or order of the steps, different traversal semantics are enacted. The example below returns the name of all the people that have battled the same monsters as Hercules who themselves are not Hercules (i.e. "co-battlers" or perhaps, "allies").

每一个单步（以分隔符号表示）都是一个函数，对上一个单步返回的对象进行操作。Gremlin 语言有很多单步(请参阅 [Gremlin Steps](https://tinkerpop.apache.org/docs/3.4.6/reference/#graph-traversal-steps))。通过改变单步或者单步的顺序，可以实现不同的遍历语义。以下示例中返回了所有跟和'Hercules'战斗过的怪兽也战斗过的人的名称（即“同战者”或“盟友”）。

Given that *The Graph of the Gods* only has one battler (Hercules), another battler (for the sake of example) is added to the graph with Gremlin showcasing how vertices and edges are added to the graph.

鉴于给定的*众神图谱*只有一个战士(Hercules)。另一个战士（为了示例）被添加到图谱中，展示了使用 Gremlin 如何添加节点和边到图谱中。

```groovy
gremlin> theseus = graph.addVertex('human')
==>v[3328]
gremlin> theseus.property('name', 'theseus')
==>null
gremlin> cerberus = g.V().has('name', 'cerberus').next()
==>v[2816]
gremlin> battle = theseus.addEdge('battled', cerberus, 'time', 22)
==>e[7eo-2kg-iz9-268][3328-battled->2816]
gremlin> battle.values('time')
==>22
```

When adding a vertex, an optional vertex label can be provided. An edge label must be specified when adding edges. Properties as key-value pairs can be set on both vertices and edges. When a property key is defined with SET or LIST cardinality, `addProperty` must be used when adding a respective property to a vertex.

当添加节点时，节点标签时可选的。当添加边时，则必须指定边标签。节点和边都可以添加键值对属性。当使用SET或LIST定义属性的key时，则给节点添加属性时，必须使用`addProperty`。

```groovy
gremlin> g.V(hercules).as('h').out('battled').in('battled').where(neq('h')).values('name')
==>theseus
```

The example above has 4 chained functions: `out`, `in`, `except`, and `values` (i.e. `name` is shorthand for `values('name')`). The function signatures of each are itemized below, where `V` is vertex and `U` is any object, where `V` is a subset of `U`.

上述示例中有4个链接在一起的函数：`out`, `in`, `except` 和 `values`(即，`name`是`values('name')`的缩写)。每个函数前面列出如下，其中`V`是顶点，`U`是任意对象，且`V`是`U`的子集。

1. `out: V -> V`

1. `in: V -> V`

1. `except: U -> U`

1. `values: V -> U`

When chaining together functions, the incoming type must match the outgoing type, where `U` matches anything. Thus, the "co-battled/ally" traversal above is correct.

当函数被链接在一起时，传入的类型必须和传出类型匹配，其中`U`必须匹配任意类型，因此上述“同战者/盟友”遍历才是正确的。

**Note:** The Gremlin overview presented in this section focused on the Gremlin-Groovy language implementation used in the Gremlin Console. Refer to [Connecting to JanusGraph](https://docs.janusgraph.org/connecting/) for information about connecting to JanusGraph with other languages than Groovy and independent of the Gremlin Console.

**提示：** 本节中介绍的 Gremlin 概述着重于 Gremlin 控制台中使用的 Gremlin-Groovy 语言的实现。请参阅[连接到JanusGraph](https://docs.janusgraph.org/connecting/)以获取有关使用 Groovy 之外的其他语言（独立于Gremlin Console）连接到 JanusGraph 的信息。

## 迭代图遍历 Iterating the Traversal

One convenient feature of the Gremlin Console is that it automatically iterates all results from a query executed from the gremlin> prompt. This works well within the [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop) environment as it shows you the results as a String. As you transition towards writing a Gremlin application, it is important to understand how to iterate a traversal explicitly because your application’s traversals will not iterate automatically. These are some of the common ways to iterate the [Traversal](https://tinkerpop.apache.org/javadocs/3.4.6/full/org/apache/tinkerpop/gremlin/process/traversal/Traversal.html):

- `iterate()` - Zero results are expected or can be ignored.

- `next()` - Get one result. Make sure to check `hasNext()` first.

- `next(int n)` - Get the next `n` results. Make sure to check `hasNext()` first.

- `toList()` - Get all results as a list. If there are no results, an empty list is returned.

Gremlin 控制台的一项便利功能是它会自动迭代从 gremlin> 提示符下执行的查询的所有结果。这在[REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop)环境中效果很好，因为它将结果显示为字符串。当你过渡到编写 Gremlin 应用程序时，了解如何显式地遍历非常重要，因为您的应用程序不会自动进行遍历。这些是迭代[Traversal](https://tinkerpop.apache.org/javadocs/3.4.6/full/org/apache/tinkerpop/gremlin/process/traversal/Traversal.html)的一些常用方法：

- `iterate()` - 可以返回零个结果，或者忽略结果。

- `next()` - 得到接下来的一个结果，首先确认检查了`hasNext()`为`true`。

- `next(int n)` - 得到接下来的`n`个结果，首先确认检查了`hasNext()`为`true`。

- `toList()` - 将所有结果作为列表返回，如果没有结果，则返回一个空列表。

A Java code example is shown below to demonstrate these concepts:

如下的Java代码示例演示了这些概念：

```groovy
Traversal t = g.V().has("name", "pluto"); // Define a traversal
// Note the traversal is not executed/iterated yet
Vertex pluto = null;
if (t.hasNext()) { // Check if results are available
    pluto = g.V().has("name", "pluto").next(); // Get one result
    g.V(pluto).drop().iterate(); // Execute a traversal to drop pluto from graph
}
// Note the traversal can be cloned for reuse
Traversal tt = t.asAdmin().clone();
if (tt.hasNext()) {
    System.err.println("pluto was not dropped!");
}
List<Vertex> gods = g.V().hasLabel("god").toList(); // Find all the gods
```
