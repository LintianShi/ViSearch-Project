# Multi-thread Search

## SearchConfiguration

* 添加了一个SearchConfiguration类，有三个参数

  * ```java
    int searchMode; // 0: dfs, 1: bfs, 2: h*
    ```

  * searchLimit  最多搜索多少个状态
  * stateLimit  当前队列中最多有多少个状态，如果超过这个数量则停止搜索

* 通过bfs和限制队列中的状态数量，我们可以对DAG进行一次bfs搜索获取一系列的状态

  ```java
  SearchConfiguration configuration = new SearchConfiguration(1, -1, 30);
  configuration1.setAdt(adt);
  MinimalVisSearch vfs = new MinimalVisSearch(configuration);
  vfs.init(happenBeforeGraph);
  vfs.checkConsistency();
  List<SearchState> states = vfs.getAllSearchState();
  ```

## MultiThreadSearch

* MultiThreadSearch类中有以下参数：

  ```java
  private int threadNum;
  private SearchConfiguration configuration;
  private HappenBeforeGraph happenBeforeGraph;
  private List<SearchThread> searchs = new ArrayList<>();
  private List<Thread> threads = new ArrayList<>();
  ```

* 当有了一系列的初始状态后，MultiThreadSearch为每个初始状态启动一次搜索。一个线程负载一个搜索。

  ```java
  for (SearchState state : startStates) {
      SearchConfiguration conf = new SearchConfiguration(0, -1, -1);
      conf.setAdt(configuration.getAdt().createInstance());
      MinimalVisSearch visSearch = new MinimalVisSearch(conf);
      visSearch.init(happenBeforeGraph, state);
      searchs.add(new SearchThread(visSearch));
  }
  
  for (SearchThread search : searchs) {
  	threads.add(new Thread(search));
  }
  for (Thread t : threads) {
  	t.start();
  }
  ```

## TODO

* 我们希望保持几个线程在进行搜索，如果一个线程完成了一个子搜索，其他还在搜索的线程给一个状态让空闲下来的进程继续搜索