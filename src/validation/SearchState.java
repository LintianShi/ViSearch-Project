package validation;

import arbitration.VisibilityType;
import history.HBGNode;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import arbitration.LinVisibility;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchState implements Comparable<SearchState> {
    public static HappenBeforeGraph happenBeforeGraph;
    private Linearization linearization;
    private LinVisibility visibility;
    private ManualRecurse manualRecurse = null;
    private Set<HBGNode> visibleNodes = null;
    private List<HBGNode> candidateNodes = null;
    private int adtState = 0;
    private VisibilityType visibilityType;

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
            adjacencyNodes = linearization.getAdjacencyNodes();
            for (HBGNode node : happenBeforeGraph.getNodesWithoutPrev()) {
                if (!linearization.contains(node)) {   //没有前驱的节点
                    adjacencyNodes.add(node);
                }
            }
        }

        List<Linearization> newLins = linearization.extendLin(adjacencyNodes);
        List<SearchState> newStates = new ArrayList<>();
        for (Linearization lin : newLins) {
            newStates.add(new SearchState(lin, (LinVisibility) visibility.clone()));
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
            Set<HBGNode> prevs = node.getAllPrevs();
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
            Set<HBGNode> prevs = node.getAllPrevs();
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
                hbs.add(new ImmutablePair<>(linearization.get(j).getId(), linearization.get(i).getId()));
            }
        }
        return hbs;
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
