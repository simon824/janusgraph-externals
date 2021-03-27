# 配置 Configuration

A JanusGraph graph database cluster consists of one or multiple JanusGraph instances. To open a JanusGraph instance, a configuration has to be provided which specifies how JanusGraph should be set up.

一个 JanusGraph 图数据库集群由一个或多个 JanusGraph 实例组成。要打开一个 JanusGraph 实例，必须提供一个指定 JanusGraph 应该如何工作运行的配置。

A JanusGraph configuration specifies which components JanusGraph should use, controls all operational aspects of a JanusGraph deployment, and provides a number of tuning options to get maximum performance from a JanusGraph cluster.

JanusGraph 配置指定 JanusGraph 应该使用哪些组件，控制 JanusGraph 部署过程中所有操作的点，还提供了许多可调整的选项使得 JanusGraph 集群可以获得最佳性能。

At a minimum, a JanusGraph configuration must define the persistence engine that JanusGraph should use as a storage backend. [Storage Backends](https://docs.janusgraph.org/storage-backend/) lists all supported persistence engines and how to configure them respectively. If advanced graph query support (e.g full-text search, geo search, or range queries) is required an additional indexing backend must be configured. See [Index Backends](https://docs.janusgraph.org/index-backend/search-predicates/) for details. If query performance is a concern, then caching should be enabled. Cache configuration and tuning is described in [JanusGraph Cache](https://docs.janusgraph.org/basics/configuration/#caching).

至少，JanusGraph 配置必须要定义用来作为存储后端的持久层引擎。[存储后端](https://docs.janusgraph.org/storage-backend/)列出了所有支持的持久层引擎以及分别如何配置它们。如果需要高级图查询支持（例如，全文搜索，地理搜索或者范围查询），则需要额外配置索引后端。详细请查看[索引后端](https://docs.janusgraph.org/index-backend/search-predicates/)。如果考虑到查询性能，则应该启用缓存。缓存配置和调整选项在[JanusGraph 缓存](https://docs.janusgraph.org/basics/configuration/#caching)中进行了描述。

## 配置示例 Example Configurations

Below are some example configuration files to demonstrate how to configure the most commonly used storage backends, indexing systems, and performance components. This covers only a tiny portion of the available configuration options. Refer to [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) for the complete list of all options.

以下是一些示例配置文件，用来演示如何配置最常用的存储后端，索引系统和性能组件。这里展示的内容仅涵盖了可用配置选项的一小部分。关于所有配置选项的完整列表，请参考[配置参考](https://docs.janusgraph.org/basics/configuration-reference/)。

### Cassandra+Elasticsearch

Sets up JanusGraph to use the Cassandra persistence engine running locally and a remote Elastic search indexing system:

使用本地运行的 Cassandra 作为持久层引擎和远程的 Elasticsearch 索引系统来设置 JanusGraph：

```properties
storage.backend=cql
storage.hostname=localhost

index.search.backend=elasticsearch
index.search.hostname=100.100.101.1, 100.100.101.2
index.search.elasticsearch.client-only=true
```

### HBase+Caching

Sets up JanusGraph to use the HBase persistence engine running remotely and uses JanusGraph’s caching component for better performance. 

使用远程运行的 HBase 作为持久层引擎来设置 JanusGraph，并使用 JanusGraph 的缓存组件来获得更好的性能。

```properties
storage.backend=hbase
storage.hostname=100.100.101.1
storage.port=2181

cache.db-cache = true
cache.db-cache-clean-wait = 20
cache.db-cache-time = 180000
cache.db-cache-size = 0.5
```

### BerkeleyDB

Sets up JanusGraph to use BerkeleyDB as an embedded persistence engine with Elasticsearch as an embedded indexing system. 

使用 BerkeleyDB 作为嵌入式的持久层引擎和 Elasticsearch 作为嵌入式索引系统来设置 JanusGraph。

```properties
storage.backend=berkeleyje
storage.directory=/tmp/graph

index.search.backend=elasticsearch
index.search.directory=/tmp/searchindex
index.search.elasticsearch.client-only=false
index.search.elasticsearch.local-mode=true
```

[Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) describes all of these configuration options in detail. The `conf` directory of the JanusGraph distribution contains additional configuration examples.

[配置参考](https://docs.janusgraph.org/basics/configuration-reference/)详细描述了所有这些配置选项。JanusGraph 发行版的`conf`目录中包含了其他配置示例。

### 进一步示例 Further Examples

There are several example configuration files in the `conf/` directory that can be used to get started with JanusGraph quickly. Paths to these files can be passed to `JanusGraphFactory.open(...)` as shown below:

`conf/`目录中包含一些配置文件，可以用来快速开始使用 JanusGraph。这些配置文件的路径可以作为参数传递到`JanusGraphFactory.open(...)`方法中，如下所示：

```groovy
// Connect to Cassandra on localhost using a default configuration
graph = JanusGraphFactory.open("conf/janusgraph-cql.properties")
// Connect to HBase on localhost using a default configuration
graph = JanusGraphFactory.open("conf/janusgraph-hbase.properties")
```

## 使用配置 Using Configuration

How the configuration is provided to JanusGraph depends on the instantiation mode.

如何将配置提供给 JanusGraph 取决于它的实例化模式。

### JanusGraphFactory

#### Gremlin控制台 Gremlin Console

The JanusGraph distribution contains a command line Gremlin Console which makes it easy to get started and interact with JanusGraph. Invoke `bin/gremlin.sh` (Unix/Linux) or `bin/gremlin.bat` (Windows) to start the Console and then open a JanusGraph graph using the factory with the configuration stored in an accessible properties configuration file: 

JanusGraph 发行版包含一个基于命令行的Gremlin控制台，可以使用它轻松上手并与 JanusGraph 进行交互。调用 `bin/gremlin.sh` (Unix/Linux) 或 `bin/gremlin.bat` (Windows) 来启动控制台，然后使用工厂方法和存储在可访问的properties文件中的配置来打开一个 JanusGraph 图谱：

```groovy
graph = JanusGraphFactory.open('path/to/configuration.properties')
```

#### 嵌入式JanusGraph JanusGraph Embedded

JanusGraphFactory can also be used to open an embedded JanusGraph graph instance from within a JVM-based user application. In that case, JanusGraph is part of the user application and the application can call upon JanusGraph directly through its public API.

`JanusGraphFactory` 也可以用于从基于JVM的用户应用程序中打开嵌入式的 JanusGraph 实例。在这种情况下，JanusGraph 是用户应用程序的一部分，并且该应用程序可以通过其公开的API直接调用 JanusGraph。

#### 简写代码 Short Codes

If the JanusGraph graph cluster has been previously configured and/or only the storage backend needs to be defined, JanusGraphFactory accepts a colon-separated string representation of the storage backend name and hostname or directory. 

如果已经配置过 JanusGraph 图谱集群或者只需要定义存储后端，则`JanusGraphFactory`接受一个冒号分割的字符串来表示存储后端的名称和主机名或者目录。

```groovy
graph = JanusGraphFactory.open('cql:localhost')
graph = JanusGraphFactory.open('berkeleyje:/tmp/graph')
```

### JanusGraph服务 JanusGraph Server

JanusGraph, by itself, is simply a set of jar files with no thread of execution. There are two basic patterns for connecting to, and using a JanusGraph database:

1. JanusGraph can be used by embedding JanusGraph calls in a client program where the program provides the thread of execution.

1. JanusGraph packages a long running server process that, when started, allows a remote client or logic running in a separate program to make JanusGraph calls. This long running server process is called **JanusGraph Server**.

JanusGraph 本身是一组没有执行线程的jar文件。有两种连接和使用 JanusGraph 数据库的基本模式：

1. JanusGraph 可以通过将 JanusGraph 嵌入到客户端程序中来使用，该客户端程序提供执行线程。

1. JanusGraph 打包了一个长时间运行的服务进程，启动该进程后，允许远程客户端或在单独程序中运行的逻辑单元对其进行调用。这个长时间运行的服务进程称为**JanusGraph Server**。

For the JanusGraph Server, JanusGraph uses [Gremlin Server](https://tinkerpop.apache.org/docs/3.4.6/reference/#gremlin-server) of the [Apache TinkerPop](https://tinkerpop.apache.org/) stack to service client requests. JanusGraph provides an out-of-the-box configuration for a quick start with JanusGraph Server, but the configuration can be changed to provide a wide range of server capabilities.

对于 JanusGraph 服务，它使用 [Apache TinkerPop](https://tinkerpop.apache.org/) 技术栈中的 [Gremlin Server](https://tinkerpop.apache.org/docs/3.4.6/reference/#gremlin-server) 来响应客户端请求。JanusGraph 提供了开箱即用的配置来快速启动 JanusGraph 服务，你也可以修改这些配置来提供更广泛的服务端功能。

Configuring JanusGraph Server is accomplished through a JanusGraph Server yaml configuration file located in the ./conf/gremlin-server directory in the JanusGraph distribution. To configure JanusGraph Server with a graph instance (`JanusGraph`), the JanusGraph Server configuration file requires the following settings:

可以通过 JanusGraph 发行版的 `./conf/gremlin-server` 目录中的yaml配置文件来配置 JanusGraph Server。要通过图实例(`JanusGraph`)配置 JanusGraph Server，则需要在 JanusGraph Server 的配置文件中作如下配置：

```yaml
graphs: {
  graph: conf/janusgraph-berkeleyje.properties
}
scriptEngines: {
  gremlin-groovy: {
    plugins: { org.janusgraph.graphdb.tinkerpop.plugin.JanusGraphGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.server.jsr223.GremlinServerGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.tinkergraph.jsr223.TinkerGraphGremlinPlugin: {},
               org.apache.tinkerpop.gremlin.jsr223.ImportGremlinPlugin: {classImports: [java.lang.Math], methodImports: [java.lang.Math#*]},
               org.apache.tinkerpop.gremlin.jsr223.ScriptFileGremlinPlugin: {files: [scripts/empty-sample.groovy]}}}}
```

The entry for `graphs` defines the bindings to specific `JanusGraph` configurations. In the above case it binds `graph` to a JanusGraph configuration at `conf/janusgraph-berkeleyje.properties`. The `plugins` entry enables the JanusGraph Gremlin Plugin, which enables auto-imports of JanusGraph classes so that they can be referenced in remotely submitted scripts.

`graphs` 配置项定义了具体的 `JanusGraph` 配置的绑定配置。在上述示例中，`graph` 配置项将 JanusGraph 配置和 `conf/janusgraph-berkeleyje.properties` 文件配置进行了绑定。`plugins` 配置项启用了 Gremlin 插件，允许自动导入 JanusGraph 的类文件到 Gremlin Server 中，以便远程提交的脚本可以引用到这些类。

Learn more about configuring and using JanusGraph Server in [JanusGraph Server](https://docs.janusgraph.org/basics/server/).

可以在 [JanusGraph Server](https://docs.janusgraph.org/basics/server/) 章节中了解更多配置和使用它的内容。

#### 服务端发行包 Server Distribution

The JanusGraph zip file contains a quick start server component that helps make it easier to get started with Gremlin Server and JanusGraph. Invoke `bin/janusgraph.sh` start to start Gremlin Server with Cassandra and Elasticsearch.

JanusGraph 的zip文件包含了一个用于快速启动的服务组件，可以帮助你更轻松地开始使用 Gremlin Server 和 JanusGraph。执行 `bin/janusgraph.sh` 开始用 Cassandra 和 Elasticsearch 来启动 Gremlin Server。

**Note:** For security reasons Elasticsearch and therefore `janusgraph.sh` must be run under a non-root account

**提示：** 出于安全原因，Elasticsearch 和 `janusgraph.sh` 必须在非root用户下运行。 

**Note:** Starting with 0.5.1, this is just included in the full package version.

**提示：** 从0.5.1版本开始，它仅包含在完整的软件包版本中。

## 全局配置 Global Configuration

JanusGraph distinguishes between local and global configuration options. Local configuration options apply to an individual JanusGraph instance. Global configuration options apply to all instances in a cluster. More specifically, JanusGraph distinguishes the following five scopes for configuration options:

- **LOCAL**: These options only apply to an individual JanusGraph instance and are specified in the configuration provided when initializing the JanusGraph instance.

- **MASKABLE**: These configuration options can be overwritten for an individual JanusGraph instance by the local configuration file. If the local configuration file does not specify the option, its value is read from the global JanusGraph cluster configuration.

- **GLOBAL**: These options are always read from the cluster configuration and cannot be overwritten on an instance basis.

- **GLOBAL_OFFLINE**: Like *GLOBAL*, but changing these options requires a cluster restart to ensure that the value is the same across the entire cluster.

- **FIXED**: Like *GLOBAL*, but the value cannot be changed once the JanusGraph cluster is initialized.

JanusGraph 区分本地配置选项和全局配置选项。本地配置选项适用于单个 JanusGraph 实例。全局配置选项适用于群集中的所有实例。更具体地说，JanusGraph 的配置选项分为以下五种范围： 

- **本地**: 这些选项仅适用于单个 JanusGraph 实例，并且在初始化 JanusGraph 实例的配置中指定。

- **可屏蔽**: 本地配置文件可以为单个 JanusGraph 实例覆盖这些配置项。如果本地配置文件中未指定该选项，则从全局 JanusGraph 集群配置中读取值。

- **全局**: 这些配置项始终从集群配置中读取，不能根据实例进行覆盖。 

- **全局离线**: 跟*全局*配置类似，但是更改这些配置项需要重新启动集群，确保整个集群中的配置值都相同。

- **固定**: 跟*全局*配置类似，但是一旦初始化 JanusGraph 集群就无法更改值。

When the first JanusGraph instance in a cluster is started, the global configuration options are initialized from the provided local configuration file. Subsequently changing global configuration options is done through JanusGraph’s management API. To access the management API, call `g.getManagementSystem()` on an open JanusGraph instance handle `g`. For example, to change the default caching behavior on a JanusGraph cluster: 

启动集群中的第一个 JanusGraph 实例时，将从本地的配置文件中初始化全局配置项。随后，通过 JanusGraph 的管理API更改全局配置项。要访问管理API，需要通过已打开的 JanusGraph 实例的句柄`g`调用`g.getManagementSystem()`方法。例如，要更改 JanusGraph 群集上默认的缓存行为： 

*此处官方文档的描述与代码示例不一致，请读者知悉。*

```groovy
mgmt = graph.openManagement()
mgmt.get('cache.db-cache')
// Prints the current config setting
mgmt.set('cache.db-cache', true)
// Changes option
mgmt.get('cache.db-cache')
// Prints 'true'
mgmt.commit()
// Changes take effect
```

### 修改离线配置 Changing Offline Options

Changing configuration options does not affect running instances and only applies to newly started ones. Changing *GLOBAL_OFFLINE* configuration options requires restarting the cluster so that the changes take effect immediately for all instances. To change *GLOBAL_OFFLINE* options follow these steps:

- Close all but one JanusGraph instance in the cluster

- Connect to the single instance

- Ensure all running transactions are closed

- Ensure no new transactions are started (i.e. the cluster must be offline)

- Open the management API

- Change the configuration option(s)

- Call commit which will automatically shut down the graph instance

- Restart all instances

更改配置选项不会影响正在运行的实例，而仅适用于新启动的实例。更改*全局离线*配置选项需要重新启动群集，以便更改对所有实例立即生效。要更改*全局离线*选项，请按照以下步骤操作： 

- 关闭集群中所有的 JanusGraph 实例，只留下一个实例

- 连接到这个单个实例

- 确保所有正在运行的事务都已经关闭

- 确保没有新启动的事务（即集群必须离线）

- 打开 management API

- 更改配置项

- 提交更改，这也会自动关闭图实例

- 重启所有实例

Refer to the full list of configuration options in [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) for more information including the configuration scope of each option.

关于更多的配置信息，包括每个配置项的范围，请参考[配置参考](https://docs.janusgraph.org/basics/configuration-reference/)。
