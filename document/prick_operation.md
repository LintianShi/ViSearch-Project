# Prick Operation

* 实际搜索过程中经常会出现卡在一个读操作上的情况，因为这个读操作需要一个比较奇怪的序列才能得到正确的返回值。

* 对于这种情况我们希望将这些“刺头”操作从整个trace中剥离出来单独进行搜索排列，然后再将得到的正确结果放回大搜索过程中继续进行搜索。

## Related Operation

* 我们通过经验可以发现，在很多数据结构里，读操作和其他的大部分写操作是解耦的，只和一小部分写操作相关。
* 这里我们假设所有参与读写的值，还有一个额外的key

### 完全独立操作

完全独立操作指这类读操作只受写这个读操作key的写操作影响

* 比如Map中的get操作，只和参数为同一个key的put、remove操作有关

  Set中的contains操作，只和参数为同一个key的add、remove操作有关

* 因此我们能够轻松地将一个读操作、几个相关写操作与整个trace剥离开来，然后对这几个小操作进行排列

### 半独立操作

半独立操作指这类读操作受写这个读操作key的写操作影响，但同时受其他写操作影响

* 比如RPQ中的max操作
  * 一个max操作的返回值为[key: 17865, value: 80]
  * max的返回值显然和参数为同一个key的add、incrby、remove操作有关。因为此时RPQ中必然要存在一个key为17865，value为80的元素
  * 但同时max的返回值也受其他操作影响，其他操作此时不能构造出value大于80的元素

### 非独立操作

非独立操作的特点是操作不直接施加在key上

* 比如list数据结构，操作是施加在index上的
* 但CRDT-Redis里的list并没有设计针对单个元素的读操作，虽然CRDT-Redis里的list的写操作是施加在key上的

## Common Happen-before Relation

对于一个读操作和其关联的操作，我们可以独立的进行排序

* 比如一个读操作**max,132725 50**，它的关联操作为

  [incrby,132725,-15,null], [max,132725 50]

  [incrby,132725,-43,null]

  [add,132725,50,null]

  [rem,132725,null]

  [rem,132725,null]

  其中一行为一个线程

* 对这一个子trace我们可以有各种不同的排列方式使得读操作的返回值正确，而不同排列中有几个Happen-before关系是必须都要满足的。

  即：[add,132725,50,null]必须发生在[max,132725 50]之前，且这两个操作之间没有有其他操作。

  [add,132725,50,null] => [max,132725 50]就是一个common happen-before relation

### 预处理加边

* 利用common  happen-before relation我们可以在对大trace进行一致性检测前，先对所有读操作和其related operation构成的子trace进行搜索。然后获取子trace的common happen-before relation，在大trace上进行加边预处理。

* 因为子trace都很小，规模可以看成常数级别，因此时间上完全可以接受。

### TODO

* 预处理加边只刻画了**“[add,132725,50,null]必须发生在[max,132725 50]之前”**，我们还希望刻画**“这两个操作之间没有有其他操作”**

* 子trace所有可行的排列可以总结出哪些happen-before关系不能同时出现，然后作为规则指导搜索。

  比如[add,132725,50,null] => [incrby,132725,-43,null] 和 [incrby,132725,-43,null] => [max,132725 50]不能同时出现。

* 如果有[add,132725,50,null] => [incrby,132725,-43,null]，那全序只能是[add,132725,50,null] [max,132725 50] [incrby,132725,-43,null]。

  即如果[incrby,132725,-43,null]在[add,132725,50,null]后面，[incrby,132725,-43,null]就不能再在[max,132725 50]前面了。