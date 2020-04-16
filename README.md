# Visibility-Checking
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
  				{"A":1,"B":5}
  			]
  		},
  		{
  			"HAPPENBEFORE":
  			[
  				{"A":1,"B":4}
  			]
  		}
  	]
  }
  
  
  ```

* 需要解释的是，"HAPPENBEFORE"中包含了一系列的happen-before关系。"A"表示先发生的invocation的编号，"B"表示后发生的invocation的编号。编号方法为：假设一共有m个SubProgram，$SubProgram_i$具有$l_i$个invocation，那么$SubProgram_i$的第j个invocation的编号为$(\sum_{k=0}^{i-1}l_k) + j$.

## 构建基于Happen-Before的DAC

* <po, hbs>包含了若干的hb，基于一个hb可以生成一个对应的DAC

* DAC中的节点包含以下信息：invocation，nexts，prevs，id

```java
public class Node {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();
    private List<Node> prevs = new ArrayList<>();
    private int id;
}
```

* nexts和prevs包含了所有的基于hb关系的后继和前驱

* id为每个invocation的unique id，用于识别invocation

## 基于DAC生成所有的Linearization

* 使用回溯法生成所有的Linearization
* 一个Linearization就是一个Invocation的序列