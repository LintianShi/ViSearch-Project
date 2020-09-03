# 针对CRDT的RA-Lin一致性检测 

## 1. Replica-Aware Linearizability基础

### 1.1 CRDT操作类型的定义

* Query：只读操作
* Update：写操作
* Query-Update：读写操作
* 本研究中主要研究Query和Update类型的操作，即单纯的读、写操作

### 1.2 CRDT操作间的关系

* 主要指的是CRDT中各个EFFECT操作之间的关系

* Program order：同一个副本发起的操作之间，根据时间顺序产生相应的Program order。即在同一个副本中，t<sub>1</sub>时刻发起操作o<sub>1</sub>的PREPARE和EFFECT，t<sub>2</sub>时刻发起操作o<sub>2</sub>的PREPARE和EFFECT，且t<sub>1</sub> < t<sub>2</sub>，那么EFFECT<sub>o<sub>1</sub></sub> <<sub>po</sub> EFFECT<sub>o<sub>2</sub></sub>。

* Happen-before relation：副本r<sub>1</sub>发起了操作o<sub>1</sub>，然后副本r<sub>2</sub>收到了网络中传播的EFFECT<sub>o<sub>1</sub></sub>。那么EFFECT<sup>r1</sup><sub>o<sub>1</sub></sub> <<sub>hb</sub> EFFECT<sup>r2</sup><sub>o<sub>1</sub></sub>。

* 将各EFFECT之间用Po和Hb连接起来，就可以得到一个基于EFFECT操作服务端视角的DAG图。

  ![server.png](https://i.loli.net/2020/08/25/3YTSADyGNvr8i9Q.png)

* 将非操作发起的EFFECT除去后，可以得到客户端视角的DAG图。

  ![client.png](https://i.loli.net/2020/08/25/sv4l7iPtCd8kcFX.png)

* Visibility：在客户端视角的DAG图上，任意不构成环的有向边都可以是vis关系。即不与Po、Hb矛盾的偏序关系（包含Po、Hb本身）。

  ![vis.png](https://i.loli.net/2020/08/25/i8DWU139OCBbGVv.png)

### 1.3 RA-Lin的规约

* 一个history满足RA-Lin仅当存在一个不违背偏序关系vis的全序seq
* 对于UPDATE操作，seq中的所有UPDATE操作在全序seq上的投影满足串行规约
* 对于QUERY操作o，所有o可见的UPDATE操作和操作o组成的子序列满足串行规约

## 2. 系统原始数据的表示与储存

### 2.1 服务器原始Log格式

| 操作类型 | 格式                                                 |
| -------- | ---------------------------------------------------- |
| Query    | 时间戳，user_log：操作名称 操作对象 参数列表：返回值 |
| Update   | 时间戳，PREPARE/EFFECT：操作名称 操作对象 参数列表   |

* 示例

| Log                                                         |
| :---------------------------------------------------------- |
| 1598065549622004, PREPARE: rwfzadd rwfrpq 22631 93.7195     |
| 1598065549622022, EFFECT: rwfzadd rwfrpq 22631              |
| 1598065549626151, EFFECT: rwfzadd rwfrpq 4275 34.4804       |
| 1598065549641125, user_log: rwfzmax rwfrpq 22631: 93.719500 |

### 2.2 系统原始Trace

* 在CRDT-Redis系统中，原始的Trace由每个副本服务器的原始Log文件构成。
* 例如在一个节点数为3，副本数为3的集群中，原始的Trace就是由9个原始Log文件组成。

### 2.3 系统原始Trace在程序中的表示

* rawtrace包用于在程序中表示和储存系统原始Trace

#### 2.3.1 CrdtOperation

* **CrdtOperation**类描述了副本服务器原始Log中的一行，即副本服务器接收到的一个CRDT操作

* **CrdtOperation**类详解

  | Field                            | Description                                               |
  | -------------------------------- | --------------------------------------------------------- |
  | timeStamp: *long*                | 操作的时间戳                                              |
  | type: *CRDT_OPERATION_TYPE*      | 枚举类型，标识操作的类型                                  |
  | operationName: *String*          | 操作名                                                    |
  | crdtName: *String*               | 操作对象名                                                |
  | arguments: *ArrayList\<String\>* | 操作参数列表（Query操作的返回值储存在列表的最后一个元素） |
  | uniqueID: *int*                  | 用于标识每一个操作                                        |
  | origin: *boolean*                | 标识一个操作是否属于操作发起者                            |
  | po: *CrdtOperation*              | Trace中满足program order的下一个操作                      |
  | hbs: *List\<CrdtOperation\>*     | Trace中满足happen-before的操作                            |

#### 2.3.2 CrdtTrace

* **CrdtTrace**类存储了一个完整的Trace，通过有向图的结构保存了所有的操作和操作之间的关系。

* **CrdtTrace**类可以从一系列的副本服务器日志文件中读取数据，并组织成一个可供检测算法检测的history。
  1. 从各副本服务器日志文件中，将各服务器的Log读取为若干个链表。
  2. 通过同一个操作EFFECT传播的因果序，确立EFFECT操作之间的Happen-before关系。
  3. 调整Happen-before关系，保证每一个表示Happen-before关系的有向边都从一个操作发起者的EFFECT指向另一个操作发起者的EFFECT
  4. 只保留user_log标签的操作和操作发起者的EFFECT操作，形成DAG图。

## 3. History的表示和储存

### 3.1 History的文件存储格式

* 一个Trace使用JSON表示

* 格式如下例为

  ```json
  {
  	"SUBPROGRAMS":
  	[
  		{
  			"INVOCATIONS":
  			[
  				{"METHOD NAME":"put","ARGUMENTS":[1,0]},
  				{"METHOD NAME":"contains","ARGUMENTS":[0]},
  				{"METHOD NAME":"put","ARGUMENTS":[2,2]}
  			]
  		}, 
  		{
  			"INVOCATIONS":
  			[
  				{"METHOD NAME":"put","ARGUMENTS":[0,0]},
  				{"METHOD NAME":"put","ARGUMENTS":[1,1]},
  				{"METHOD NAME":"put","ARGUMENTS":[3,3]}
  			]
  		}
  	],
  	"HBS":
  	[
  		{
  			"HAPPENBEFORE":
  			[
  				{"PREV":[0,1],"NEXT":[1,1]}
  			]
  		},
  		{
  			"HAPPENBEFORE":
  			[
  				{"PREV":[0,1],"NEXT":[1,2]}
  			]
  		}
  	]
  }
  ```

* SUBPROGRAMS记录了代码的program order

* HBS记录了代码的Happen-Before关系

* 需要解释的是，"HAPPENBEFORE"中包含了一系列的happen-before关系。"PREV"表示先发生的invocation的编号，"NEXT"表示后发生的invocation的编号。编号的前一项指的是进程编号，后一项指的是操作在进程中编号。

### 3.2 History在程序内的表示和储存

#### 3.2.1 Invocation类

* **Invocation**类用于表述对一个数据结构某个接口的一次调用

