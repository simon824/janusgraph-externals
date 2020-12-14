# 异常与恢复 Failure &amp; Recovery

<a name="failure-recovery"></a>
# Failure & Recovery
JanusGraph is a highly available and robust graph database. In large scale JanusGraph deployments failure is inevitable. This page describes some failure situations and how JanusGraph can handle them.

JanusGraph是一个高度可用且健壮的图形数据库。在大规模的JanusGraph部署中，失败是不可避免的。本页描述了一些失败情况以及JanusGraph如何处理它们。
<a name="transaction-failure"></a>
## Transaction Failure
Transactions can fail for a number of reasons. If the transaction fails before the commit the changes will be discarded and the application can retry the transaction in coherence with the business logic. Likewise, locking or other consistency failures will cause an exception prior to persistence and hence can be retried. The persistence stage of a transaction is when JanusGraph starts persisting data to the various backend systems.

事务可能由于多种原因而失败。如果事务在提交之前失败，则更改将被丢弃，并且应用程序可以根据业务逻辑重新尝试事务。同样，锁定或其他一致性失败将在持久性之前导致异常，因此可以重试。事务的持久性阶段是JanusGraph开始将数据持久化到各种后端系统的时间。

JanusGraph first persists all graph mutations to the storage backend. This persistence is executed as one batch mutation to ensure that the mutation is committed atomically for those backends supporting atomicity. If the batch mutation fails due to an exception in the storage backend, the entire transaction is failed.

JanusGraph首先将所有图形突变持久化到存储后端。这种持久性是作为一个批处理突变执行的，以确保对于支持原子性的那些后端，原子地进行突变。如果批处理突变由于存储后端中的异常而失败，则整个事务将失败。

If the primary persistence into the storage backend succeeds but secondary persistence into the indexing backends or the logging system fail, the transaction is still considered to be successful because the storage backend is the authoritative source of the graph.

如果对存储后端的主要持久性成功，但对索引后端或日志记录系统的次要持久性失败，则由于存储后端是该图的权威来源，因此该事务仍被视为成功。

However, this can create inconsistencies with the indexes and logs. To automatically repair such inconsistencies, JanusGraph can maintain a transaction write-ahead log which is enabled through the configuration.

但是，这可能会导致索引和日志不一致。为了自动修复这种不一致，JanusGraph可以维护通过配置启用的事务预写日志。
```
tx.log-tx = true
tx.max-commit-time = 10000
```
The max-commit-time property is used to determine when a transaction has failed. If the persistence stage of the transaction takes longer than this time, JanusGraph will attempt to recover it if necessary. Hence, this time out should be configured as a generous upper bound on the maximum duration of persistence. Note, that this does not include the time spent before commit.

max-commit-time属性用于确定事务何时失败。如果事务的持久性阶段花费的时间比此时间长，JanusGraph将在必要时尝试恢复它。因此，此超时应配置为最大持续时间的慷慨上限。注意，这不包括提交之前花费的时间。

In addition, a separate process must be setup that reads the log to identify partially failed transaction and repair any inconsistencies caused. It is suggested to run the transaction repair process on a separate machine connected to the cluster to isolate failures. Configure a separately controlled process to run the following where the start time specifies the time since epoch where the recovery process should start reading from the write-ahead log.

此外，必须设置一个单独的过程来读取日志，以识别部分失败的事务并修复所引起的任何不一致。建议在连接到群集的单独计算机上运行事务修复过程，以隔离故障。配置一个单独控制的过程以运行以下命令，其中开始时间指定自开始从预写日志读取恢复过程的时期开始的时间。
```
recovery = JanusGraphFactory.startTransactionRecovery(graph, startTime, TimeUnit.MILLISECONDS);
```
Enabling the transaction write-ahead log causes an additional write operation for mutating transactions which increases the latency. Also note, that additional space is required to store the log. The transaction write-ahead log has a configurable time-to-live of 2 days which means that log entries expire after that time to keep the storage overhead small. Refer to [Configuration Reference](https://docs.janusgraph.org/basics/configuration-reference/) for a complete list of all log related configuration options to fine tune logging behavior.

启用事务预写日志会导致用于使事务发生变异的附加写操作，这会增加延迟。另请注意，需要额外的空间来存储日志。事务预写日志的可配置生存时间为2天，这意味着日志条目在该时间之后过期，以保持较小的存储开销。有关所有与日志相关的配置选项的完整列表，请参考《配置参考》，以微调日志记录行为。
<a name="janusgraph-instance-failure"></a>
## JanusGraph Instance Failure
JanusGraph is robust against individual instance failure in that other instances of the JanusGraph cluster are not impacted by such failure and can continue processing transactions without loss of performance while the failed instance is restarted.

JanusGraph具有强大的抵抗单个实例失败的能力，因为JanusGraph群集的其他实例不受此类失败的影响，并且可以在重新启动失败的实例时继续处理事务而不会损失性能。

However, some schema related operations - such as installing indexes - require the coordination of all JanusGraph instances. For this reason, JanusGraph maintains a record of all running instances. If an instance fails, i.e. is not properly shut down, JanusGraph considers it to be active and expects its participation in cluster-wide operations which subsequently fail because this instances did not participate in or did not acknowledge the operation.

但是，某些与模式相关的操作（例如，安装索引）需要协调所有JanusGraph实例。因此，JanusGraph会保留所有正在运行的实例的记录。如果某个实例失败，即未正确关闭，JanusGraph会认为它处于活动状态，并期望其参与集群范围的操作，该操作随后由于该实例未参与或未确认该操作而失败。

In this case, the user must manually remove the failed instance record from the cluster and then retry the operation. To remove the failed instance, open a management transaction against any of the running JanusGraph instances, inspect the list of running instances to identify the failed one, and finally remove it.

在这种情况下，用户必须从群集中手动删除失败的实例记录，然后重试该操作。要删除失败的实例，请针对任何正在运行的JanusGraph实例打开一个管理事务，检查正在运行的实例的列表以识别失败的实例，最后将其删除。
```
mgmt = graph.openManagement()
mgmt.getOpenInstances() //all open instances
==>7f0001016161-dunwich1(current)
==>7f0001016161-atlantis1
mgmt.forceCloseInstance('7f0001016161-atlantis1') //remove an instance
mgmt.commit()
```
The unique identifier of the current JanusGraph instance is marked with the suffix `(current)` so that it can be easily identified. This instance cannot be closed via the `forceCloseInstance` method and instead should be closed via `g.close()`

当前JanusGraph实例的唯一标识符标有后缀（当前），以便于识别。此实例无法通过forceCloseInstance方法关闭，而应通过g.close（）关闭

It must be ensured that the manually removed instance is indeed no longer active. Removing an active JanusGraph instance from a cluster can cause data inconsistencies. Hence, use this method with great care in particular when JanusGraph is operated in an environment where instances are automatically restarted.

必须确保手动删除的实例确实不再处于活动状态。从群集中删除活动的JanusGraph实例可能会导致数据不一致。因此，尤其是在JanusGraph在自动重启实例的环境中运行时，请格外小心地使用此方法。
