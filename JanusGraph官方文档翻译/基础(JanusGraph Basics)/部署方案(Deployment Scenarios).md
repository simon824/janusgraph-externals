# 部署方案 Deployment Scenarios
# Deployment Scenarios部署方案
JanusGraph offers a wide choice of storage and index backends which results in great flexibility of how it can be deployed. This chapter presents a few possible deployment scenarios to help with the complexity that comes with this flexibility.

Before discussing the different deployment scenarios, it is important to understand the roles of JanusGraph itself and that of the backends. First of all, applications only communicate directly with JanusGraph, mostly by sending Gremlin traversals for execution. JanusGraph then communicates with the configured backends to execute the received traversal. When JanusGraph is used in the form of JanusGraph Server, then there is nothing like a _master_ JanusGraph Server. Applications can therefore connect to any JanusGraph Server instance. They can also use a load-balancer to schedule requests to the different instances. The JanusGraph Server instances themselves don’t communicate to each other directly which makes it easy to scale them when the need arises to process more traversals.

> Note
The scenarios presented in this chapter are only examples of how JanusGraph can be deployed. Each deployment needs to take into account the concrete use cases and production needs.

JanusGraph提供了广泛的存储和索引后端选择，这为如何部署它提供了极大的灵活性。本章介绍了一些可能的部署方案，以帮助解决这种灵活性带来的复杂性。

在讨论不同的部署方案之前，重要的是要了解JanusGraph本身以及后端的角色。首先，应用程序仅直接与JanusGraph通信，主要是通过发送Gremlin遍历来执行。然后，JanusGraph与配置的后端进行通信以执行接收到的遍历。当以JanusGraph Server的形式使用JanusGraph时，没有什么比主JanusGraph Server更好了。因此，应用程序可以连接到任何JanusGraph Server实例。他们还可以使用负载均衡器来调度对不同实例的请求。 JanusGraph Server实例本身不会直接相互通信，这使得在需要处理更多遍历时轻松缩放它们。

>注意
本章介绍的方案只是JanusGraph可以如何部署的示例。每个部署都需要考虑具体的用例和生产需求。

## 入门方案 Getting Started Scenario 
This scenario is the scenario most users probably want to choose when they are just getting started with JanusGraph. It offers scalability and fault tolerance with a minimum number of servers required. JanusGraph Server runs together with an instance of the storage backend and optionally also an instance of the index backend on every server.

这种情况是大多数用户刚开始使用JanusGraph时可能想要选择的情况。它以最少的服务器数量提供可伸缩性和容错能力。 JanusGraph Server与存储后端实例一起运行，还可以选择在每台服务器上同时与索引后端实例一起运行。

![](https://cdn.nlark.com/yuque/0/2020/svg/1209774/1606533103787-7e008a56-a147-49d6-8657-5d44830d816e.svg#align=left&display=inline&height=700&margin=%5Bobject%20Object%5D&originHeight=700&originWidth=1011&size=0&status=done&style=none&width=1011)

A setup like this can be extended by simply adding more servers of the same kind or by moving one of the components onto dedicated servers. The latter describes a growth path to transform the deployment into the [Advanced Scenario](https://docs.janusgraph.org/basics/deployment/#advanced-scenario).

Any of the scalable storage backends can be used with this scenario. Note however that for Scylla [some configuration is required when it is hosted co-located with other services](http://docs.scylladb.com/getting-started/scylla_in_a_shared_environment/) like in this scenario. When an index backend should be used in this scenario then it also needs to be one that is scalable.

可以通过简单地添加更多相同类型的服务器或将组件之一移动到专用服务器上来扩展这种设置。后者描述了将部署转换为高级方案的增长途径。

任何可扩展的存储后端都可用于此方案。但是请注意，对于Scylla，将其与其他服务托管在同一位置时，需要一些配置，例如在这种情况下。当在这种情况下应该使用索引后端时，它也必须是可伸缩的。

## 高级方案 Advanced Scenario 
The advanced scenario is an evolution of the [Getting Started Scenario](https://docs.janusgraph.org/basics/deployment/#getting-started-scenario). Instead of hosting the JanusGraph Server instances together with the storage backend and optionally also the index backend, they are now separated on different servers. The advantage of hosting the different components (JanusGraph Server, storage/index backend) on different servers is that they can be scaled and managed independently of each other. This offers a higher flexibility at the cost of having to maintain more servers.

![](https://cdn.nlark.com/yuque/0/2020/svg/1209774/1606533104501-7485f9c1-28fd-4b15-928c-ce8dd7ea68d4.svg#align=left&display=inline&height=658&margin=%5Bobject%20Object%5D&originHeight=658&originWidth=1248&size=0&status=done&style=none&width=1248)

Since this scenario offers independent scalability of the different components, it of course makes most sense to also use scalable backends.

高级方案是“入门方案”的演变。现在，它们已经在不同的服务器上分离，而不是与存储后端以及索引后端一起托管JanusGraph Server实例。在不同的服务器上托管不同组件（JanusGraph Server，存储/索引后端）的优点是可以相互独立地缩放和管理它们。这以需要维护更多服务器为代价提供了更高的灵活性。

由于此方案提供了不同组件的独立可伸缩性，因此当然也可以使用可伸缩后端。

## 极简方案 Minimalist Scenario 
It is also possible to host JanusGraph Server together with the backend(s) on just one server. This is especially attractive for testing purposes or for example when JanusGraph just supports a single application which can then also run on the same server.

![](https://cdn.nlark.com/yuque/0/2020/svg/1209774/1606533103388-1dd38479-b20d-4401-91ae-d7a0f9830900.svg#align=left&display=inline&height=204&margin=%5Bobject%20Object%5D&originHeight=204&originWidth=1011&size=0&status=done&style=none&width=1011)

Opposed to the previous scenarios, it makes most sense to use backends for this scenario that are not scalable. The in-memory backend can be used for testing purposes or Berkeley DB for production and Lucene as the optional index backend.

也可以将JanusGraph Server和后端一起托管在一台服务器上。这对于测试目的或例如JanusGraph仅支持单个应用程序也可以在同一服务器上运行时特别有吸引力。

与以前的方案相反，在此方案中使用不可扩展的后端是最有意义的。内存后端可用于测试目的，伯克利数据库可用于生产，而Lucene可作为可选索引后端。

## 嵌入式JanusGraph Embedded JanusGraph 
Instead of connecting to the JanusGraph Server from an application it is also possible to embed JanusGraph as a library inside a JVM based application. While this reduces the administrative overhead, it makes it impossible to scale JanusGraph independently of the application. Embedded JanusGraph can be deployed as a variation of any of the other scenarios. JanusGraph just moves from the server(s) directly into the application as its now just used as a library instead of an independent service.

除了可以从应用程序连接到JanusGraph Server之外，还可以将JanusGraph作为库嵌入基于JVM的应用程序中。尽管这减少了管理开销，但它使得无法独立于应用程序扩展JanusGraph。可以将嵌入式JanusGraph部署为其他任何方案的变体。 JanusGraph只是从服务器直接移到应用程序中，因为它现在仅用作库而不是独立的服务。
