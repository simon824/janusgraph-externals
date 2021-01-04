
Almost all interaction with JanusGraph is associated with a transaction. JanusGraph transactions are safe for concurrent use by multiple threads. Methods on a JanusGraph instance like `graph.V(...)` and `graph.tx().commit()` perform a `ThreadLocal` lookup to retrieve or create a transaction associated with the calling thread. Callers can alternatively forego `ThreadLocal` transaction management in favor of calling `graph.tx().createThreadedTx()`, which returns a reference to a transaction object with methods to read/write graph data and commit or rollback.

JanusGraph transactions are not necessarily ACID. They can be so configured on BerkeleyDB, but they are not generally so on Cassandra or HBase, where the underlying storage system does not provide serializable isolation or multi-row atomic writes and the cost of simulating those properties would be substantial.

This section describes JanusGraph’s transactional semantics and API.

与 JanusGraph 的几乎所有交互都与事务关联。 JanusGraph 事务对于多个线程并发使用是安全的。 JanusGraph 实例上的方法（ 如 graph.V(...) 和 graph.tx().commit() ）执行ThreadLocal查找，以检索或创建与调用线程关联的事务。调用者可以选择放弃 ThreadLocal 事务管理，而倾向于调用 graph.tx().createThreadedTx()，该方法返回对事务对象的引用，该引用具有读取/写入图数据以及提交或回滚的方法。

JanusGraph 事务不一定是 ACID。可以在 BerkeleyDB 上对它们进行配置，但是在Cassandra或HBase上却不能，因为底层存储系统不提供可序列化的隔离或多行原子写操作，因此模拟这些属性的成本将很高。

本节介绍 JanusGraph 的事务语义和API。

## 事务处理 Transaction Handling
Every graph operation in JanusGraph occurs within the context of a transaction. According to the TinkerPop’s transactional specification, each thread opens its own transaction against the graph database with the first operation (i.e. retrieval or mutation) on the graph:

JanusGraph中的每个图形操作都在事务上下文中发生。根据TinkerPop的事务规范，每个线程都会通过图上的第一个操作（即检索或更改）针对图形数据库打开自己的事务：
```java
graph = JanusGraphFactory.open("berkeleyje:/tmp/janusgraph")
juno = graph.addVertex() //Automatically opens a new transaction
juno.property("name", "juno")
graph.tx().commit() //Commits transaction
```
In this example, a local JanusGraph graph database is opened. Adding the vertex "juno" is the first operation (in this thread) which automatically opens a new transaction. All subsequent operations occur in the context of that same transaction until the transaction is explicitly stopped or the graph database is closed. If transactions are still open when `close()` is called, then the behavior of the outstanding transactions is technically undefined. In practice, any non-thread-bound transactions will usually be effectively rolled back, but the thread-bound transaction belonging to the thread that invoked shutdown will first be committed. Note, that both read and write operations occur within the context of a transaction.

在此示例中，将打开本地JanusGraph图数据库。添加顶点“ juno”是该线程中的第一个操作，它将自动打开一个新事务。所有后续操作都在同一事务的上下文中发生，直到显式停止该事务或关闭图形数据库为止。如果在调用close()时仍打开事务，则技术上未定义未完成事务的行为。在实践中，通常会有效地回滚任何非线程绑定的事务，但是将首先提交属于调用shutdown的线程的线程绑定的事务。注意，读和写操作都发生在事务的上下文中。

## 事务范围 Transactional Scope 
All graph elements (vertices, edges, and types) are associated with the transactional scope in which they were retrieved or created. Under TinkerPop’s default transactional semantics, transactions are automatically created with the first operation on the graph and closed explicitly using `commit()` or `rollback()`. Once the transaction is closed, all graph elements associated with that transaction become stale and unavailable. However, JanusGraph will automatically transition vertices and types into the new transactional scope as shown in this example:

所有图元素（顶点，边和类型）都与在其中检索或创建它们的事务范围相关联。在TinkerPop的默认事务语义下，事务会在图上执行第一个操作自动创建，并使用commit()或rollback()显式关闭。事务关闭后，与该事务关联的所有图形元素都将变为陈旧且不可用。但是，JanusGraph会自动将顶点和类型转换到新的事务范围中，如以下示例所示：
```java
graph = JanusGraphFactory.open("berkeleyje:/tmp/janusgraph")
juno = graph.addVertex() //Automatically opens a new transaction
graph.tx().commit() //Ends transaction
juno.property("name", "juno") //Vertex is automatically transitioned
```
Edges, on the other hand, are not automatically transitioned and cannot be accessed outside their original transaction. They must be explicitly transitioned:

另一方面，边不会自动转换，也无法在原始事务之外访问。必须明确地转换它们：
```java
e = juno.addEdge("knows", graph.addVertex())
graph.tx().commit() //Ends transaction
e = g.E(e).next() //Need to refresh edge
e.property("time", 99)
```


## 事务失败 Transaction Failures
When committing a transaction, JanusGraph will attempt to persist all changes to the storage backend. This might not always be successful due to IO exceptions, network errors, machine crashes or resource unavailability. Hence, transactions can fail. In fact, transactions **will eventually fail** in sufficiently large systems. Therefore, we highly recommend that your code expects and accommodates such failures:

提交事务时，JanusGraph将尝试将所有更改持久化到存储后端。由于IO异常，网络错误，机器崩溃或资源不可用，这可能并不总是成功。因此，事务可能会失败。实际上，在足够大的系统中，事务最终将失败。因此，我们强烈建议您的代码预期并解决以下故障：
```java
try {
    if (g.V().has("name", name).iterator().hasNext())
        throw new IllegalArgumentException("Username already taken: " + name)
    user = graph.addVertex()
    user.property("name", name)
    graph.tx().commit()
} catch (Exception e) {
    //Recover, retry, or return error message
    println(e.getMessage())
}
```
The example above demonstrates a simplified user signup implementation where `name` is the name of the user who wishes to register. First, it is checked whether a user with that name already exists. If not, a new user vertex is created and the name assigned. Finally, the transaction is committed.

If the transaction fails, a `JanusGraphException` is thrown. There are a variety of reasons why a transaction may fail. JanusGraph differentiates between _potentially temporary_ and _permanent_ failures.

Potentially temporary failures are those related to resource unavailability and IO hiccups (e.g. network timeouts). JanusGraph automatically tries to recover from temporary failures by retrying to persist the transactional state after some delay. The number of retry attempts and the retry delay are configurable (see [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/)).

Permanent failures can be caused by complete connection loss, hardware failure or lock contention. To understand the cause of lock contention, consider the signup example above and suppose a user tries to signup with username "juno". That username may still be available at the beginning of the transaction but by the time the transaction is committed, another user might have concurrently registered with "juno" as well and that transaction holds the lock on the username therefore causing the other transaction to fail. Depending on the transaction semantics one can recover from a lock contention failure by re-running the entire transaction.

Permanent exceptions that can fail a transaction include:

- PermanentLockingException(**Local lock contention**): Another local thread has already been granted a conflicting lock.
- PermanentLockingException(**Expected value mismatch for X: expected=Y vs actual=Z**): The verification that the value read in this transaction is the same as the one in the datastore after applying for the lock failed. In other words, another transaction modified the value after it had been read and modified.

上面的示例演示了简化的用户注册实现，其中name是希望注册的用户的名称。首先，检查具有该名称的用户是否已经存在。如果没有，将创建一个新的用户顶点并分配名称。最后，事务已提交。

如果事务失败，则抛出JanusGraphException。事务可能失败的原因有多种。 

JanusGraph 区分潜在的暂时性故障和永久性故障。
- **潜在的临时故障**是与资源不可用和IO打ic有关的故障（例如网络超时）。 JanusGraph通过在某些延迟后重试以保持事务状态来自动尝试从临时故障中恢复。重试次数和重试延迟是可配置的（请参阅《配置参考》）。
- **永久性故障**可能由完全连接丢失，硬件故障或锁争用引起。要了解锁争用的原因，请考虑上面的注册示例，并假设用户尝试使用用户名“ juno”进行注册。该用户名可能在事务开始时仍然可用，但是到事务提交时，另一个用户可能也已同时向“ juno”注册，并且该事务持有该用户名的锁，因此导致另一个事务失败。根据事务的语义，可以通过重新运行整个事务来从锁争用失败中恢复。

可能导致事务失败的永久异常包括：
- PermanentLockingException（本地锁争用）：另一个本地线程已被授予冲突锁。
- PermanentLockingException（X的预期值不匹配：期望= Y与实际= Z）：验证在此事务中读取的值与在申请锁定后在数据存储区中读取的值相同。换句话说，另一个事务在读取和修改值之后修改了该值。

## 多线程事务 Multi-Threaded Transactions
JanusGraph supports multi-threaded transactions through TinkerPop’s threaded transactions. Hence, to speed up transaction processing and utilize multi-core architectures multiple threads can run concurrently in a single transaction.

With TinkerPop’s default transaction handling, each thread automatically opens its own transaction against the graph database. To open a thread-independent transaction, use the `createThreadedTx()` method.

JanusGraph通过TinkerPop的线程事务支持多线程事务。因此，为了加速事务处理并利用多核体系结构，多个线程可以在单个事务中同时运行。

使用TinkerPop的默认事务处理，每个线程都会针对图形数据库自动打开其自己的事务。要打开与线程无关的事务，请使用createThreadedTx()方法。
```java
threadedGraph = graph.tx().createThreadedTx();
threads = new Thread[10];
for (int i=0; i<threads.length; i++) {
    threads[i]=new Thread({
        println("Do something with 'threadedGraph''");
    });
    threads[i].start();
}
for (int i=0; i<threads.length; i++) threads[i].join();
threadedGraph.tx().commit();
```
The `createThreadedTx()` method returns a new `Graph` object that represents this newly opened transaction. The graph object `tx` supports all of the methods that the original graph did, but does so without opening new transactions for each thread. This allows us to start multiple threads which all work concurrently in the same transaction and one of which finally commits the transaction when all threads have completed their work.

JanusGraph relies on optimized concurrent data structures to support hundreds of concurrent threads running efficiently in a single transaction.

createThreadedTx() 方法返回一个新的 Graph 对象，该对象表示此新打开的事务。图形对象tx支持原始图形执行的所有方法，但是这样做无需为每个线程打开新的事务。这使我们可以启动多个线程，这些线程在同一事务中同时工作，并且在所有线程完成其工作后，其中一个最终提交事务。

JanusGraph 依靠优化的并发数据结构来支持数百个并发线程在单个事务中有效运行。

## 并发算法 Concurrent Algorithms 
Thread independent transactions started through `createThreadedTx()` are particularly useful when implementing concurrent graph algorithms. Most traversal or message-passing (ego-centric) like graph algorithms are [embarrassingly parallel](https://en.wikipedia.org/wiki/Embarrassingly_parallel) which means they can be parallelized and executed through multiple threads with little effort. Each of these threads can operate on a single `Graph` object returned by `createThreadedTx()` without blocking each other.

在实现并发图算法时，通过 createThreadedTx() 启动的与线程无关的事务特别有用。大多数遍历或消息传递（以自我为中心）之类的图算法都是“令人尴尬的并行处理”，这意味着它们可以通过多个线程进行并行化和执行，所需的工作量很小。这些线程中的每个线程都可以对createThreadedTx()返回的单个Graph对象进行操作，而不会互相阻塞。

## 嵌套事务 Nested Transactions 
Another use case for thread independent transactions is nested transactions that ought to be independent from the surrounding transaction.

For instance, assume a long running transactional job that has to create a new vertex with a unique name. Since enforcing unique names requires the acquisition of a lock (see [Eventually-Consistent Storage Backends](https://docs.janusgraph.org/advanced-topics/eventual-consistency/) for more detail) and since the transaction is running for a long time, lock congestion and expensive transactional failures are likely.

与线程无关的事务的另一个用例是应该独立于周围事务的嵌套事务。

例如，假设一项长期运行的事务性工作必须创建一个具有唯一名称的新顶点。由于强制唯一名称需要获取锁（有关更多详细信息，请参见最终一致的存储后端），并且由于事务运行了很长时间，因此可能发生锁拥塞和代价高昂的事务失败。

```java
v1 = graph.addVertex()
//Do many other things
v2 = graph.addVertex()
v2.property("uniqueName", "foo")
v1.addEdge("related", v2)
//Do many other things
graph.tx().commit() // This long-running tx might fail due to contention on its uniqueName lock
```
One way around this is to create the vertex in a short, nested thread-independent transaction as demonstrated by the following pseudo code

解决此问题的一种方法是在一个简短的，与线程无关的嵌套嵌套事务中创建顶点，如以下伪代码所示
```java
v1 = graph.addVertex()
//Do many other things
tx = graph.tx().createThreadedTx()
v2 = tx.addVertex()
v2.property("uniqueName", "foo")
tx.commit() // Any lock contention will be detected here 这里将检测到锁竞争
v1.addEdge("related", g.V(v2).next()) // Need to load v2 into outer transaction 需要将v2加载到外部事务中
//Do many other things
graph.tx().commit() // Can't fail due to uniqueName write lock contention involving v2
```

## 常见处理问题 Common Transaction Handling Problems 
Transactions are started automatically with the first operation executed against the graph. One does NOT have to start a transaction manually. The method `newTransaction` is used to start [multi-threaded transactions](https://docs.janusgraph.org/basics/transactions/#multi-threaded-transactions) only.

Transactions are automatically started under the TinkerPop semantics but **not** automatically terminated. Transactions must be terminated manually with `commit()` or `rollback()`. If a `commit()` transactions fails, it should be terminated manually with `rollback()` after catching the failure. Manual termination of transactions is necessary because only the user knows the transactional boundary.

A transaction will attempt to maintain its state from the beginning of the transaction. This might lead to unexpected behavior in multi-threaded applications as illustrated in the following artificial example

事务会自动执行，并针对图表执行第一个操作。人们不必手动开始事务。方法 newTransaction 仅用于启动多线程事务。

事务会在TinkerPop语义下自动启动，但不会自动终止。必须使用commit()或rollback()手动终止事务。如果commit()事务失败，则应在捕获失败后使用rollback()手动终止它。手动终止事务是必要的，因为只有用户才知道事务边界。

事务将尝试从事务开始就保持其状态。如下面的人工示例所示，这可能会导致多线程应用程序出现意外行为。
```java
v = g.V(4).next() // Retrieve vertex, first action automatically starts transaction
g.V(v).bothE()
>> returns nothing, v has no edges
//thread is idle for a few seconds, another thread adds edges to v
g.V(v).bothE()
>> still returns nothing because the transactional state from the beginning is maintained
```
Such unexpected behavior is likely to occur in client-server applications where the server maintains multiple threads to answer client requests. It is therefore important to terminate the transaction after a unit of work (e.g. code snippet, query, etc). So, the example above should be:

在服务器维护多个线程来响应客户端请求的客户端服务器应用程序中，可能会发生这种意外行为。因此，重要的是在一个工作单元（例如代码段，查询等）之后终止事务。因此，以上示例应为：
```
v = g.V(4).next() // Retrieve vertex, first action automatically starts transaction
g.V(v).bothE()
graph.tx().commit()
//thread is idle for a few seconds, another thread adds edges to v
g.V(v).bothE()
>> returns the newly added edge
graph.tx().commit()
```
When using multi-threaded transactions via `newTransaction` all vertices and edges retrieved or created in the scope of that transaction are **not** available outside the scope of that transaction. Accessing such elements after the transaction has been closed will result in an exception. As demonstrated in the example above, such elements have to be explicitly refreshed in the new transaction using `g.V(existingVertex)` or `g.E(existingEdge)`.

通过newTransaction使用多线程事务时，在该事务范围内检索或创建的所有顶点和边在该事务范围之外不可用。在事务关闭后访问此类元素将导致异常。如上面的示例所示，必须在新事务中使用g.V（existingVertex）或g.E（existingEdge）显式刷新这些元素。

## 事务配置 Transaction Configuration
JanusGraph’s `JanusGraph.buildTransaction()` method gives the user the ability to configure and start a new [multi-threaded transaction](https://docs.janusgraph.org/basics/transactions/#multi-threaded-transactions) against a `JanusGraph`. Hence, it is identical to `JanusGraph.newTransaction()` with additional configuration options.

`buildTransaction()` returns a `TransactionBuilder` which allows the following aspects of a transaction to be configured:

- `readOnly()` - makes the transaction read-only and any attempt to modify the graph will result in an exception.
- `enableBatchLoading()` - enables batch-loading for an individual transaction. This setting results in similar efficiencies as the graph-wide setting `storage.batch-loading` due to the disabling of consistency checks and other optimizations. Unlike `storage.batch-loading` this option will not change the behavior of the storage backend.
- `setTimestamp(long)` - Sets the timestamp for this transaction as communicated to the storage backend for persistence. Depending on the storage backend, this setting may be ignored. For eventually consistent backends, this is the timestamp used to resolve write conflicts. If this setting is not explicitly specified, JanusGraph uses the current time.
- `setVertexCacheSize(long size)` - The number of vertices this transaction caches in memory. The larger this number, the more memory a transaction can potentially consume. If this number is too small, a transaction might have to re-fetch data which causes delays in particular for long running transactions.
- `checkExternalVertexExistence(boolean)` - Whether this transaction should verify the existence of vertices for user provided vertex ids. Such checks requires access to the database which takes time. The existence check should only be disabled if the user is absolutely sure that the vertex must exist - otherwise data corruption can ensue.
- `checkInternalVertexExistence(boolean)` - Whether this transaction should double-check the existence of vertices during query execution. This can be useful to avoid **phantom vertices** on eventually consistent storage backends. Disabled by default. Enabling this setting can slow down query processing.
- `consistencyChecks(boolean)` - Whether JanusGraph should enforce schema level consistency constraints (e.g. multiplicity constraints). Disabling consistency checks leads to better performance but requires that the user ensures consistency confirmation at the application level to avoid inconsistencies. USE WITH GREAT CARE!

Once, the desired configuration options have been specified, the new transaction is started via `start()` which returns a `JanusGraphTransaction`.

JanusGrap h的 JanusGraph.buildTransaction() 方法使用户能够针对 JanusGraph 配置和启动新的多线程事务。因此，它与 JanusGraph.newTransaction() 相同，带有其他配置选项。

buildTransaction() 返回一个 TransactionBuilder，它允许配置事务的以下方面：
- readOnly()：使事务为只读，任何尝试修改图形的操作都会导致异常。
- enableBatchLoading()：为单个事务启用批量加载。由于禁用了一致性检查和其他优化，因此此设置产生的效率与图形范围的设置storage.batch-loading相似。与storage.batch-loading不同，此选项不会更改存储后端的行为。
- setTimestamp（long）：设置此事务的时间戳，该时间戳与存储后端进行通信以保持持久性。根据存储后端，此设置可能会被忽略。对于最终一致的后端，这是用于解决写冲突的时间戳。如果未明确指定此设置，则JanusGraph使用当前时间。
- setVertexCacheSize（long size）：此事务在内存中缓存的顶点数。该数字越大，事务可能消耗的内存越多。如果此数字太小，则事务可能必须重新获取数据，这尤其会导致长时间运行的事务引起延迟。
- checkExternalVertexExistence（boolean）：此事务是否应验证用户提供的顶点ID的顶点是否存在。这种检查需要访问数据库，这需要花费时间。仅当用户绝对确定顶点必须存在时才应禁用存在检查-否则可能导致数据损坏。
- checkInternalVertexExistence（boolean）：此事务是否应在查询执行期间仔细检查顶点的存在。这对于避免最终一致的存储后端上的幻影顶点很有用。默认禁用。启用此设置可能会减慢查询处理。
- ConstanceChecks（boolean）：JanusGraph是否应强制执行架构级别的一致性约束（例如，多重性约束）。禁用一致性检查可以提高性能，但是要求用户确保在应用程序级别进行一致性确认，以避免不一致。与极大的关心一起使用！

一旦指定了所需的配置选项，就会通过start()启动新事务，该事务将返回JanusGraphTransaction。
