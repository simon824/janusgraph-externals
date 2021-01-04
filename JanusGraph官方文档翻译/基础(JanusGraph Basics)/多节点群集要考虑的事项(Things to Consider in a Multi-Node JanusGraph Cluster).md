# 多节点群集要考虑的事项(Things to Consider in a Multi-Node JanusGraph Cluster)

JanusGraph is a distributed graph database, which means it can be setup
in a multi-node cluster. However, when working in such an environment,
there are important things to consider. Furthermore, if configured
properly, JanusGraph handles some of these special considerations for
the user.

JanusGraph是一个分布式图形数据库，这意味着可以在多节点群集中进行设置。 但是，在这样的环境中工作时，需要考虑一些重要的事情。 此外，如果配置正确，JanusGraph将为用户处理其中的一些特殊注意事项。

## 动态图 Dynamic Graphs

JanusGraph supports [dynamically creating graphs](configured-graph-factory.md#configuredgraphfactory). This is
deviation from the way in which standard Gremlin Server implementations
allow one to access a graph. Traditionally, users create bindings to
graphs at server-start, by configuring the gremlin-server.yaml file
accordingly. For example, if the `graphs` section of your yaml file
looks like this:

JanusGraph支持动态创建图形。 这与标准Gremlin Server实现允许人们访问图形的方式有所不同。 传统上，用户通过在服务器启动时相应地配置gremlin-server.yaml文件来创建对图形的绑定。 例如，如果您的yaml文件的graphs部分如下所示：

```yaml
graphs {
  graph1: conf/graph1.properties,
  graph2: conf/graph2.properties
}
```

then you will access your graphs on the Gremlin Server using the fact
that the String `graph1` will be bound to the graph opened on the server
as per its supplied properties file, and the same holds true for
`graph2`.

However, if we use the `ConfiguredGraphFactory` to dynamically create
graphs, then those graphs are managed by the
[JanusGraphManager](configured-graph-factory.md#janusgraphmanager) and
the graph configurations are managed by the
[ConfigurationManagementGraph](configured-graph-factory.md#configurationmanagementgraph).
This is especially useful because it 1. allows you to define graph
configurations post-server-start and 2. allows the graph configurations
to be managed in a persisted and distributed nature across your
JanusGraph cluster.

To properly use the `ConfiguredGraphFactory`, you must configure every
Gremlin Server in your cluster to use the `JanusGraphManager` and the
`ConfigurationManagementGraph`. This procedure is explained in detail
[here](configured-graph-factory.md#configuring-janusgraph-server-for-configuredgraphfactory).

那么您将使用字符串graph1将根据其提供的属性文件绑定到在服务器上打开的图的事实来访问Gremlin服务器上的图，对于graph2同样如此。

但是，如果我们使用ConfiguredGraphFactory动态创建图，则这些图由JanusGraphManager管理，而图配置由ConfigurationManagementGraph管理。 这特别有用，因为它1.允许您在服务器启动后定义图形配置，并且2.允许在JanusGraph集群中以持久和分布式的方式管理图形配置。

要正确使用ConfiguredGraphFactory，必须将群集中的每个Gremlin服务器配置为使用JanusGraphManager和ConfigurationManagementGraph。

### 图参考一致性 Graph Reference Consistency 

If you configure all your JanusGraph servers to use the
[ConfiguredGraphFactory](configured-graph-factory.md#configuring-janusgraph-server-for-configuredgraphfactory),
JanusGraph will ensure all graph representations are-up-to-date across
all JanusGraph nodes in your cluster.

For example, if you update or delete the configuration to a graph on one
JanusGraph node, then we must evict that graph from the cache on *every
JanusGraph node in the cluster*. Otherwise, we may have inconsistent
graph representations across your cluster. JanusGraph automatically
handles this eviction using a messaging log queue through the backend
system that the graph in question is configured to use.

If one of your servers is configured incorrectly, then it may not be
able to successfully remove the graph from the cache.

如果您将所有JanusGraph服务器配置为使用ConfiguredGraphFactory，则JanusGraph将确保集群中所有JanusGraph节点上的所有图形表示都是最新的。

例如，如果您更新或删除一个JanusGraph节点上的图的配置，那么我们必须从集群中每个JanusGraph节点上的缓存中逐出该图。 否则，我们在整个集群中的图形表示可能会不一致。 JanusGraph使用消息日志队列通过后端系统自动处理此逐出，该问题系统已配置为使用该图形。

如果您的一台服务器配置不正确，则它可能无法成功从缓存中删除图形。

!!! important
    Any updates to your
    [TemplateConfiguration](configured-graph-factory.md#template-configuration)
    will not result in the updating of graphs/graph configurations
    previously created using said template configuration. If you want to
    update the individual graph configurations, you must do so using the
    [available update
    APIs](configured-graph-factory.md#updating-configurations). These
    update APIs will *then* result in the graph cache eviction across all
    JanusGraph nodes in your cluster.

!!! 重要
    对TemplateConfiguration的任何更新都不会导致以前使用所述模板配置创建的图形/图形配置的更新。 如果要更新各个图形配置，则必须使用可用的更新API进行更新。 然后，这些更新API将导致群集中所有JanusGraph节点之间的图形缓存逐出。

### 动态图和遍历绑定 Dynamic Graph and Traversal Bindings

JanusGraph has the ability to bind dynamically created graphs and their
traversal references to `<graph.graphname>` and
`<graph.graphname>_traversal`, respectively, across all JanusGraph nodes
in your cluster, with a maximum of a 20s lag for the binding to take
effect on any node in the cluster. Read more about this
[here](configured-graph-factory.md#graph-and-traversal-bindings).

JanusGraph accomplishes this by having each node in your cluster poll
the `ConfigurationManagementGraph` for all graphs for which you have
created configurations. The `JanusGraphManager` will then open said
graph with its persisted configuration, store it in its graph cache, and
bind the `<graph.graphname>` to the graph reference on the
`GremlinExecutor` as well as bind `<graph.graphname>_traversal` to the
graph’s traversal reference on the `GremlinExecutor`.

This allows you to access a dynamically created graph and its traversal
reference by their string bindings, on every node in your JanusGraph
cluster. This is particularly important to be able to work with Gremlin
Server clients and use [TinkerPops’s withRemote functionality](#using-tinkerpops-withremote-functionality).

JanusGraph能够将动态创建的图形及其遍历引用分别绑定到集群中所有JanusGraph节点上的<graph.graphname>和<graph.graphname> _traversal，最大滞后时间为20秒，绑定才能生效在群集中的任何节点上。在这里阅读更多有关此的内容。

JanusGraph通过让集群中的每个节点都轮询ConfigurationManagementGraph来查看已为其创建配置的所有图形的方法。然后，JanusGraphManager将使用其持久配置打开该图，将其存储在其图缓存中，并将<graph.graphname>绑定到GremlinExecutor上的图引用，并将<graph.graphname> _traversal绑定到图上的遍历引用。 GremlinExecutor。

这样，您就可以在JanusGraph集群中的每个节点上通过其字符串绑定访问动态创建的图形及其遍历引用。能够与Gremlin Server客户端一起使用并使用TinkerPops的withRemote功能，这一点尤其重要。

#### 设定 Set Up

To set up your cluster to bind dynamically created graphs and their
traversal references, you must:

1.  Configure each node to use the
    [ConfiguredGraphFactory](configured-graph-factory.md#configuring-JanusGraph-server-for-configuredgraphfactory).

2.  Configure each node to use a `JanusGraphChannelizer`, which injects
    lower-level Gremlin Server components, like the GremlinExecutor,
    into the JanusGraph project, giving us greater control of the
    Gremlin Server.

To configure each node to use a `JanusGraphChannelizer`, we must update
the `gremlin-server.yaml` to do so:

要设置群集以绑定动态创建的图形及其遍历引用，您必须：
1. 配置每个节点以使用ConfiguredGraphFactory。
2. 配置每个节点以使用JanusGraphChannelizer，该工具将较低级的Gremlin Server组件（例如GremlinExecutor）注入到JanusGraph项目中，从而使我们能够更好地控制Gremlin Server。
要将每个节点配置为使用JanusGraphChannelizer，我们必须更新gremlin-server.yaml以这样做：

    channelizer: org.janusgraph.channelizers.JanusGraphWebSocketChannelizer

There are a few channelizers you can choose from:
您可以从以下几种通道化器中进行选择：

1.  org.janusgraph.channelizers.JanusGraphWebSocketChannelizer
2.  org.janusgraph.channelizers.JanusGraphHttpChannelizer
3.  org.janusgraph.channelizers.JanusGraphNioChannelizer
4.  org.janusgraph.channelizers.JanusGraphWsAndHttpChannelizer

All of the channelizers share the exact same functionality as their TinkerPop counterparts.

所有的通道器都与TinkerPop的通道器具有完全相同的功能。

#### 使用TinkerPop的远程功能 Using TinkerPop’s withRemote Functionality

Since traversal references are bound on the JanusGraph servers, we can
make use of [TinkerPop’s withRemote
functionality](https://tinkerpop.apache.org/docs/{{ tinkerpop_version }}/reference/#connecting-via-remotegraph).
This will allow one to run gremlin queries locally, against a remote
graph reference. Traditionally, one runs queries against remote Gremlin
Servers by sending String script representations, which are processed on
the remote server and the response serialized and sent back. However,
TinkerPop also allows for the use of `remoteGraph`, which could be
useful if you are building a TinkerPop compliant graph infrastructure
that is easily transferable to multiple implementations.

To use this functionality in JanusGraph, we must first ensure we have
created a graph on the remote JanusGraph cluster:

由于遍历引用绑定在JanusGraph服务器上，因此我们可以利用TinkerPop的withRemote功能。 这样一来，您就可以针对远程图形引用在本地运行gremlin查询。 传统上，通过发送String脚本表示形式对远程Gremlin服务器运行查询，该脚本表示形式在远程服务器上进行处理，并将响应序列化并发送回。 但是，TinkerPop还允许使用remoteGraph，如果您要构建可轻松转移到多种实现的TinkerPop兼容图形基础架构，这可能会很有用。

要在JanusGraph中使用此功能，我们必须首先确保已在远程JanusGraph集群上创建了图：

``` ConfiguredGraphFactory.create("graph1"); ```

Next, we must wait 20 seconds to ensure the traversal reference is bound
on every JanusGraph node in the remote cluster.

Finally, we can locally make use of the `withRemote` method to access a
local reference to a remote graph:

接下来，我们必须等待20秒，以确保遍历引用绑定到远程集群中的每个JanusGraph节点上。

最后，我们可以在本地使用withRemote方法访问对远程图的本地引用：
```groovy
gremlin> cluster = Cluster.open('conf/remote-objects.yaml')
==>localhost/127.0.0.1:8182
gremlin> graph = EmptyGraph.instance()
==>emptygraph[empty]
gremlin> g = graph.traversal().withRemote(DriverRemoteConnection.using(cluster, "graph1_traversal"))
==>graphtraversalsource[emptygraph[empty], standard]
```
为了完成，上面的conf / remote-objects.yaml应该告诉Cluster API如何访问远程JanusGraph服务器。 例如，它可能看起来像：

For completion, the above `conf/remote-objects.yaml` should tell the
`Cluster` API how to access the remote JanusGraph servers; for example,
it may look like:
```yaml
hosts: [remoteaddress1.com, remoteaddress2.com]
port: 8182
username: admin
password: password
connectionPool: { enableSsl: true }
serializer: { className: org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0, config: { ioRegistries: [org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry] }}
```
