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
    private volatile boolean exit = false;

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
        while (!priorityQueue.isEmpty() && !exit
                && (configuration.getSearchLimit() == -1 || stateExplored < configuration.getSearchLimit())
                && (configuration.getQueueLimit() == -1 || priorityQueue.size() < configuration.getQueueLimit())) {
            stateExplored++;
            SearchState state = priorityQueue.poll();
            if (configuration.isEnableIncompatibleRelation()) {
                addTempHBRelations(state.getTempHBRelations());
            }

            int times = 0;
            while (state.nextVisibility(configuration.getVisibilityType()) != -1 && !exit
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
                    priorityQueue.offer(state);
                    if (configuration.isEnableOutputSchedule() && list.size() == 0) {
                        System.out.println("empty!!!");
                        Set<HBGNode> expansion = new HashSet<>();
                        for (HBGNode node : state.getLinearization()) {
                            for (HBGNode next :happenBeforeGraph.getNexts(node)) {
                                if (!state.getLinearization().contains(next)) {
                                    expansion.add(next);
                                }
                            }
                        }
                        System.out.println(expansion.toString());
                        for (HBGNode node : expansion) {
                            boolean flag = true;
                            for (HBGNode prev : happenBeforeGraph.getPrevs(node)) {
                                if (!state.getLinearization().contains(prev)) {    //节点所有的前驱必须都已经被包含在全序里
                                    flag = false;
                                    System.out.println(node.toString() + "--fail:" + prev.toString());
                                }
                            }
                            if (flag) {
                                System.out.println("success:" + node.toString());
                            }
                        }

                        if (happenBeforeGraph.detectCircle()) {
                            System.out.println("circle detected!!!");
                            return false;
                        }
                    }
                    for (SearchState newState : list) {
                        priorityQueue.offer(newState);
                    }
                    break;
               } else {
                    handlePrickOperation(state);
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
        boolean excuteResult = Validation.crdtExecute(adt, searchState);
        if (configuration.isEnableOutputSchedule()) {
            HBGNode lastOperation = searchState.getLinearization().getLast();
            if (searchState.getLinearization().size() % 10 == 0) {
                System.out.println(lastOperation.toString() + " + " + searchState.getLinearization().size() + "/" + happenBeforeGraph.size() + "--" + searchState.getQueryOperationSize());
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
}
