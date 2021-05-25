package validation;

import history.HBGNode;
import history.HappenBeforeGraph;
import arbitration.Linearization;
import arbitration.LinVisibility;

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

    public int nextVisibility() {
        if (manualRecurse == null) {
            visibleNodes = getVisibleNodes();
            candidateNodes = getCandinateNodes(visibleNodes);
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
        HBGNode node = linearization.get(linearization.size() - 1);
        Set<HBGNode> prevs = node.getAllPrevs();
        for (HBGNode prev : prevs) {
            visibleNodes.addAll(visibility.getNodeVisibility(prev));
        }
        visibleNodes.addAll(prevs);
        visibleNodes.add(node);
        return visibleNodes;
    }

    private List<HBGNode> getCandinateNodes(Set<HBGNode> visible) {
        List<HBGNode> candidate = new ArrayList<>();
        for (HBGNode node1 : linearization) {
            if (!visible.contains(node1)) {
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

    public int compareTo(SearchState o) {
        if (linearization.size() > o.linearization.size()) {
            return 1;
        } else if (linearization.size() == o.linearization.size()) {
            return 0;
        } else {
            return -1;
        }
    }

    public String toString() {
        return linearization.toString();
    }

}
