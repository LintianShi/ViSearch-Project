package validation;

import history.HappenBeforeGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadSearch {
    private SearchConfiguration configuration;
    private static HappenBeforeGraph happenBeforeGraph;
    private List<SearchThread> searchThreads = new ArrayList<>();

    public MultiThreadSearch(HappenBeforeGraph happenBeforeGraph, SearchConfiguration configuration) {
        MultiThreadSearch.happenBeforeGraph = happenBeforeGraph;
        this.configuration = configuration;
    }


    public boolean startSearch(List<SearchState> startStates) {
        int threadNum = startStates.size();
//        System.out.println(threadNum);
        SearchLock searchLock = new SearchLock(threadNum);
        for (SearchState state : startStates) {
            MinimalVisSearch visSearch = new MinimalVisSearch((SearchConfiguration) configuration.clone());
            visSearch.init(happenBeforeGraph, state);
            searchThreads.add(new SearchThread(visSearch, searchLock));
        }

        for (SearchThread search : searchThreads) {
            new Thread(search).start();
        }
        searchLock.hold();
        for (SearchThread search : searchThreads) {
            search.stop();
        }
        return searchLock.getResult();
    }
}

class SearchLock {
    private final int size;
    private AtomicBoolean satisfication = new AtomicBoolean(false);
    private AtomicInteger finished = new AtomicInteger(0);

    public SearchLock(int size) {
        this.size = size;
    }

    public void hold() {
        while (!satisfication.get() && finished.get() < size) {
            ;
        }
    }

    public void finish() {
        finished.incrementAndGet();
    }

    public void find() {
        satisfication.set(true);
    }

    public boolean getResult() {
        return satisfication.get();
    }
}

class SearchThread implements Runnable {
    private MinimalVisSearch visSearch;
    private SearchLock searchLock;

    public SearchThread(MinimalVisSearch visSearch, SearchLock searchLock) {
        this.visSearch = visSearch;
        this.searchLock = searchLock;
    }

    public void run() {
        boolean result = visSearch.checkConsistency();
        if (result) {
            searchLock.find();
            searchLock.finish();
        } else {
            searchLock.finish();
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
