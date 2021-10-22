package validation;

import datatype.AbstractDataType;
import history.HappenBeforeGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiThreadSearch {
    private int threadNum = 6;
    private SearchConfiguration configuration;
    private HappenBeforeGraph happenBeforeGraph;
    private List<SearchThread> searchs = new ArrayList<>();
    private List<SearchState> results = new ArrayList<>();

    public MultiThreadSearch(HappenBeforeGraph happenBeforeGraph, SearchConfiguration configuration) {
        this.happenBeforeGraph = happenBeforeGraph;
        this.configuration = configuration;
    }

    public List<SearchState> getResults() {
        return results;
    }

    public void startSearch(List<SearchState> startStates) {
        threadNum = startStates.size();
        System.out.println(threadNum);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            for (SearchState state : startStates) {
                MinimalVisSearch visSearch = new MinimalVisSearch((SearchConfiguration) configuration.clone());
                visSearch.init((HappenBeforeGraph) happenBeforeGraph.clone(), state);
                searchs.add(new SearchThread(visSearch, countDownLatch));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (SearchThread search : searchs) {
            new Thread(search).start();
        }
        try {
            countDownLatch.await();
            for (SearchThread search : searchs) {
                search.stop();
            }
            for (SearchThread search : searchs) {
                if (search.isExit()) {
                    results.addAll(search.getResults());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class SearchThread implements Runnable {
    private MinimalVisSearch visSearch;
    private CountDownLatch countDownLatch;
    private List<SearchState> results;

    public SearchThread(MinimalVisSearch visSearch, CountDownLatch countDownLatch) {
        this.visSearch = visSearch;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        if (visSearch.checkConsistency()) {
            countDownLatch.countDown();
        }
    }

    public void stop() {
        visSearch.stopSearch();
    }

    public boolean isExit() {
        return visSearch.isExit();
    }

    public List<SearchState> getResults() {
        return visSearch.getResults();
    }
}
