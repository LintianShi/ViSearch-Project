package validation;

import datatype.AbstractDataType;
import history.HBGNode;
import history.HappenBeforeGraph;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class MinimalVisSearch {
    private SearchStatePriorityQueue priorityQueue;
    private static HappenBeforeGraph happenBeforeGraph;
    private SearchConfiguration configuration;
    private int stateExplored = 0;
    private HashMap<HBGNode, Integer> prickOperationCounter = new HashMap<>();
    private int readOperationFailLimit = 30;
    private List<SearchState> results = new ArrayList<>();
    private volatile boolean exit = false;

    public MinimalVisSearch(SearchConfiguration configuration) {
        this.configuration = configuration;
        SearchState.visibilityType = configuration.getVisibilityType();
    }

    public void init(HappenBeforeGraph happenBeforeGraph) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        MinimalVisSearch.happenBeforeGraph = happenBeforeGraph;
        SearchState startState = new SearchState();
        startState.getLinearization().addFront(happenBeforeGraph.getStartNodes());
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        for (SearchState newState : startState.linExtent()) {
            priorityQueue.offer(newState);
        }
    }

//    private void addTempHBRelations(Collection<ImmutablePair<Integer, Integer>> tempRelations) {
//        for (ImmutablePair<Integer, Integer> pair : tempRelations) {
//            happenBeforeGraph.addHBRelation(pair.right, pair.left);
//        }
//    }
//
//    private void removeTempHBRelations(Collection<ImmutablePair<Integer, Integer>> tempRelations) {
//        for (ImmutablePair<Integer, Integer> pair : tempRelations) {
//            happenBeforeGraph.removeHBRelation(pair.right, pair.left);
//        }
//    }

    public void init(HappenBeforeGraph happenBeforeGraph, SearchState initState) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        MinimalVisSearch.happenBeforeGraph = happenBeforeGraph;
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        priorityQueue.offer(initState);
    }

    public void init(HappenBeforeGraph happenBeforeGraph, List<SearchState> initStates) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        MinimalVisSearch.happenBeforeGraph = happenBeforeGraph;
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        for (SearchState state : initStates)
        priorityQueue.offer(state);
    }

    public boolean checkConsistency() {
        AbstractDataType adt = configuration.getAdt();
        while (!priorityQueue.isEmpty() && !exit
                && (configuration.getQueueLimit() == -1 || priorityQueue.size() < configuration.getQueueLimit())) {

            SearchState state = priorityQueue.poll();
//            if (configuration.isEnableIncompatibleRelation()) {
//                addTempHBRelations(state.getTempHBRelations());
//            }

            List<HBGNode> subset = null;
            while ((subset = state.nextVisibility()) != null && !exit) {
                stateExplored++;
                if (executeCheck(adt, state)) {
                    if (state.isComplete()) {
//                        System.out.println(stateExplored);
//                        System.out.println(state.toString());
                        results.add(state);
                        if (!configuration.isFindAllAbstractExecution()) {
                            return true;
                        }
                    }
                    state.pruneVisibility(subset);
                    List<SearchState> list =state.linExtent();
                    priorityQueue.offer(state);
                    for (SearchState newState : list) {
                        priorityQueue.offer(newState);
                    }
                    break;
               } else {
                    handlePrickOperation(state);
               }
            }
//            if (configuration.isEnableIncompatibleRelation()) {
//                removeTempHBRelations(state.getTempHBRelations());
//            }
        }
        System.out.println(stateExplored);
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
        boolean excuteResult = Validation.crdtExecute(adt, searchState);
        if (configuration.isEnableOutputSchedule()) {
            HBGNode lastOperation = searchState.getLinearization().getLast();
            if (searchState.getLinearization().size() % 10 == 0) {
                System.out.println(Thread.currentThread().getName() + ":" + lastOperation.toString() + " + " + searchState.getLinearization().size() + "/" + happenBeforeGraph.size() + "--" + searchState.getQueryOperationSize());
            }
        }
        return excuteResult;
    }

    private void handlePrickOperation(SearchState state) {
        if (!configuration.isEnablePrickOperation()) {
            return;
        }
        HBGNode prickOperation = state.getLinearization().getLast();
        if (!prickOperationCounter.containsKey(prickOperation)) {
            prickOperationCounter.put(prickOperation, 1);
        } else {
            Integer failTimes = prickOperationCounter.get(prickOperation);
            if (failTimes == -1) {
                prickOperationCounter.remove(prickOperation);
                return;
            }
            prickOperationCounter.put(prickOperation, failTimes + 1);
            if (failTimes > readOperationFailLimit) {
                System.out.println(state.getLinearization().size() + ": " + "FAIL" + ":" + Integer.toString(failTimes) + " " + prickOperation);
                prickOperationCounter.put(prickOperation, -1);
            }
        }
    }

    public List<SearchState> getResults() {
        return results;
    }

    public void stopSearch() {
        exit = true;
    }

    public boolean isExit() {
        return exit;
    }

    public int getStateExplored() {
        return stateExplored;
    }
}
