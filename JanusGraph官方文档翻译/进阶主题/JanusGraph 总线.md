# JanusGraph 总线

<a name="janusgraph-bus"></a>
# JanusGraph Bus
The **JanusGraph Bus** describes a collection of configurable logs to which JanusGraph writes changes to the graph and its management. The JanusGraph Bus is used for internal (i.e. between multiple JanusGraph instances) and external (i.e. integration with other systems) communication.<br />In particular, JanusGraph maintains three separate logs:<br />JanusGraph总线描述了一组可配置的日志，JanusGraph将对图形及其管理的更改写入其中。 JanusGraph总线用于内部（即在多个JanusGraph实例之间）和外部（即与其他系统集成）通信。
<a name="trigger-log"></a>
## Trigger Log
The purpose of the trigger log is to capture the mutations of a transaction so that the resulting changes to the graph can trigger events in other system. Such events may be propagating the change to other data stores, view maintenance, or aggregate computation.<br />触发日志的目的是捕获事务的突变，以便图形的结果更改可以触发其他系统中的事件。此类事件可能会将更改传播到其他数据存储，视图维护或聚合计算。<br />The trigger log consists of multiple sub-logs as configured by the user. When opening a transaction, the identifier for the trigger sub-log can be specified:<br />触发日志由用户配置的多个子日志组成。打开交易时，可以指定触发器子日志的标识符：
```
tx = g.buildTransaction().logIdentifier("purchase").start();
```
In this case, the identifier is "purchase" which means that the mutations of this transaction will be written to a log with the name "trigger_purchase". This gives the user control over where transactional mutations are logged. If no trigger log is specified, no trigger log entry will be created.<br />在这种情况下，标识符为“ purchase”，这意味着该交易的变异将被写入名称为“ trigger_purchase”的日志中。这使用户可以控制记录交易突变的位置。如果未指定触发日志，则不会创建触发日志条目。
<a name="transaction-log"></a>
## Transaction Log
The transaction log is maintained by JanusGraph and contains two entries for each transaction if enabled: 1. Pre-Commit: Before the changes are persisted to the storage and indexing backends, the changes are compiled and written to the log. 2. Post-Commit: The success status of the transaction is written to the log.<br />In this way, the transaction log functions as a Write-Ahead-Log (WAL). This log is not meant for consumption by the user or external systems - use trigger logs for that. It is used internally to store partial transaction persistence against eventually consistent backends.<br />The transaction log can be enabled via the root-level configuration option "log-tx".<br />事务日志由JanusGraph维护，如果启用，则每个事务包含两个条目：

1. 预先提交：在将更改持久保存到存储和索引后端之前，将更改编译并写入日志。 
1. 提交后：事务的成功状态被写入日志。

这样，事务日志就可以用作预写日志（WAL）。该日志不供用户或外部系统使用-为此请使用触发日志。它在内部用于存储针对最终一致后端的部分事务持久性。<br />可以通过根级配置选项“ log-tx”启用事务日志。
<a name="management-log"></a>
## Management Log
The management log is maintained by JanusGraph internally to communicate and persist all changes to global configuration options or the graph schema.<br />JanusGraph在内部维护管理日志，以交流并持久保存对全局配置选项或图形架构的所有更改。
