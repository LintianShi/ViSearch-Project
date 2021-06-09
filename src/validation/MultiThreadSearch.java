package validation;

import datatype.AbstractDataType;
import history.HappenBeforeGraph;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadSearch {
    private int threadNum;
    private SearchConfiguration configuration;
    private HappenBeforeGraph happenBeforeGraph;
    private List<SearchThread> searchs = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();

    public MultiThreadSearch(HappenBeforeGraph happenBeforeGraph, SearchConfiguration configuration) {
        this.happenBeforeGraph = happenBeforeGraph;
        this.configuration = configuration;
    }

    public void startSearch(List<SearchState> startStates) {
        threadNum = startStates.size();
        System.out.println(threadNum);
        for (SearchState state : startStates) {
            SearchConfiguration conf = new SearchConfiguration(0, -1, -1, 10, false);
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
    }
}

class SearchThread implements Runnable {
    private MinimalVisSearch visSearch;

    public SearchThread(MinimalVisSearch visSearch) {
        this.visSearch = visSearch;
    }

    public void run() {
        visSearch.checkConsistency();
    }
}
