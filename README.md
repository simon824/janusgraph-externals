# JanusGraph Externals 

[JanusGraph: an open-source, distributed graph database](https://github.com/JanusGraph/janusgraph)   
*Rel: [JanusGraph](https://docs.janusgraph.org/) | [TinkerPop (Gremlin)](https://tinkerpop.apache.org/docs/current/) | [HBase](https://hbase.apache.org/book.html)  | [ElasticSearch](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)  | [Spark GraphX](http://spark.apache.org/docs/latest/graphx-programming-guide.html)*

[![janusgraph.cn](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/simon824/janusgraph.cn)
[![GitHub stars](https://img.shields.io/github/stars/simon824/janusgraph.cn.svg?label=Stars)](https://github.com/simon824/janusgraph.cn/stargazers)
[![GitHub watchers](https://img.shields.io/github/watchers/simon824/janusgraph.cn.svg?label=Watchers)](https://github.com/simon824/janusgraph.cn/watchers)
[![GitHub forks](https://img.shields.io/github/forks/simon824/janusgraph.cn.svg?label=Forks)](https://github.com/simon824/janusgraph.cn/fork)
[![GitHub followers](https://img.shields.io/github/followers/simon824.svg?label=Followers)](https://github.com/simon824)

> [Discussions (Q&A)](https://github.com/simon824/janusgraph.cn/discussions)

**Contributors**
[ (How to become janusgraph-externals contributor ?)](https://github.com/simon824/janusgraph.cn/blob/main/contributor.md)

Thanks to all developers!

[![](https://opencollective.com/janusgraph-externals/contributors.svg?width=666)](https://github.com/simon824/janusgraph-externals/graphs/contributors)



## What is janusgraph-externals?

`janusgraph-externals` is an unofficial collection of external toolkits built around `janusgraph`. The purpose is to make `janusgraph` more convenient and efficient to use, including the following modules.  

- **janusgraph-graphx**  
`janusgraph-graphx` built on top of `Apache Spark GraphX` and `JanusGraph`. The purpose is to integrate these two components. Spark generates RDD by parsing the underlying HFile of JanusGraph and deserializing the JanusGraph vertex-edge structure. Welcome developers to test and upgrade iteratively together. (This is a basic version adapt to `janusgraph-0.5.2`, other versions need to be tested)
   
- **janusgraph-seatunnel**  
`janusgraph-seatunnel` built on top of `SeaTunnel`. The purpose is to improve the efficiency of batch import and export of JanusGraph data.(Coming soon)
    
- **janusgraph-chinese-doc**  
[JanusGraph Chinese Document](https://github.com/simon824/janusgraph.cn#janusgraph-doc)

> Stay tuned for more content! (For example: Spark writes into JanusGraph by generating HFile, visual query platform, etc. Developers are also welcome to contribute!)

## JanusGraph Q&A
[ Ask Question](https://github.com/simon824/janusgraph.cn/discussions)

- [The vertex-center-index will be created automatically, so there is no need for user customization?](https://github.com/simon824/janusgraph.cn/discussions/3)
- [If I add attributes to the edge, will janusgraph automatically create a local index? Do I not need to define a schema?](https://github.com/simon824/janusgraph.cn/discussions/3)
- [Where does the vertex-center-index exist? What is the principle？](https://github.com/simon824/janusgraph.cn/discussions/3)
- [JanusGraph's profile() cannot display the vertex-centic index. Is it applied?](https://github.com/simon824/janusgraph.cn/discussions/2)
- [JanusGraph causes memory overflow problem.](https://github.com/simon824/janusgraph.cn/discussions/5) 

## Others
Other awesome project about JanusGraph* 
- [janusgraph-visualization](https://github.com/fenglex/janusgraph-visualization)
- [janusgraph-source-article](https://github.com/yoylee/janusgraph-source-article)
- [janusgraph-util](https://github.com/dengziming/janusgraph-util)
- [janusgraph-prometheus](https://github.com/gguttikonda/janusgraph-prometheus)
- [janusgraph-docker](https://github.com/JanusGraph/janusgraph-docker)
