package validation;

import datatype.AbstractDataType;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.LinVisibility;

import java.util.*;

public class MinimalVisSearch {
    private SearchStatePriorityQueue priorityQueue;
    private HappenBeforeGraph happenBeforeGraph;
    private Pair<Linearization, LinVisibility> result;
    private SearchConfiguration configuration;
    private int stateExplored = 0;

    public MinimalVisSearch(SearchConfiguration configuration) {
        this.configuration = configuration;
    }

    public void init(HappenBeforeGraph happenBeforeGraph) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        this.happenBeforeGraph = happenBeforeGraph;
        SearchState startState = new SearchState();
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        for (SearchState newState : startState.linExtent()) {
            priorityQueue.offer(newState);
        }
    }

    public void init(HappenBeforeGraph happenBeforeGraph, SearchState initState) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        this.happenBeforeGraph = happenBeforeGraph;
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        priorityQueue.offer(initState);
    }

    public boolean checkConsistency() {
        AbstractDataType adt = configuration.getAdt();
        while (!priorityQueue.isEmpty()
                && (configuration.getSearchLimit() == -1 || stateExplored < configuration.getSearchLimit())
                && (configuration.getStateLimit() == -1 || priorityQueue.size() < configuration.getStateLimit())) {
            stateExplored++;
           // System.out.println(priorityQueue.size());
            SearchState state = priorityQueue.poll();
            //System.out.println(priorityQueue.toString());
            int times = 0;
            while (state.nextVisibility() != -1 && times < 10) {
                times++;
                if (executeCheck(adt, state)) {
                    if (state.isComplete()) {
                        result = new ImmutablePair<>((Linearization) state.getLinearization().clone(), (LinVisibility) state.getVisibility().clone());
                        return true;
                    }
                    List<SearchState> list =state.linExtent();
                    Collections.reverse(list);
                    for (SearchState newState : list) {
                        priorityQueue.offer(newState);
                    }
                    break;
                }
            }
        }
        return false;
    }

    public List<SearchState> getAllSearchState() {
        List<SearchState> states = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            states.add(priorityQueue.poll());
        }
        return states;
    }

    private boolean executeCheck(AbstractDataType adt, SearchState searchState) {
        String retTrace = searchState.getLinearization().getRetValueTrace(searchState.getLinearization().size());
        String excuteTrace = Validation.crdtExecute(adt, searchState.getLinearization(), searchState.getVisibility()).toString();
        //System.out.println(Thread.currentThread().getId() + ":" + Integer.toString(searchState.getLinearization().size()) + "/" + Integer.toString(happenBeforeGraph.size()));
//        System.out.println(retTrace);
//        System.out.println(excuteTrace);
//        System.out.println();
        if (excuteTrace.equals(retTrace)) {
//            System.out.println(retTrace);
//            System.out.println(excuteTrace);
//            System.out.println();
            return true;
        } else {
//            System.out.println(retTrace);
//            System.out.println(excuteTrace);
//            System.out.println();
            return false;
        }
    }

    public Pair<Linearization, LinVisibility> getResult() {
        return result;
    }
}
