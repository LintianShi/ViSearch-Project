# Representation of History

## RawTrce的格式

* RawTrace是直接从数据库获得的原始日志文件。各数据库产生的原始日志文件必然会有不同，但我们希望能将其统一成一个一致的形式。

* 一次操作应表示为

  ***startTime, endTime, operationName, argument1, argument2, ... , argumentn, retValue***

  （其中操作的参数可以是零个或多个。操作的返回值必须存在，如果操作无返回值，则应该使用null或者其他占位符代替）

* 综上我们将原始日志中的一次操作调用以Record对象的形式表示

  | Field                 | Description                                      |
  | --------------------- | ------------------------------------------------ |
  | startTime: long       | 操作发起调用的时间                               |
  | endTime: long         | 操作调用结束的时间                               |
  | operationName: String | 操作名                                           |
  | argument: String      | 操作的参数，零个或多个                           |
  | retValue: String      | 操作的返回值，对于没有返回值的操作应用占位符补上 |

## Trace的格式

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
  	"HB":
      {
  		"HAPPENBEFORE":
  		[
  			{"PREV":[0,1],"NEXT":[1,1]}
  		]
  	}
  }
  
  
  ```
  
* SUBPROGRAMS记录了代码的program order

* HB记录了代码的Happen-Before关系

* 需要解释的是，"HAPPENBEFORE"中包含了一系列的happen-before关系。"PREV"表示先发生的invocation的编号，"NEXT"表示后发生的invocation的编号。编号的前一项指的是进程编号，后一项指的是操作在进程中编号。

## 构建基于Happen-Before的DAG

* <po, hb>可以生成一个对应的DAG

* DAG中的节点包含以下信息：invocation，nexts，prevs，id, pairID

```java
public class Node {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();
    private List<Node> prevs = new ArrayList<>();
    private int id;
    private Pair<Integer, Integer> pairID;
}
```

* nexts和prevs包含了一个节点基于偏序关系的后继和前驱

* id为每个invocation的unique id，用于识别invocation

  编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$SubProgram_i$的第j个invocation的编号为$(\sum_{k=0}^{i-1}l_k) + j$.

* pairID也唯一标识了一个invocation，不过是以数对的形式

  编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$$SubProgram_i$$的第j个invocation的编号为**<i, j>**.

## 抽象数据结构AbstractDataType

* 定义了抽象数据结构AbstractDataType类

* 有String invoke(Invocation invocation)方法。根据invocation的methodName反射出AbstractDataType中的对应方法进行执行

  ```java
  public final String invoke(Invocation invocation) throws Exception {
          String methodName = invocation.getMethodName();
          Class clazz = this.getClass();
          Method method = clazz.getDeclaredMethod(methodName, Invocation.class);
          method.setAccessible(true);
          return (String)method.invoke(this, invocation);
      }
  ```

  执行的返回值使用字符串表示

* 若要实现一个具体的数据结构，只要实现一个AbstractDataType子类即可

  例如MyHashMap类，内置了一个java.util.HashSet，并实现了put、contains方法包装了HashSet.put和HashSet.contains

  ```java
  public class MyHashMap extends AbstractDataType {
      HashMap<Integer, Integer> data = new HashMap<>();
      private String put(Invocation invocation) {
          Integer key = (Integer) invocation.getArguments().get(0);
          Integer value = (Integer) invocation.getArguments().get(1);
          Integer ret = data.put(key, value);
          if (ret == null) {
              return "N";
          } else {
              return Integer.toString(ret);
          }
      }
      private String contains(Invocation invocation) {
          boolean result = data.containsValue(invocation.getArguments().get(0));
          if (result) {
              return "T";
          } else {
              return "F";
          }
      }
  }
  ```

  

## AbstractDataType的执行

* AbstractDataType执行需要有三个参数，一个数据结构的实现Impl、一个Linearization lin、一个在lin下的visibility关系vis。

* 执行过程伪代码如下

  ```python
  let ret = {}
  for (let invocation in lin) {
      let seq = vis(invocation)
      for (let i in seq) {
      	let res = Impl.invoke(seq)
          if (i is last of seq) {
          	ret.append(res)
          }
      }
      Impl.reset()
  }
  yield ret
  ```

  

# Specification类

* 增加了一个Specification类，用Map保存了Method及其对应的Vis类型

# OperationTypes类

* 记录了CRDT中操作的类型：UPDATE、QUERY、QUERY-UPDATE

# Behaviour类

* 增加了一个Behaviour类，保存了一个trace中对应invocation在某次执行时的返回值
