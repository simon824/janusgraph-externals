![JanusGraph 中文社区](https://github.com/simon824/janusgraph.cn/blob/main/images/68747470733a2f2f6a616e757367726170682e6f72672f696d672f6a616e757367726170682e706e67.png?raw=true)
[JanusGraph: an open-source, distributed graph database](https://github.com/JanusGraph/janusgraph)

- 社区讨论：[Discussions](https://github.com/simon824/janusgraph.cn/discussions)
- 博客：[CSDN](https://blog.csdn.net/weixin_46892441)
- GitHub：[有收获的话 star🌟 一下吧！](https://github.com/simon824/janusgraph.cn)
- **微信群：加微信 shirning，备注 janus**

[![janusgraph.cn](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/simon824/janusgraph.cn)
[![GitHub stars](https://img.shields.io/github/stars/simon824/janusgraph.cn.svg?label=Stars)](https://github.com/simon824/janusgraph.cn/stargazers)
[![GitHub watchers](https://img.shields.io/github/watchers/simon824/janusgraph.cn.svg?label=Watchers)](https://github.com/simon824/janusgraph.cn/watchers)
[![GitHub forks](https://img.shields.io/github/forks/simon824/janusgraph.cn.svg?label=Forks)](https://github.com/simon824/janusgraph.cn/fork)
[![GitHub followers](https://img.shields.io/github/followers/simon824.svg?label=Followers)](https://github.com/simon824)

**特别鸣谢以下 Contributors**
[ (如何成为社区 Contributor ?)](https://github.com/simon824/janusgraph.cn/blob/main/contributor.md)

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/akiii-G">
      </a>
      <a href="https://github.com/akiii-G">akiii-G</a>
     </td>       
    <td align="center">
      <a href="https://github.com/ray1991a">
      </a>
      <a href="https://github.com/ray1991a">ray1991a</a>
    </td>
    <td align="center">
      <a href="https://github.com/beiluve">
      </a>
      <a href="https://github.com/beiluve">beiluve</a>
    </td>
    <td align="center">
      <a href="https://github.com/mengbd">
      </a>
      <a href="https://github.com/mengbd">mengbd</a>
    </td>
    <td align="center">
      <a href="https://github.com/simon824">
      </a>
      <a href="https://github.com/simon824">simon824</a>
    </td>

  </tr>
</table>

# 分布式图数据库 JanusGraph 中文社区
涉及技术包括:  [JanusGraph](https://docs.janusgraph.org/) | [TinkerPop (Gremlin)](https://tinkerpop.apache.org/docs/current/) | [HBase](https://hbase.apache.org/book.html)  | [ElasticSearch](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)  | [Spark GraphX](http://spark.apache.org/docs/latest/graphx-programming-guide.html)

<details>

<summary>目录</summary>

- [JanusGraph Q&A](https://github.com/simon824/janusgraph.cn#janusgraph-qa)
- [JanusGraph 官方文档翻译](https://github.com/simon824/janusgraph.cn#janusgraph-%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91)
- [JanusGraph 实践]()
- [Gremlin 相关](https://github.com/simon824/janusgraph.cn#gremlin-%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91)
</details>


# JanusGraph Q&A
> [ 我想提问 🙋‍♂️🙋](https://github.com/simon824/janusgraph.cn/discussions)

- [会自动创建点中心索引，那是不是就不用用户自定义了？](https://github.com/simon824/janusgraph.cn/discussions/3)
- [如果我在边添加属性，janusgraph 是否会自动创建局部索引？ 我不需要定义 schema 吗？](https://github.com/simon824/janusgraph.cn/discussions/3)
- [点中心索引存在哪里？原理是怎么样的？](https://github.com/simon824/janusgraph.cn/discussions/3)
- [JanusGraph 的 profile() 无法显示点中心索引 (vertex-centic index) 是否有被应用?](https://github.com/simon824/janusgraph.cn/discussions/2)
- [JanusGraph 导致内存溢出的问题](https://github.com/simon824/janusgraph.cn/discussions/5)


# JanusGraph 官方文档翻译
>带中文后缀的为还未翻译的文章，链接指向官网英文原文，有兴趣欢迎贡献！
- [Introduction](https://docs.janusgraph.org/) 介绍
- 开始 (Getting Started)
   - [Installation](https://docs.janusgraph.org/getting-started/installation/) 安装
   - [Basic Usage](https://docs.janusgraph.org/getting-started/basic-usage/)  基本使用
   - [Architectural Overview](https://docs.janusgraph.org/getting-started/architecture/) 架构总览
- 基础 (JanusGraph Basics)
   - [配置(Configuration)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E9%85%8D%E7%BD%AE(Configuration).md) 
   - [模式与数据建模(Schema and Data Modeling)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E6%A8%A1%E5%BC%8F%E4%B8%8E%E6%95%B0%E6%8D%AE%E5%BB%BA%E6%A8%A1(Schema%20and%20Data%20Modeling).md) 
   - [gremlin查询语言(Gremlin Query Language)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/Gremlin%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80(Gremlin%20Query%20Language).md) 
   - [JanusGraph Server](https://docs.janusgraph.org/basics/server/) janusgraph服务
   - [部署方案(Deployment Scenarios)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E9%83%A8%E7%BD%B2%E6%96%B9%E6%A1%88(Deployment%20Scenarios).md) 
   - [ConfiguredGraphFactory](https://docs.janusgraph.org/basics/configured-graph-factory/) 配置工厂
   - [Things to Consider in a Multi-Node JanusGraph Cluster](https://docs.janusgraph.org/basics/multi-node/) 多节点JanusGraph群集中要考虑的事项
   - [事务(Transactions)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E4%BA%8B%E5%8A%A1(Transactions).md)
   - [缓存(JanusGraph cache)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E7%BC%93%E5%AD%98(JanusGraph%20cache).md) 
   - [Transaction Log](https://docs.janusgraph.org/basics/transaction-log/) 事务日志
   - [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) 配置参考
   - [Config Example](https://docs.janusgraph.org/basics/example-config/) 配置示例
   - [常见问题(Common Questions)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98(Common%20Questions).md)
   - [技术局限(Technical Limitations)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E5%9F%BA%E7%A1%80(JanusGraph%20Basics)/%E6%8A%80%E6%9C%AF%E5%B1%80%E9%99%90(Technical%20Limitations).md) 
- 索引管理 (Index Management)
   - [索引提升性能(Indexing for Better Performance)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E7%B4%A2%E5%BC%95%E7%AE%A1%E7%90%86(Index%20Management)/%E7%B4%A2%E5%BC%95%E6%8F%90%E5%8D%87%E6%80%A7%E8%83%BD(Indexing%20for%20Better%20Performance).md) 
   - [索引的生命周期(Index Lifecycle)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E7%B4%A2%E5%BC%95%E7%AE%A1%E7%90%86(Index%20Management)/%E7%B4%A2%E5%BC%95%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F(Index%20Lifecycle).md) 
   - [重索引(Reindexing)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E7%B4%A2%E5%BC%95%E7%AE%A1%E7%90%86(Index%20Management)/%E9%87%8D%E7%B4%A2%E5%BC%95(Reindexing).md) 
   - [删索引(Removal)](https://github.com/simon824/janusgraph.cn/blob/main/JanusGraph%E5%AE%98%E6%96%B9%E6%96%87%E6%A1%A3%E7%BF%BB%E8%AF%91/%E7%B4%A2%E5%BC%95%E7%AE%A1%E7%90%86(Index%20Management)/%E5%88%A0%E7%B4%A2%E5%BC%95(Removal).md) 
- 存储后端 (Storage Backends)
   - [Introduction](https://docs.janusgraph.org/storage-backend/)
   - [Apache Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
   - [Apache HBase](https://docs.janusgraph.org/storage-backend/hbase/)
   - [Google Cloud Bigtable](https://docs.janusgraph.org/storage-backend/bigtable/)
   - [Oracle Berkeley DB Java Edition](https://docs.janusgraph.org/storage-backend/bdb/)
   - [InMemory Storage Backend](https://docs.janusgraph.org/storage-backend/inmemorybackend/)
- 混合索引后端 (Mixed Index Backends)
   - [Introduction](https://docs.janusgraph.org/index-backend/) 介绍
   - [Search Predicates and Data Types](https://docs.janusgraph.org/index-backend/search-predicates/) 搜索谓词和数据类型
   - [Index Parameters and Full-Text Search](https://docs.janusgraph.org/index-backend/text-search/) 索引参数和全文搜索
   - [Field Mapping](https://docs.janusgraph.org/index-backend/field-mapping/) 字段映射
   - [Direct Index Query](https://docs.janusgraph.org/index-backend/direct-index-query/) 直接索引查询
   - [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
   - [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
   - [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)
- 进阶主题 (Advanced Topics)
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
- 连接JanusGraph (Connecting to JanusGraph)
   - [Introduction](https://docs.janusgraph.org/connecting/)
   - [Using Java](https://docs.janusgraph.org/connecting/java/)
   - [Using Python](https://docs.janusgraph.org/connecting/python/)
   - [Using .Net](https://docs.janusgraph.org/connecting/dotnet/)
- [Development](https://docs.janusgraph.org/development/) 发展历程
- [Appendices](https://docs.janusgraph.org/appendices/) 附录
- [Changelog](https://docs.janusgraph.org/changelog/) 更新日志



# JanusGraph 实践
- [JanusGraph 可视化项目](https://github.com/fenglex/janusgraph-visualization)
- [JanusGraph 源码解析文章](https://github.com/yoylee/janusgraph-source-article)
- [JanusGraph 批量导数据](https://github.com/dengziming/janusgraph-util)
- [JanusGraph 集成 Prometheus 指标监控](https://github.com/gguttikonda/janusgraph-prometheus)
- [JanusGraph docker 镜像](https://github.com/JanusGraph/janusgraph-docker)


# Gremlin 相关

