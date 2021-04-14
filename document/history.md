# Representation of History
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

## 构建基于Happen-Before的DAG

* <po, hbs>包含了若干组的hb，基于一组hb可以生成一个对应的DAG

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

  编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$SubProgram_i$的第j个invocation的编号为**<i, j>**.

## 基于DAG生成所有的Linearization

* 使用回溯法生成所有的Linearization，即在一个偏序关系里找到一个与其不矛盾的全序关系

* 具体算法为：

  ```java
  int index[process_num] = {0, 0, ... ,0}
  void generateLin(int[] index, Stack<Node> stack) {
  	if (isEnd(stack)) {
  		yield lin;
  	}
  	for (int i = 0; i < index.length; i++) {
  		if (isValid(node(index[i])) {
  			stack.push(node(index[i]));
  			index[i]++;
  			generateLin(index, stack);
  			index[i]--;
  			stack.pop();
  		}
  	}
  }
  ```

* 一个Linearization就是一个Node的序列。Node中不仅包含了一个Invocation，还有整个DAG图的信息，Vis关系需要这些信息。

## 基于Linearization生成Vis信息

* 定义了LinVisibility类，用于表示一个Linearization里的所有操作可能对应的一个vis关系
* 在一个序列lin中，第k个操作的vis集合有$2^{k-1}$个，即前k-1个操作的幂集。
* 所以一个序列lin，假设包含了n个操作，那么就有$2^{0}*2^{1}*\ldots*2^{k-1}=2^{\sum_{k=0}^{n-1}k}$组vis关系

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

# QueryUpdateExtension类

* 包含了一个HashMap< String, Function<Invocation, Pair<Invocation, Invocation> > > map

* 规定了一个类型为Query-Update的操作如何映射成一个Query操作和一个Update操作

* Program类利用OperationTypes类和QueryUpdateExtension类对整个History进行拓展

  ```java
  public void extendQueryUpdate(OperationTypes operationTypes, QueryUpdateExtension queryUpdateExtension)
  ```

  

# Behaviour类

* 增加了一个Behaviour类，保存了一个trace中对应invocation在某次执行时的返回值
