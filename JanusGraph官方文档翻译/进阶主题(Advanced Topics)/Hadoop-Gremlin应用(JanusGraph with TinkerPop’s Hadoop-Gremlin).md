# JanusGraph with TinkerPop’s Hadoop-Gremlin

<a name="janusgraph-with-tinkerpops-hadoop-gremlin"></a>
# JanusGraph with TinkerPop’s Hadoop-Gremlin
This chapter describes how to leverage [Apache Hadoop](https://hadoop.apache.org/) and [Apache Spark](https://spark.apache.org/) to configure JanusGraph for distributed graph processing. These steps will provide an overview on how to get started with those projects, but please refer to those project communities to become more deeply familiar with them.

JanusGraph-Hadoop works with TinkerPop’s [hadoop-gremlin](https://tinkerpop.apache.org/docs/3.4.6/reference/#hadoop-gremlin) package for general-purpose OLAP.

For the scope of the example below, Apache Spark is the computing framework and Apache Cassandra is the storage backend. The directions can be followed with other packages with minor changes to the configuration properties.

- Note
The examples in this chapter are based on running Spark in local mode or standalone cluster mode. Additional configuration is required when using Spark on YARN or Mesos.

本章介绍如何利用Apache Hadoop和Apache Spark配置JanusGraph进行分布式图形处理。这些步骤将概述如何开始使用这些项目，但是请参考那些项目社区以更加熟悉它们。

JanusGraph-Hadoop与TinkerPop的hadoop-gremlin软件包配合使用，用于通用OLAP。

在以下示例的范围内，Apache Spark是计算框架，Apache Cassandra是存储后端。其他软件包对配置属性进行了细微更改后，可以遵循这些说明。

- 注意
本章中的示例基于在本地模式或独立集群模式下运行Spark。在YARN或Mesos上使用Spark时，需要其他配置。

## Configuring Hadoop for Running OLAP 配置 Hadoop 跑 OLAP
For running OLAP queries from the Gremlin Console, a few prerequisites need to be fulfilled. You will need to add the Hadoop configuration directory into the `CLASSPATH`, and the configuration directory needs to point to a live Hadoop cluster.

Hadoop provides a distributed access-controlled file system. The Hadoop file system is used by Spark workers running on different machines to have a common source for file based operations. The intermediate computations of various OLAP queries may be persisted on the Hadoop file system.

For configuring a single node Hadoop cluster, please refer to official [Apache Hadoop Docs](https://hadoop.apache.org/docs/r2.7.7/hadoop-project-dist/hadoop-common/SingleCluster.html)

Once you have a Hadoop cluster up and running, we will need to specify the Hadoop configuration files in the `CLASSPATH`. The below document expects that you have those configuration files located under `/etc/hadoop/conf`.

Once verified, follow the below steps to add the Hadoop configuration to the `CLASSPATH` and start the Gremlin Console, which will play the role of the Spark driver program.

为了从Gremlin控制台运行OLAP查询，需要满足一些先决条件。您将需要将Hadoop配置目录添加到CLASSPATH中，并且该配置目录需要指向活动的Hadoop集群。

Hadoop提供了分布式访问控制的文件系统。在不同计算机上运行的Spark工作者使用Hadoop文件系统来为基于文件的操作提供通用资源。各种OLAP查询的中间计算可以保留在Hadoop文件系统上。

有关配置单节点Hadoop集群的信息，请参阅官方的Apache Hadoop文档

Hadoop集群启动并运行后，我们将需要在CLASSPATH中指定Hadoop配置文件。以下文档期望您将这些配置文件放在/ etc / hadoop / conf下。

验证后，请按照以下步骤将Hadoop配置添加到CLASSPATH并启动Gremlin Console，该控制台将充当Spark驱动程序的角色。
```
export HADOOP_CONF_DIR=/etc/hadoop/conf
export CLASSPATH=$HADOOP_CONF_DIR
bin/gremlin.sh
```
Once the path to Hadoop configuration has been added to the `CLASSPATH`, we can verify whether the Gremlin Console can access the Hadoop cluster by following these quick steps:

将Hadoop配置的路径添加到CLASSPATH后，我们可以按照以下快速步骤验证Gremlin Console是否可以访问Hadoop集群：
```
gremlin> hdfs
==>storage[org.apache.hadoop.fs.LocalFileSystem@65bb9029] // BAD
gremlin> hdfs
==>storage[DFS[DFSClient[clientName=DFSClient_NONMAPREDUCE_1229457199_1, ugi=user (auth:SIMPLE)]]] // GOOD
```

## OLAP Traversals
JanusGraph-Hadoop works with TinkerPop’s hadoop-gremlin package for general-purpose OLAP to traverse over the graph, and parallelize queries by leveraging Apache Spark.

JanusGraph-Hadoop与TinkerPop的hadoop-gremlin软件包配合使用，用于通用OLAP，以遍历图形并利用Apache Spark并行化查询。

### OLAP Traversals with Spark Local
OLAP Examples below are showing configuration examples for directly supported backends by JanusGraph. Additional configuration will be needed that is specific to that storage backend. The configuration is specified by the `gremlin.hadoop.graphReader` property which specifies the class to read data from the storage backend.

JanusGraph directly supports following graphReader classes:

- `CqlInputFormat` for use with Cassandra 3
- `CassandraInputFormat` for use with Cassandra 2
- `HBaseInputFormat` and `HBaseSnapshotInputFormat` for use with HBase

The following `.properties` files can be used to connect a JanusGraph instance such that it can be used with HadoopGraph to run OLAP queries.

下面的OLAP示例显示了JanusGraph直接支持的后端的配置示例。将需要特定于该存储后端的其他配置。该配置由gremlin.hadoop.graphReader属性指定，该属性指定要从存储后端读取数据的类。

JanusGraph直接支持以下graphReader类：
- CqlInputFormat与Cassandra 3一起使用
- 与Cassandra 2一起使用的CassandraInputFormat
- 与HBase一起使用的HBaseInputFormat和
以下.properties文件可用于连接JanusGraph实例，以便它可与HadoopGraph一起使用以运行OLAP查询。

read-cql.properties
``` shell
# Copyright 2019 JanusGraph Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Hadoop Graph Configuration
#
gremlin.graph=org.apache.tinkerpop.gremlin.hadoop.structure.HadoopGraph
gremlin.hadoop.graphReader=org.janusgraph.hadoop.formats.cql.CqlInputFormat
gremlin.hadoop.graphWriter=org.apache.hadoop.mapreduce.lib.output.NullOutputFormat
gremlin.hadoop.jarsInDistributedCache=true
gremlin.hadoop.inputLocation=none
gremlin.hadoop.outputLocation=output
gremlin.spark.persistContext=true
#
# JanusGraph Cassandra InputFormat configuration
#
# These properties defines the connection properties which were used while write data to JanusGraph.
janusgraphmr.ioformat.conf.storage.backend=cql
# This specifies the hostname & port for Cassandra data store.
janusgraphmr.ioformat.conf.storage.hostname=127.0.0.1
janusgraphmr.ioformat.conf.storage.port=9042
# This specifies the keyspace where data is stored.
janusgraphmr.ioformat.conf.storage.cql.keyspace=janusgraph
# This defines the indexing backend configuration used while writing data to JanusGraph.
janusgraphmr.ioformat.conf.index.search.backend=elasticsearch
janusgraphmr.ioformat.conf.index.search.hostname=127.0.0.1
# Use the appropriate properties for the backend when using a different storage backend (HBase) or indexing backend (Solr).
#
# Apache Cassandra InputFormat configuration
#
cassandra.input.partitioner.class=org.apache.cassandra.dht.Murmur3Partitioner
cassandra.input.widerows=true
#
# SparkGraphComputer Configuration
#
spark.master=local[*]
spark.executor.memory=1g
spark.serializer=org.apache.spark.serializer.KryoSerializer
spark.kryo.registrator=org.janusgraph.hadoop.serialize.JanusGraphKryoRegistrator
```
read-cassandra.propertiesread-hbase.properties

First create a properties file with above configurations, and load the same on the Gremlin Console to run OLAP queries as follows:

read-cql.properties
``` shell
bin/gremlin.sh
        \,,,/
        (o o)
-----oOOo-(3)-oOOo-----
plugin activated: janusgraph.imports
gremlin> :plugin use tinkerpop.hadoop
==>tinkerpop.hadoop activated
gremlin> :plugin use tinkerpop.spark
==>tinkerpop.spark activated
gremlin> // 1. Open a the graph for OLAP processing reading in from Cassandra 3
gremlin> graph = GraphFactory.open('conf/hadoop-graph/read-cql.properties')
==>hadoopgraph[cqlinputformat->gryooutputformat]
gremlin> // 2. Configure the traversal to run with Spark
gremlin> g = graph.traversal().withComputer(SparkGraphComputer)
==>graphtraversalsource[hadoopgraph[cqlinputformat->gryooutputformat], sparkgraphcomputer]
gremlin> // 3. Run some OLAP traversals
gremlin> g.V().count()
......
==>808
gremlin> g.E().count()
......
==> 8046
```
read-cassandra.propertiesread-hbase.properties

### OLAP Traversals with Spark Standalone Cluster 使用Spark独立集群进行OLAP遍历
The steps followed in the previous section can also be used with a Spark standalone cluster with only minor changes:

- Update the `spark.master` property to point to the Spark master URL instead of local
- Update the `spark.executor.extraClassPath` to enable the Spark executor to find the JanusGraph dependency jars
- Copy the JanusGraph dependency jars into the location specified in the previous step on each Spark executor machine

- Note
We have copied all the jars under **janusgraph-distribution/lib** into /opt/lib/janusgraph/ and the same directory structure is created across all workers, and jars are manually copied across all workers.

The final properties file used for OLAP traversal is as follows:

read-cql-standalone-cluster.properties

**

上一部分中遵循的步骤也可以与Spark独立集群一起使用，只需进行少量更改即可：

- 更新spark.master属性以指向Spark主URL，而不是本地
- 更新spark.executor.extraClassPath以使Spark执行程序能够找到JanusGraph依赖罐
- 将JanusGraph依赖项jar复制到每个Spark执行程序机器上一步中指定的位置

注意：
我们已将janusgraph-distribution / lib下的所有jar都复制到/ opt / lib / janusgraph /中，并且在所有工作进程中创建了相同的目录结构，并且在所有worker中手动复制了jar。

用于OLAP遍历的最终属性文件如下：

read-cql-standalone-cluster.properties
```shell
# Copyright 2020 JanusGraph Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Hadoop Graph Configuration
#
gremlin.graph=org.apache.tinkerpop.gremlin.hadoop.structure.HadoopGraph
gremlin.hadoop.graphReader=org.janusgraph.hadoop.formats.cql.CqlInputFormat
gremlin.hadoop.graphWriter=org.apache.hadoop.mapreduce.lib.output.NullOutputFormat
gremlin.hadoop.jarsInDistributedCache=true
gremlin.hadoop.inputLocation=none
gremlin.hadoop.outputLocation=output
gremlin.spark.persistContext=true
#
# JanusGraph Cassandra InputFormat configuration
#
# These properties defines the connection properties which were used while write data to JanusGraph.
janusgraphmr.ioformat.conf.storage.backend=cql
# This specifies the hostname & port for Cassandra data store.
janusgraphmr.ioformat.conf.storage.hostname=127.0.0.1
janusgraphmr.ioformat.conf.storage.port=9042
# This specifies the keyspace where data is stored.
janusgraphmr.ioformat.conf.storage.cql.keyspace=janusgraph
# This defines the indexing backend configuration used while writing data to JanusGraph.
janusgraphmr.ioformat.conf.index.search.backend=elasticsearch
janusgraphmr.ioformat.conf.index.search.hostname=127.0.0.1
# Use the appropriate properties for the backend when using a different storage backend (HBase) or indexing backend (Solr).
#
# Apache Cassandra InputFormat configuration
#
cassandra.input.partitioner.class=org.apache.cassandra.dht.Murmur3Partitioner
cassandra.input.widerows=true
#
# SparkGraphComputer Configuration
#
spark.master=spark://127.0.0.1:7077
spark.executor.memory=1g
spark.executor.extraClassPath=/opt/lib/janusgraph/*
spark.serializer=org.apache.spark.serializer.KryoSerializer
spark.kryo.registrator=org.janusgraph.hadoop.serialize.JanusGraphKryoRegistrator
```
read-cassandra-standalone-cluster.propertiesread-hbase-standalone-cluster.properties

Then use the properties file as follows from the Gremlin Console:

read-cql-standalone-cluster.properties
```
bin/gremlin.sh
        \,,,/
        (o o)
-----oOOo-(3)-oOOo-----
plugin activated: janusgraph.imports
gremlin> :plugin use tinkerpop.hadoop
==>tinkerpop.hadoop activated
gremlin> :plugin use tinkerpop.spark
==>tinkerpop.spark activated
gremlin> // 1. Open a the graph for OLAP processing reading in from Cassandra 3
gremlin> graph = GraphFactory.open('conf/hadoop-graph/read-cql-standalone-cluster.properties')
==>hadoopgraph[cqlinputformat->gryooutputformat]
gremlin> // 2. Configure the traversal to run with Spark
gremlin> g = graph.traversal().withComputer(SparkGraphComputer)
==>graphtraversalsource[hadoopgraph[cqlinputformat->gryooutputformat], sparkgraphcomputer]
gremlin> // 3. Run some OLAP traversals
gremlin> g.V().count()
......
==>808
gremlin> g.E().count()
......
==> 8046
```
read-cassandra-standalone-cluster.propertiesread-hbase-standalone-cluster.properties

## Other Vertex Programs
Apache TinkerPop provides various vertex programs. A vertex program runs on each vertex until either a termination criteria is attained or a fixed number of iterations has been reached. Due to the parallel nature of vertex programs, they can leverage parallel computing framework like Spark to improve their performance.

Once you are familiar with how to configure JanusGraph to work with Spark, you can run all the other vertex programs provided by Apache TinkerPop, like Page Rank, Bulk Loading and Peer Pressure. See the [TinkerPop VertexProgram docs](https://tinkerpop.apache.org/docs/3.4.6/reference/#vertexprogram) for more details.

Apache TinkerPop提供了各种顶点程序。 在每个顶点上运行一个顶点程序，直到达到终止条件或达到固定的迭代次数为止。 由于顶点程序具有并行性，因此它们可以利用Spark等并行计算框架来提高性能。

熟悉如何配置JanusGraph与Spark一起使用后，就可以运行Apache TinkerPop提供的所有其他顶点程序，例如Page Rank，Bulk Loading和Peer Pressure。 有关更多详细信息，请参见TinkerPop VertexProgram文档。
