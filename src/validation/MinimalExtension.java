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

                if (executeCheck(vis, adt)) {
                    manualRecurse.prune(subset);
                    linVisibility.updateNodeVisibility(linearization.getLast(), vis);
                    checkConsistency(adt);
                    linVisibility.removeNodeVisibility(linearization.getLast());
                }
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
        linVisibility.updateNodeVisibility(linearization.getLast(), vis);
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

//    private List<Set<HBGNode>> visExtensions(AbstractDataType adt) {
//        System.out.println("vis extension");
//        Set<HBGNode> visible = new HashSet<>();
//        HBGNode node = linearization.get(linearization.size() - 1);
//        Set<HBGNode> prevs = node.getAllPrevs();
//        for (HBGNode prev : prevs) {
//            visible.addAll(linVisibility.getNodeVisibility(prev));
//        }
//        visible.addAll(prevs);
//        visible.add(node);
//
//        List<HBGNode> candidate = new ArrayList<>();
//        for (HBGNode node1 : linearization) {
//            if (!visible.contains(node1)) {
//                candidate.add(node1);
//            }
//        }
//
//        //candidate.clear();
//        return generateMinimalVis(visible, candidate, adt);
//    }

//    private List<Set<HBGNode>> generateMinimalVis(Set<HBGNode> base, List<HBGNode> ext, AbstractDataType adt) {
//        //System.out.println("minimal vis");
//        List<Set<HBGNode>> results = new ArrayList<>();
//        String retTrace = linearization.getRetValueTrace(linearization.size());
//        HBGNode node = linearization.getLast();
//        SubsetNode head = generateAllSubsets(ext);
//        BFTraverse(base, head, adt, retTrace, results);
//        System.out.println("BFT over");
//        linVisibility.removeNodeVisibility(node);
//
//        return results;
//    }

//    private void BFTraverse(Set<HBGNode> base, SubsetNode node, AbstractDataType adt, String retTrace, List<Set<HBGNode>> results) {
//        System.out.println("BFT");
//        Queue<SubsetNode> queue = new ArrayDeque<>();
//        queue.offer(node);
//        while (!queue.isEmpty()) {
//            System.out.println(queue.size());
//            SubsetNode subsetNode = queue.poll();
//            subsetNode.visited = true;
//            if (!subsetNode.valid) {
//                continue;
//            }
//            Set<HBGNode> vis = new HashSet<>(base);
//            vis.addAll(subsetNode.getSubset());
//            linVisibility.updateNodeVisibility(linearization.getLast(), vis);
//            String excuteTrace = Validation.crdtExecute(adt, linearization, linVisibility).toString();
////            if (linearization.getLast().getInvocation().getMethodName().equals("rwfzmax")) {
////                System.out.println(linVisibility.getNodeVisibility(linearization.getLast()));
////                System.out.println(linearization.getLast().getAllPrevs());
////            }
//            System.out.println(retTrace);
//            System.out.println(excuteTrace);
//            System.out.println();
//            if (excuteTrace.equals(retTrace)) {
//                results.add(vis);
//                for (SubsetNode next : subsetNode.getNexts()) {
//                    invalidate(next);
//                }
//            } else {
//                for (SubsetNode next : subsetNode.getNexts()) {
//                    if (!next.visited) {
//                        queue.offer(next);
//                    }
//                }
//            }
//        }
//    }
//
//    private void traverse(Set<HBGNode> base, SubsetNode node, AbstractDataType adt, String retTrace, List<Set<HBGNode>> results) {
//        if (!node.valid) {
//            return;
//        }
//        Set<HBGNode> vis = new HashSet<>(base);
////        for (HBGNode subnode : node.getSubset()) {
////            vis.addAll(linVisibility.getNodeVisibility(subnode));
////        }
//        vis.addAll(node.getSubset());
//        linVisibility.updateNodeVisibility(linearization.getLast(), vis);
//        String excuteTrace = Validation.execute(adt, linearization, linVisibility).toString();
//        System.out.println(retTrace);
//        System.out.println(excuteTrace);
//        System.out.println();
//        if (excuteTrace.equals(retTrace)) {
//            results.add(vis);
//            for (SubsetNode next : node.getNexts()) {
//                invalidate(next);
//            }
//        } else {
//            for (SubsetNode next : node.getNexts()) {
//                traverse(base, next, adt, retTrace, results);
//            }
//        }
//    }

//    private static void invalidate(SubsetNode node) {
//        node.valid = false;
//        for (SubsetNode next : node.getNexts()) {
//            if (next.valid == true)
//                invalidate(next);
//        }
//    }
//
//    private static SubsetNode generateAllSubsets(List<HBGNode> candidate) {
//        //System.out.println("generateAllSubsets 1");
//        Stack<HBGNode> stack = new Stack<>();
//        List<Set<HBGNode>> results = new ArrayList<>();
//        generateAllSubsets(candidate, 0, stack, results);
//        System.out.println("ok");
//        SubsetNode head = null;
//        List<SubsetNode> subsets = new ArrayList<>(results.size());
//        for (Set<HBGNode> result : results) {
//            subsets.add(new SubsetNode(result));
//        }
//
//        System.out.println("size:" + Integer.toString(subsets.size()));
//        for (int i = 0; i < subsets.size(); i++) {
//            if (subsets.get(i).getSubset().size() == 0) {
//                head = subsets.get(i);
//            }
//            for (int j = 0; j < subsets.size(); j++) {
//                if (i != j && subsets.get(i).getSubset().size() + 1 == subsets.get(j).getSubset().size()
//                        && subsets.get(j).getSubset().containsAll(subsets.get(i).getSubset())) {
//                    subsets.get(i).addNext(subsets.get(j));
//                }
//            }
//        }
//        System.out.println("ok1");
//        return head;
//    }
//
//    private static void generateAllSubsets(List<HBGNode> candidate, int index, Stack<HBGNode> stack, List<Set<HBGNode>> results) {
//        //System.out.println("generateAllSubsets: " + Integer.toString(index) + ": " + Integer.toString(candidate.size()));
//        if (candidate.size() == index) {
//            Set<HBGNode> temp = new HashSet<>(stack);
//            results.add(temp);
//            return;
//        }
//        stack.push(candidate.get(index));
//        generateAllSubsets(candidate, index + 1, stack, results);
//        stack.pop();
//        generateAllSubsets(candidate, index + 1, stack, results);
//    }


    private List<Linearization> linExtensions() {
        //System.out.println("lin extension");
        Set<HBGNode> adjacencyNodes = new HashSet<>();
        if (linearization.size() == 0) {
            for (HBGNode node : happenBeforeGraph) {
                if (node.getPrevs().isEmpty()) {
                    adjacencyNodes.add(node);
                }
            }
        } else {
            Set<HBGNode> temp = new HashSet<>();
            for (HBGNode node : linearization) {
                temp.addAll(node.getNexts());
            }
            for (HBGNode node : temp) {
                if (linearization.contains(node)) {
                    continue;
                }
                //System.out.println(node.getInvocation().getRetValue());
                //System.out.println(node.getPrevs());
                boolean flag = true;
                for (HBGNode prev : node.getPrevs()) {
                    if (!linearization.contains(prev)) {    //节点所有的前驱必须都已经被包含在全序里
                        flag = false;
                        break;
                    }
                }
                //System.out.println(flag);
                if (flag) {
                    adjacencyNodes.add(node);
                }
            }
            for (HBGNode node : happenBeforeGraph) {
                if (node.getPrevs().isEmpty() && !linearization.contains(node)) {   //没有前驱的节点
                    adjacencyNodes.add(node);
                }
            }
        }

        return enumerateAllSeqs(new ArrayList<>(adjacencyNodes));
    }

    private static List<Linearization> enumerateAllSeqs(List<HBGNode> nodes) {
//        Stack<HBGNode> stack = new Stack<>();
//        List<Linearization> seqs = new ArrayList<>();
//        List<Integer> visited = new ArrayList<>(nodes.size());
//        for (int i = 0; i < nodes.size(); i++) {
//            visited.add(0);
//        }
//
//        enumerateAllSeqs(nodes, stack, seqs,  visited);
//        return seqs;
        List<Linearization> seqs = new ArrayList<>();
        for (HBGNode node : nodes) {
            Linearization linearization = new Linearization();
            linearization.add(node);
            seqs.add(linearization);
        }
        return seqs;
    }

//    private static void enumerateAllSeqs(List<HBGNode> nodes, Stack<HBGNode> stack, List<Linearization> seqs, List<Integer> visited) {
//        if (!stack.isEmpty()) {
//            seqs.add(new Linearization(stack));
//            if (stack.size() == nodes.size()) {
//                return;
//            }
//        }
//        for (int i = 0; i < nodes.size(); i++) {
//            if (visited.get(i) == 0) {
//                stack.push(nodes.get(i));
//                visited.set(i, 1);
//                enumerateAllSeqs(nodes, stack, seqs, visited);
//                visited.set(i, 0);
//                stack.pop();
//            }
//        }
//    }
//
//    static class SubsetNode {
//        private Set<HBGNode> subset;
//        private List<SubsetNode> nexts = new ArrayList();
//        public boolean valid = true;
//        public boolean visited = false;
//        public SubsetNode(Set<HBGNode> subset) {
//            this.subset = subset;
//        }
//
//        public List<SubsetNode> getNexts() {
//            return nexts;
//        }
//
//        public Set<HBGNode> getSubset() {
//            return subset;
//        }
//
//        public void addNext(SubsetNode next) {
//            nexts.add(next);
//        }
//
//        @Override
//        public String toString() {
//            if (nexts.size() != 0) {
//                return subset.toString() + ":\n" + nexts.toString();
//            } else {
//                return subset.toString() + "end";
//            }
//        }
//    }

    public static void main(String[] args) throws Exception {
        List<HBGNode> list = new ArrayList<>();
        list.add(new HBGNode(new Invocation(), 1));
        list.add(new HBGNode(new Invocation(), 2));
        list.add(new HBGNode(new Invocation(), 3));
//        List<Linearization> results = enumerateAllSeqs(list);
//        for (Linearization linearization : results) {
//            System.out.println(linearization);
//        }
//        HappenBeforeGraph happenBeforeGraph = Program.load("minimal-1.json");
//        HashMap<String, Set<String>> results = new HashMap<>();
//        new MinimalExtension(happenBeforeGraph).checkConsistency(results, new MyHashMap());
//        System.out.println(results.size());
//        for (Map.Entry<String, Set<String>> entry: results.entrySet()) {
//            System.out.println(entry.getKey().toString());
//            for (String vis : entry.getValue()) {
//                System.out.println(vis);
//            }
//            System.out.println();
//        }

        //System.out.println(happenBeforeGraph.generateLins().size());

//        for (HBGNode node : happenBeforeGraph) {
//            System.out.println(node.getAllPrevs());
//        }

        //SubsetNode head = generateAllSubsets(list);

        //System.out.println(head.toString());
    }
}
