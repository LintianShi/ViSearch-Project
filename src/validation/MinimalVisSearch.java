package validation;

import arbitration.VisibilityType;
import datatype.AbstractDataType;
import datatype.RRpq;
import history.HBGNode;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.LinVisibility;

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
                && (configuration.getQueueLimit() == -1 || priorityQueue.size() < configuration.getQueueLimit())) {
            stateExplored++;
           // System.out.println(priorityQueue.size());
            SearchState state = priorityQueue.poll();
            //System.out.println(priorityQueue.toString());
            int times = 0;
            while (state.nextVisibility(configuration.getVisibilityType()) != -1
                    && (times < configuration.getVisibilityLimit()
                        || configuration.getVisibilityLimit() == -1
                        || (configuration.getVisibilityLimit() == 0 && times < state.size()))) {
                times++;
                if (executeCheck(adt, state)) {
                    if (state.isComplete()) {
                        //result = new ImmutablePair<>((Linearization) state.getLinearization().clone(), (LinVisibility) state.getVisibility().clone());
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
            System.out.println(lastOperation.toString() + " + " + searchState.getLinearization().size() + "/" + happenBeforeGraph.size());
        }

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

    public List<SearchState> getResults() {
        return results;
    }
}
