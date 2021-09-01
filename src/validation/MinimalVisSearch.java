package validation;

import datatype.AbstractDataType;
import history.HBGNode;
import history.HappenBeforeGraph;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class MinimalVisSearch {
    private SearchStatePriorityQueue priorityQueue;
    private HappenBeforeGraph happenBeforeGraph;
    private SearchConfiguration configuration;
    private int stateExplored = 0;
    private HashMap<HBGNode, Integer> prickOperationCounter = new HashMap<>();
    private int readOperationFailLimit = 30;
    private List<SearchState> results = new ArrayList<>();

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

    private void addTempHBRelations(Collection<ImmutablePair<Integer, Integer>> tempRelations) {
        for (ImmutablePair<Integer, Integer> pair : tempRelations) {
            happenBeforeGraph.addHBRelation(pair.right, pair.left);
        }
    }

    private void removeTempHBRelations(Collection<ImmutablePair<Integer, Integer>> tempRelations) {
        for (ImmutablePair<Integer, Integer> pair : tempRelations) {
            happenBeforeGraph.removeHBRelation(pair.right, pair.left);
        }
    }

    public void init(HappenBeforeGraph happenBeforeGraph, SearchState initState) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        this.happenBeforeGraph = happenBeforeGraph;
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        priorityQueue.offer(initState);
    }

    public void init(HappenBeforeGraph happenBeforeGraph, List<SearchState> initStates) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        this.happenBeforeGraph = happenBeforeGraph;
        priorityQueue = new SearchStatePriorityQueue(configuration.getSearchMode());
        for (SearchState state : initStates)
        priorityQueue.offer(state);
    }

    public boolean checkConsistency() {
        AbstractDataType adt = configuration.getAdt();
        while (!priorityQueue.isEmpty()
                && (configuration.getSearchLimit() == -1 || stateExplored < configuration.getSearchLimit())
                && (configuration.getQueueLimit() == -1 || priorityQueue.size() < configuration.getQueueLimit())) {
            stateExplored++;

            SearchState state = priorityQueue.poll();
            if (configuration.isEnableIncompatibleRelation()) {
                addTempHBRelations(state.getTempHBRelations());
            }

            int times = 0;
            while (state.nextVisibility(configuration.getVisibilityType()) != -1
                    && (times < configuration.getVisibilityLimit()
                        || configuration.getVisibilityLimit() == -1
                        || (configuration.getVisibilityLimit() == 0 && times < state.size()))) {
                times++;
                if (executeCheck(adt, state)) {
                    if (state.isComplete()) {
                        results.add(state);
                        if (!configuration.isFindAllAbstractExecution()) {
                            return true;
                        }
                    }
                    List<SearchState> list =state.linExtent();
                    Collections.reverse(list);
                    for (SearchState newState : list) {
                        priorityQueue.offer(newState);
                    }
                    break;
               } else {
                    if (!configuration.isEnablePrickOperation()) {
                        continue;
                    }
                    HBGNode prickOperation = state.getLinearization().getLast();
                    if (!prickOperationCounter.containsKey(prickOperation)) {
                        prickOperationCounter.put(prickOperation, 1);
                    } else {
                        Integer failTimes = prickOperationCounter.get(prickOperation);
                        if (failTimes == -1) {
                            continue;
                        }
                        prickOperationCounter.put(prickOperation, failTimes + 1);
                        if (failTimes > readOperationFailLimit) {
                            System.out.println(state.getLinearization().size() + ": " + "FAIL" + ":" + Integer.toString(failTimes) + " " + prickOperation);
                            prickOperationCounter.put(prickOperation, -1);
                        }
                    }
                }
            }
            if (configuration.isEnableIncompatibleRelation()) {
                removeTempHBRelations(state.getTempHBRelations());
            }
        }
        return false;
    }

    public List<SearchState> getAllSearchState() {
        List<SearchState> states = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            int times = 0;
            while (priorityQueue.peek().nextVisibility(configuration.getVisibilityType()) != -1 && times < 10) {
                times++;
                if (executeCheck(configuration.getAdt(), priorityQueue.peek())) {
                    states.add(priorityQueue.poll());
                    break;
                }
            }

        }
        return states;
    }

    private boolean executeCheck(AbstractDataType adt, SearchState searchState) {
        String retTrace = searchState.getLinearization().getRetValueTrace(searchState.getLinearization().size());
        String excuteTrace = Validation.crdtExecute(adt, searchState).toString();
        if (configuration.isEnableOutputSchedule()) {
            HBGNode lastOperation = searchState.getLinearization().getLast();
            if (searchState.getLinearization().size() % 10 == 0) {
                System.out.println(lastOperation.toString() + " + " + searchState.getLinearization().size() + "/" + happenBeforeGraph.size());
            }
        }
        if (excuteTrace.equals(retTrace)) {
            return true;
        } else {
            return false;
        }
    }

    public List<SearchState> getResults() {
        return results;
    }
}
