package validation;

import datatype.AbstractDataType;
import datatype.MyHashMap;
import history.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import visibility.LinVisibility;

import java.util.*;

public class MinimalExtension {
    private HappenBeforeGraph happenBeforeGraph;
    private OperationTypes operationTypes;
    private Linearization linearization = new Linearization();
    private LinVisibility linVisibility = new LinVisibility();
    private Pair<Linearization, LinVisibility> result;
    private boolean find = false;

    public MinimalExtension(HappenBeforeGraph happenBeforeGraph) {
        this.happenBeforeGraph = happenBeforeGraph;
    }

    public MinimalExtension(HappenBeforeGraph happenBeforeGraph, OperationTypes operationTypes) {
        this.happenBeforeGraph = happenBeforeGraph;
        this.operationTypes = operationTypes;
    }

    public Pair<Linearization, LinVisibility> getResult() {
        return result;
    }

    public boolean checkConsistency(AbstractDataType adt) {
        //System.out.println("check");
        if (find == true) {
            return true;
        }
        if (isComplete()) {
            result = new ImmutablePair<>((Linearization) linearization.clone(), (LinVisibility) linVisibility.clone());
            find = true;
            System.out.println("find!!!");
            return true;
        }
        List<Linearization> lins = linExtensions();
        //System.out.println("Lins size: " + Integer.toString(lins.size()));
        for (Linearization lin : lins) {
            if (find) {
                break;
            }
            int linSize = lin.size();
            linearization.addAll(lin);
            Set<HBGNode> visible = getVisibleNodes();
            List<HBGNode> candidate = getCandinateNodes(visible);
            ManualRecurse manualRecurse = new ManualRecurse(candidate);
            List<HBGNode> subset = null;
            while ((subset = manualRecurse.enumerate()) != null && !find) {
                Set<HBGNode> vis = new HashSet<>(visible);
                vis.addAll(visClosure(subset));
                linVisibility.updateNodeVisibility(linearization.getLast(), vis);
                if (executeCheck(vis, adt)) {
                    manualRecurse.prune(subset);
                    //linVisibility.updateNodeVisibility(linearization.getLast(), vis);
                    checkConsistency(adt);
                }
                linVisibility.removeNodeVisibility(linearization.getLast());
            }

            for (int i = 0; i < linSize; i++) {
                linearization.removeLast();
            }
        }
        return true;
    }

    private boolean isComplete() {
        return happenBeforeGraph.size() == linearization.size();
    }

    private Set<HBGNode> getVisibleNodes() {
        //System.out.println("get Visible Nodes");
        Set<HBGNode> visible = new HashSet<>();
        HBGNode node = linearization.get(linearization.size() - 1);
        Set<HBGNode> prevs = node.getAllPrevs();
        for (HBGNode prev : prevs) {
            visible.addAll(linVisibility.getNodeVisibility(prev));
        }
        visible.addAll(prevs);
        visible.add(node);
        return visible;
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
            closure.addAll(node.getAllPrevs());
        }
        return closure;
    }

    private boolean executeCheck(Set<HBGNode> vis, AbstractDataType adt) {
        if (find) {
            return false;
        }

        String retTrace = linearization.getRetValueTrace(linearization.size());
        String excuteTrace = Validation.crdtExecute(adt, linearization, linVisibility).toString();
        System.out.println(Integer.toString(linearization.size()) + "/" + Integer.toString(happenBeforeGraph.size()));
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

    private List<Linearization> linExtensions() {
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
        return enumerateAllSeqs(new ArrayList<>(adjacencyNodes));
    }

    private static List<Linearization> enumerateAllSeqs(List<HBGNode> nodes) {
        List<Linearization> seqs = new ArrayList<>();
        for (HBGNode node : nodes) {
            Linearization linearization = new Linearization();
            linearization.add(node);
            seqs.add(linearization);
        }
        return seqs;
    }

    public static void main(String[] args) throws Exception {

    }
}
