# Monitoring JanusGraph

<a name="monitoring-janusgraph"></a>
# Monitoring JanusGraph
<a name="metrics-in-janusgraph"></a>
## Metrics in JanusGraph
JanusGraph supports [Metrics](https://dropwizard.io/). JanusGraph can measure the following:

- The number of transactions begun, committed, and rolled back<br />
- The number of attempts and failures of each storage backend operation type<br />
- The response time distribution of each storage backend operation type

JanusGraph支持指标。 JanusGraph可以测量以下内容：<br />• 开始，落实和回滚的事务数<br />• 每种存储后端操作类型的尝试次数和失败次数<br />• 每种存储后端操作类型的响应时间分布
<a name="configuring-metrics-collection"></a>
### Configuring Metrics Collection
To enable Metrics collection, set the following in JanusGraph’s properties file:<br />要启用指标收集，请在JanusGraph的属性文件中设置以下内容：
```
# Required to enable Metrics in JanusGraph
metrics.enabled = true
```
This setting makes JanusGraph record measurements at runtime using Metrics classes like Timer, Counter, Histogram, etc. To access these measurements, one or more Metrics reporters must be configured as described in the section [Configuring Metrics Reporting](https://docs.janusgraph.org/advanced-topics/monitoring/#configuring-metrics-reporting).<br />此设置使JanusGraph在运行时使用计时器，计数器，直方图等度量标准记录测量。要访问这些度量，必须按照配置度量报告一节中的说明配置一个或多个度量报告程序。
<a name="customizing-the-default-metric-names"></a>
#### Customizing the Default Metric Names 自定义默认指标名称
JanusGraph prefixes all metric names with "org.janusgraph" by default. This prefix can be set through the `metrics.prefix` configuration property. For example, to shorten the default "org.janusgraph" prefix to just "janusgraph":<br />默认情况下，JanusGraph为所有度量标准名称添加“ org.janusgraph”前缀。可以通过metrics.prefix配置属性设置此前缀。例如，将默认的“ org.janusgraph”前缀缩短为“ janusgraph”：
```
# Optional
metrics.prefix = janusgraph
```
<a name="transaction-specific-metrics-names"></a>
#### Transaction-Specific Metrics Names 特定交易指标名称
Each JanusGraph transaction may optionally specify its own Metrics name prefix, overriding both the default Metrics name prefix and the `metrics.prefix` configuration property. For example, the prefix could be changed to the name of the frontend application that opened the JanusGraph transaction. Note that Metrics maintains a ConcurrentHashMap of metric names and their associated objects in memory, so it’s probably a good idea to keep the number of distinct metric prefixes small.<br />每个JanusGraph事务可以选择指定其自己的Metrics名称前缀，从而覆盖默认Metrics名称前缀和metrics.prefix配置属性。例如，可以将前缀更改为打开JanusGraph事务的前端应用程序的名称。请注意，Metrics在内存中维护着一个Metrics名称及其相关对象的ConcurrentHashMap，因此，保持不同Metric前缀的数量较小可能是一个好主意。<br />To do this, call `TransactionBuilder.setMetricsPrefix(String)`:
```
JanusGraph graph = ...;
TransactionBuilder tbuilder = graph.buildTransaction();
JanusGraphTransaction tx = tbuilder.groupName("foobar").start();
```
<a name="separating-metrics-by-backend-store"></a>
#### Separating Metrics by Backend Store 通过后端存储分离指标
JanusGraph combines the Metrics for its various internal storage backend handles by default. All Metrics for storage backend interactions follow the pattern "<prefix>.stores.<opname>", regardless of whether they come from the ID store, edge store, etc. When `metrics.merge-basic-metrics = false` is set in JanusGraph’s properties file, the "stores" string in metric names is replaced by "idStore", "edgeStore", "vertexIndexStore", or "edgeIndexStore".<br />默认情况下，JanusGraph将指标用于其各种内部存储后端句柄。所有存储后端交互的度量均遵循“ <prefix> .stores。<opname>”模式，无论它们是否来自ID存储，边缘存储等。在JanusGraph的metrics中设置metrics.merge-basic-metrics = false时属性文件中，度量标准名称中的“ stores”字符串将替换为“ idStore”，“ edgeStore”，“ vertexIndexStore”或“ edgeIndexStore”。
<a name="configuring-metrics-reporting"></a>
## Configuring Metrics Reporting 配置指标报告
JanusGraph supports the following Metrics reporters:

- [Console](https://docs.janusgraph.org/advanced-topics/monitoring/#console-reporter)
- [CSV](https://docs.janusgraph.org/advanced-topics/monitoring/#csv-file-reporter)
- [Ganglia](https://docs.janusgraph.org/advanced-topics/monitoring/#ganglia-reporter)
- [Graphite](https://docs.janusgraph.org/advanced-topics/monitoring/#graphite-reporter)
- [JMX](https://docs.janusgraph.org/advanced-topics/monitoring/#jmx-reporter)
- [Slf4j](https://docs.janusgraph.org/advanced-topics/monitoring/#slf4j-reporter)
- [User-provided/Custom](https://docs.janusgraph.org/advanced-topics/monitoring/#custom-reporter)

Each reporter type is independent of and can coexist with the others. For example, it’s possible to configure Ganglia, JMX, and Slf4j Metrics reporters to operate simultaneously. Just set all their respective configuration keys in janusgraph.properties (and enable metrics as directed above). <br />每种报告者类型独立于其他报告者，并且可以共存。例如，可以将Ganglia，JMX和Slf4j Metrics报告器配置为同时运行。只需在janusgraph.properties中设置它们各自的配置键即可（并按照上面的指示启用指标）。
<a name="console-reporter"></a>
### Console Reporter
Metrics Console Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.console.interval | yes | Milliseconds to wait between dumping metrics to the console | null |

Example janusgraph.properties snippet that prints metrics to the console once a minute:
```
metrics.enabled = true
# Required; specify logging interval in milliseconds
metrics.console.interval = 60000
```
<a name="csv-file-reporter"></a>
### CSV File Reporter
Metrics CSV Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.csv.interval | yes | Milliseconds to wait between writing CSV lines | null |
| metrics.csv.directory | yes | Directory in which CSV files are written (will be created if it does not exist) | null |

Example janusgraph.properties snippet that writes CSV files once a minute to the directory `./foo/bar/` (relative to the process’s working directory):
```
metrics.enabled = true
# Required; specify logging interval in milliseconds
metrics.csv.interval = 60000
metrics.csv.directory = foo/bar
```
<a name="ganglia-reporter"></a>
### Ganglia Reporter
Note<br />Configuration of [Ganglia](http://ganglia.sourceforge.net/) requires an additional library that is not packaged with JanusGraph due to its LGPL licensing that conflicts with the JanusGraph’s Apache 2.0 License. To run with Ganglia monitoring, download the `org.acplt:oncrpc` jar from [here](https://repo1.maven.org/maven2/org/acplt/oncrpc/1.0.7/) and copy it to the JanusGraph `/lib` directory before starting the server.<br />Ganglia的配置需要附加的库，该库未与JanusGraph一起打包，因为它的LGPL许可与JanusGraph的Apache 2.0许可冲突。 要使用Ganglia监视运行，请从此处下载org.acplt：oncrpc jar并将其复制到JanusGraph / lib目录，然后再启动服务器。<br />Metrics Ganglia Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.ganglia.hostname | yes | Unicast host or multicast group to which our Metrics are sent | null |
| metrics.ganglia.interval | yes | Milliseconds to wait between sending datagrams | null |
| metrics.ganglia.port | no | UDP port to which we send Metrics datagrams | 8649 |
| metrics.ganglia.addressing-mode | no | Must be "unicast" or "multicast" | unicast |
| metrics.ganglia.ttl | no | Multicast datagram TTL; ignore for unicast | 1 |
| metrics.ganglia.protocol-31 | no | Boolean; true to use Ganglia protocol 3.1, false to use 3.0 | true |
| metrics.ganglia.uuid | no | [Host UUID to report instead of IP:hostname](https://github.com/ganglia/monitor-core/wiki/UUIDSources) | null |
| metrics.ganglia.spoof | no | [Override IP:hostname reported to Ganglia](https://github.com/ganglia/monitor-core/wiki/Gmetric-Spoofing) | null |

Example janusgraph.properties snippet that sends unicast UDP datagrams to localhost on the default port once every 30 seconds:<br />示例janusgraph.properties片段每30秒发送一次单播UDP数据报到默认端口上的localhost：
```
metrics.enabled = true
# Required; IP or hostname string
metrics.ganglia.hostname = 127.0.0.1
# Required; specify logging interval in milliseconds
metrics.ganglia.interval = 30000
```
Example janusgraph.properties snippet that sends unicast UDP datagrams to a non-default destination port and which also spoofs the IP and hostname reported to Ganglia:<br />示例janusgraph.properties片段将单播UDP数据报发送到非默认目标端口，并且还欺骗了报告给Ganglia的IP和主机名：
```
metrics.enabled = true
# Required; IP or hostname string
metrics.ganglia.hostname = 1.2.3.4
# Required; specify logging interval in milliseconds
metrics.ganglia.interval = 60000
# Optional
metrics.ganglia.port = 6789
metrics.ganglia.spoof = 10.0.0.1:zombo.com
```
<a name="graphite-reporter"></a>
### Graphite Reporter
Metrics Graphite Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.graphite.hostname | yes | IP address or hostname to which [Graphite plaintext protocol](https://graphite.readthedocs.org/en/latest/feeding-carbon.html#the-plaintext-protocol) data are sent | null |
| metrics.graphite.interval | yes | Milliseconds to wait between pushing data to Graphite | null |
| metrics.graphite.port | no | Port to which Graphite plaintext protocol reports are sent | 2003 |
| metrics.graphite.prefix | no | Arbitrary string prepended to all metric names sent to Graphite | null |

Example janusgraph.properties snippet that sends metrics to a Graphite server on 192.168.0.1 every minute:
```
metrics.enabled = true
# Required; IP or hostname string
metrics.graphite.hostname = 192.168.0.1
# Required; specify logging interval in milliseconds
metrics.graphite.interval = 60000
```
<a name="jmx-reporter"></a>
### JMX Reporter
Metrics JMX Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.jmx.enabled | yes | Boolean | false |
| metrics.jmx.domain | no | Metrics will appear in this JMX domain | Metrics’s own default |
| metrics.jmx.agentid | no | Metrics will be reported with this JMX agent ID | Metrics’s own default |

Example janusgraph.properties snippet:
```
metrics.enabled = true
# Required
metrics.jmx.enabled = true
# Optional; if omitted, then Metrics uses its default values
metrics.jmx.domain = foo
metrics.jmx.agentid = baz
```
<a name="slf4j-reporter"></a>
### Slf4j Reporter
Metrics Slf4j Reporter Configuration Options

| Config Key | Required? | Value | Default |
| :--- | :--- | :--- | :--- |
| metrics.slf4j.interval | yes | Milliseconds to wait between dumping metrics to the logger | null |
| metrics.slf4j.logger | no | Slf4j logger name to use | "metrics" |

Example janusgraph.properties snippet that logs metrics once a minute to the logger named `foo`:
```
metrics.enabled = true
# Required; specify logging interval in milliseconds
metrics.slf4j.interval = 60000
# Optional; uses Metrics default when unset
metrics.slf4j.logger = foo
```
<a name="user-providedcustom-reporter"></a>
### User-Provided/Custom Reporter
In case the Metrics reporter configuration options listed above are insufficient, JanusGraph provides a utility method to access the single `MetricRegistry` instance which holds all of its measurements.<br />如果上面列出的Metrics报告程序配置选项不足，JanusGraph提供了一种实用程序方法来访问保存所有测量值的单个MetricRegistry实例。
```
com.codahale.metrics.MetricRegistry janusgraphRegistry = 
    org.janusgraph.util.stats.MetricManager.INSTANCE.getRegistry();
```
Code that accesses `janusgraphRegistry` this way can then attach non-standard reporter types or standard reporter types with exotic configurations to `janusgraphRegistry`. This approach is also useful if the surrounding application already has a framework for Metrics reporter configuration, or if the application needs multiple differently-configured instances of one of JanusGraph’s supported reporter types. For instance, one could use this approach to setup multiple unicast Graphite reporters whereas JanusGraph’s properties configuration is limited to just one Graphite reporter.<br />然后，以这种方式访问janusgraphRegistry的代码可以将非标准报告程序类型或具有特殊配置的标准报告程序类型附加到janusgraphRegistry。 如果周围的应用程序已经具有用于Metrics报告器配置的框架，或者该应用程序需要JanusGraph支持的报告器类型之一的多个不同配置的实例，则此方法也很有用。 例如，可以使用这种方法来设置多个单播Graphite报告器，而JanusGraph的属性配置仅限于一个Graphite报告器。
