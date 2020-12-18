![JanusGraph 中文社区](https://github.com/simon824/janusgraph.cn/blob/main/images/68747470733a2f2f6a616e757367726170682e6f72672f696d672f6a616e757367726170682e706e67.png?raw=true)

- 社区讨论：https://github.com/simon824/janusgraph.cn/discussions
- 博客：
- 微信群：加微信 shirning，备注 janus

有收获的话 star🌟 一下吧！

[![janusgraph.cn](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/simon824/janusgraph.cn)
[![GitHub stars](https://img.shields.io/github/stars/simon824/janusgraph.cn.svg?label=Stars)](https://github.com/simon824/janusgraph.cn/stargazers)
[![GitHub watchers](https://img.shields.io/github/watchers/simon824/janusgraph.cn.svg?label=Watchers)](https://github.com/simon824/janusgraph.cn/watchers)
[![GitHub forks](https://img.shields.io/github/forks/simon824/janusgraph.cn.svg?label=Forks)](https://github.com/simon824/janusgraph.cn/fork)
[![GitHub followers](https://img.shields.io/github/followers/simon824.svg?label=Followers)](https://github.com/sorenduan)

# 分布式图数据库 JanusGraph 中文社区
涉及技术包括:  [JanusGraph](https://docs.janusgraph.org/) | [TinkerPop (Gremlin)](https://tinkerpop.apache.org/docs/current/) | [HBase](https://hbase.apache.org/book.html)  | [ElasticSearch](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)  | [Spark GraphX](http://spark.apache.org/docs/latest/graphx-programming-guide.html)

<details>

<summary>目录</summary>

- [JanusGraph 官方文档翻译](https://github.com/simon824/janusgraph.cn#janusgraph-%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91)
- [JanusGraph 实践]()
- [Gremlin 官方文档翻译](https://github.com/simon824/janusgraph.cn#gremlin-%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91)
</details>


# JanusGraph 官方文档翻译

- [Introduction](https://docs.janusgraph.org/) 介绍
- Getting Started 开始 (未完成)
   - [Installation](https://docs.janusgraph.org/getting-started/installation/) 安装
   - [Basic Usage](https://docs.janusgraph.org/getting-started/basic-usage/)  基本使用
   - [Architectural Overview](https://docs.janusgraph.org/getting-started/architecture/) 架构总览
- JanusGraph Basics 基础 (未完成)
   - [Configuration](https://docs.janusgraph.org/basics/configuration/) 配置
   - [Schema and Data Modeling](https://docs.janusgraph.org/basics/schema/) schema和数据建模
   - [Gremlin Query Language](https://docs.janusgraph.org/basics/gremlin/) gremlin查询语言
   - [JanusGraph Server](https://docs.janusgraph.org/basics/server/) janusgraph服务
   - [Deployment Scenarios](https://docs.janusgraph.org/basics/deployment/) 部署方案
   - [ConfiguredGraphFactory](https://docs.janusgraph.org/basics/configured-graph-factory/)
   - [Things to Consider in a Multi-Node JanusGraph Cluster](https://docs.janusgraph.org/basics/multi-node/) 多节点JanusGraph群集中要考虑的事项
   - [Transactions](https://docs.janusgraph.org/basics/transactions/) 事务
   - [JanusGraph Cache](https://docs.janusgraph.org/basics/cache/) 缓存
   - [Transaction Log](https://docs.janusgraph.org/basics/transaction-log/) 事务日志
   - [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) 配置参考
   - [Config Example](https://docs.janusgraph.org/basics/example-config/) 配置示例
   - [Common Questions](https://docs.janusgraph.org/basics/common-questions/) 常见问题
   - [Technical Limitations](https://docs.janusgraph.org/basics/technical-limitations/) 技术局限性
- Index Management 索引管理 (未完成)
   - [Indexing for Better Performance](https://docs.janusgraph.org/index-management/index-performance/) 索引以获得更好的性能
   - [Index Lifecycle](https://docs.janusgraph.org/index-management/index-lifecycle/) 索引的生命周期
   - [Reindexing](https://docs.janusgraph.org/index-management/index-reindexing/) 重建索引
   - [Removal](https://docs.janusgraph.org/index-management/index-removal/) 清除
- Storage Backends 存储后端 (未完成)
   - [Introduction](https://docs.janusgraph.org/storage-backend/)
   - [Apache Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
   - [Apache HBase](https://docs.janusgraph.org/storage-backend/hbase/)
   - [Google Cloud Bigtable](https://docs.janusgraph.org/storage-backend/bigtable/)
   - [Oracle Berkeley DB Java Edition](https://docs.janusgraph.org/storage-backend/bdb/)
   - [InMemory Storage Backend](https://docs.janusgraph.org/storage-backend/inmemorybackend/)
- Mixed Index Backends 混合索引后端 (未完成)
   - [Introduction](https://docs.janusgraph.org/index-backend/) 介绍
   - [Search Predicates and Data Types](https://docs.janusgraph.org/index-backend/search-predicates/) 搜索谓词和数据类型
   - [Index Parameters and Full-Text Search](https://docs.janusgraph.org/index-backend/text-search/) 索引参数和全文搜索
   - [Field Mapping](https://docs.janusgraph.org/index-backend/field-mapping/) 字段映射
   - [Direct Index Query](https://docs.janusgraph.org/index-backend/direct-index-query/) 直接索引查询
   - [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
   - [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
   - [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)
- Advanced Topics 进阶主题 (完成)
   - [高级schema (Advanced Schema)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E9%AB%98%E7%BA%A7schema(Advanced%20Schema).md)
   - [最终一致的存储后端 (Eventually-Consistent Storage Backends)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E6%9C%80%E7%BB%88%E4%B8%80%E8%87%B4%E7%9A%84%E5%AD%98%E5%82%A8%E5%90%8E%E7%AB%AF(Eventually-Consistent%20Storage%20Backends).md)
   - [异常与恢复 (Failure & Recovery)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E5%BC%82%E5%B8%B8%E4%B8%8E%E6%81%A2%E5%A4%8D(Failure%20%26%20Recovery).md)
   - [批写入 (Bulk Loading)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E6%89%B9%E5%86%99%E5%85%A5(Bulk%20Loading).md)
   - [图分区 (Graph Partitioning)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E5%9B%BE%E5%88%86%E5%8C%BA%20(Graph%20Partitioning).md)
   - [数据类型和属性序列化器配置 (Datatype and Attribute Serializer Configuration)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E5%92%8C%E5%B1%9E%E6%80%A7%E5%BA%8F%E5%88%97%E5%8C%96%E5%99%A8%E9%85%8D%E7%BD%AE.md) 
   - [Hadoop-Gremlin 应用 (JanusGraph with TinkerPop’s Hadoop-Gremlin)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/Hadoop-Gremlin%E5%BA%94%E7%94%A8(JanusGraph%20with%20TinkerPop%E2%80%99s%20Hadoop-Gremlin).md)
   - [监控 (Monitoring JanusGraph)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E7%9B%91%E6%8E%A7(Monitoring%20JanusGraph).md)
   - [从Titan迁移数据 (Migrating from Titan)](https://docs.janusgraph.org/advanced-topics/migrating/)
   - [数据模型 (JanusGraph Data Model)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B(JanusGraph%20Data%20Model).md)
   - [总线 (JanusGraph Bus)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E8%BF%9B%E9%98%B6%E4%B8%BB%E9%A2%98(Advanced%20Topics)/%E6%80%BB%E7%BA%BF(JanusGraph%20Bus).md)
- Connecting to JanusGraph
   - [Introduction](https://docs.janusgraph.org/connecting/)
   - [Using Java](https://docs.janusgraph.org/connecting/java/)
   - [Using Python](https://docs.janusgraph.org/connecting/python/)
   - [Using .Net](https://docs.janusgraph.org/connecting/dotnet/)
- [Development](https://docs.janusgraph.org/development/)
- [Appendices](https://docs.janusgraph.org/appendices/) 发展历程
- [Changelog](https://docs.janusgraph.org/changelog/) 更新日志


<br />
<br />

# JanusGraph 实践文章

# Gremlin 官方文档翻译

