package validation;

import datatype.AbstractDataType;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.LinVisibility;

import java.util.*;

public class MinimalVisSearch {
    private Stack<SearchState> stack = new Stack<>();
    private HappenBeforeGraph happenBeforeGraph;
    private Pair<Linearization, LinVisibility> result;

    public void init(HappenBeforeGraph happenBeforeGraph) {
        SearchState.happenBeforeGraph = happenBeforeGraph;
        this.happenBeforeGraph = happenBeforeGraph;
        SearchState startState = new SearchState();
        for (SearchState newState : startState.linExtent()) {
            stack.push(newState);
        }
    }

    public boolean checkConsistency(AbstractDataType adt) {
        while (!stack.isEmpty()) {
            SearchState state = stack.pop();
            int times = 0;
            while (state.nextVisibility() != -1 && times < 1) {
                times++;
                if (executeCheck(adt, state)) {
                    if (state.isComplete()) {
                        result = new ImmutablePair<>((Linearization) state.getLinearization().clone(), (LinVisibility) state.getVisibility().clone());
                        return true;
                    }
                    List<SearchState> list =state.linExtent();
                    Collections.reverse(list);
                    for (SearchState newState : list) {
                        stack.push(newState);
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean executeCheck(AbstractDataType adt, SearchState searchState) {
        String retTrace = searchState.getLinearization().getRetValueTrace(searchState.getLinearization().size());
        String excuteTrace = Validation.crdtExecute(adt, searchState.getLinearization(), searchState.getVisibility()).toString();
        System.out.println(Integer.toString(searchState.getLinearization().size()) + "/" + Integer.toString(happenBeforeGraph.size()));
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