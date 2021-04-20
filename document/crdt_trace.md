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

