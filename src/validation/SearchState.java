package validation;

import arbitration.VisibilityType;
import history.HBGNode;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import arbitration.LinVisibility;
import org.apache.commons.lang3.tuple.ImmutablePair;
import util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchState implements Serializable, Comparable<SearchState> {
    public static transient HappenBeforeGraph happenBeforeGraph;
    private Linearization linearization;
    private LinVisibility visibility;
    private transient ManualRecurse manualRecurse = null;
    private transient Set<HBGNode> visibleNodes = null;
    private transient List<HBGNode> candidateNodes = null;
    private transient int adtState = 0;
    private transient VisibilityType visibilityType;
    private transient List<ImmutablePair<Integer, Integer>> tempHBRelations = new ArrayList<>();

    public SearchState() {
        this.linearization = new Linearization();
        this.visibility = new LinVisibility();
    }

    public SearchState(Linearization linearization) {
        this.linearization = linearization;
        this.visibility = new LinVisibility();
    }

    public SearchState(Linearization linearization,LinVisibility visibility) {
        this.linearization = linearization;
        this.visibility = visibility;
    }

    public boolean isComplete() {
        return happenBeforeGraph.size() == linearization.size() && happenBeforeGraph.size() == visibility.size();
    }

    public List<SearchState> linExtent() {
        Set<HBGNode> adjacencyNodes;
        if (linearization.size() == 0) {
            adjacencyNodes = happenBeforeGraph.getNodesWithoutPrev();
        } else {
            adjacencyNodes = linearization.getAdjacencyNodes(happenBeforeGraph);
            for (HBGNode node : happenBeforeGraph.getNodesWithoutPrev()) {
                if (!linearization.contains(node)) {   //没有前驱的节点
                    adjacencyNodes.add(node);
                }
            }
        }

        List<Linearization> newLins = linearization.extendLin(adjacencyNodes);

        List<List<ImmutablePair<Integer, Integer>>> tempHBRelations = new ArrayList<>();
        if (happenBeforeGraph.isRuleTableExist()) {
            for (Linearization lin :newLins) {
                HBGNode lastNode = lin.getLast();
                List<ImmutablePair<Integer, Integer>> tempList = new ArrayList<>(this.tempHBRelations);
                for (HBGNode node : lin) {
                    ImmutablePair<Integer, Integer> pair = new ImmutablePair<>(node.getId(), lastNode.getId());
                    if (happenBeforeGraph.getIncompatibleRelations(pair) != null) {
                        tempList.addAll(happenBeforeGraph.getIncompatibleRelations(pair));
                    }
                    tempHBRelations.add(new ArrayList<>());
                }
            }
        }


        List<SearchState> newStates = new ArrayList<>();
        for (int i = 0; i < newLins.size(); i++) {
            SearchState newState = new SearchState(newLins.get(i), (LinVisibility) visibility.clone());
            if (happenBeforeGraph.isRuleTableExist()) {
                for (ImmutablePair<Integer, Integer> pair : tempHBRelations.get(i)) {
                    newState.addTempHBRelation(pair);
                }
            }
            newStates.add(newState);
        }

        return newStates;
    }

    public int nextVisibility(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
        if (manualRecurse == null) {
            visibleNodes = getVisibleNodes();
            candidateNodes = getCandinateNodes();
            this.manualRecurse = new ManualRecurse(candidateNodes);
        }
        List<HBGNode> subset = null;
        if ((subset = manualRecurse.enumerate()) != null) {
            Set<HBGNode> vis = new HashSet<>(visibleNodes);
            vis.addAll(visClosure(subset));
            visibility.updateNodeVisibility(linearization.getLast(), vis);
            return 0;
        }
        return -1;
    }

    private Set<HBGNode> getVisibleNodes() {
        Set<HBGNode> visibleNodes = new HashSet<>();
        if (visibilityType == VisibilityType.CAUSAL) {
            HBGNode node = linearization.get(linearization.size() - 1);
            Set<HBGNode> prevs = happenBeforeGraph.getAllPrevs(node);
            for (HBGNode prev : prevs) {
                visibleNodes.addAll(visibility.getNodeVisibility(prev));
            }
            visibleNodes.addAll(prevs);
            visibleNodes.add(node);
        } else if (visibilityType == VisibilityType.COMPLETE) {
            for (int i = 0; i < linearization.size(); i++) {
                visibleNodes.add(linearization.get(i));
            }
            //System.out.println("complete");
        } else if (visibilityType == VisibilityType.BASIC) {
            HBGNode node = linearization.get(linearization.size() - 1);
            Set<HBGNode> prevs = happenBeforeGraph.getAllPrevs(node);
            visibleNodes.addAll(prevs);
            visibleNodes.add(node);
        } else if (visibilityType == VisibilityType.WEAK) {
            HBGNode node = linearization.get(linearization.size() - 1);
            visibleNodes.add(node);
        }

        return visibleNodes;
    }

    private List<HBGNode> getCandinateNodes() {
        List<HBGNode> candidate = new ArrayList<>();
        for (HBGNode node1 : linearization) {
            if (!visibleNodes.contains(node1)) {
                candidate.add(node1);
            }
        }
        return candidate;
    }

    private Set<HBGNode> visClosure(List<HBGNode> vis) {
        Set<HBGNode> closure = new HashSet<>(vis);
        for (HBGNode node : vis) {
            closure.addAll(visibility.getNodeVisibility(node));
        }
        return closure;
    }

    public Linearization getLinearization() {
        return linearization;
    }

    public LinVisibility getVisibility() {
        return visibility;
    }

    public int getAdtState() {
        return adtState;
    }

    public void setAdtState(int adtState) {
        this.adtState = adtState;
    }

    public List<ImmutablePair<Integer, Integer>> extractHBRelation() {
        List<ImmutablePair<Integer, Integer>> hbs = new ArrayList<>();
        for (int i = 1; i < linearization.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (linearization.get(j).getThreadId() != linearization.get(i).getThreadId()) {
                    hbs.add(new ImmutablePair<Integer, Integer>(linearization.get(j).getId(), linearization.get(i).getId()));
                }
            }
        }
        return hbs;
    }

    public void addTempHBRelation(ImmutablePair<Integer, Integer> pair) {
        tempHBRelations.add(pair);
    }

    public List<ImmutablePair<Integer, Integer>> getTempHBRelations() {
        return tempHBRelations;
    }

    public int compareTo(SearchState o) {
        if (linearization.size() > o.linearization.size()) {
            return 1;
        } else if (linearization.size() == o.linearization.size()) {
            return 0;
        } else {
            return -1;
        }
    }

    public int size() {
        return linearization.size();
    }

    public String toString() {
        return Integer.toString(adtState) + ":" + linearization.toString() + " | " + visibility.toString();
    }

}
