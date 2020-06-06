package validation;

import datatype.AbstractDataType;
import datatype.MyHashMap;
import trace.*;
import visibility.LinVisibility;

import java.util.*;

public class MinimalExtension {
    private HappenBeforeGraph happenBeforeGraph;
    private Linearization linearization = new Linearization();
    private LinVisibility linVisibility = new LinVisibility();

    public MinimalExtension(HappenBeforeGraph happenBeforeGraph) {
        this.happenBeforeGraph = happenBeforeGraph;
    }

    public boolean checkConsistency(HashMap<String, Set<String>> results, AbstractDataType adt) {
        if (isComplete()) {
            String lin = linearization.toString();
            String vis = linVisibility.toString();
            Set<String> set = results.get(lin);
            if (set != null) {
                set.add(vis);
            } else {
                set = new HashSet<>();
                set.add(vis);
                results.put(lin, set);
            }
            return true;
        }
        List<Linearization> lins = linExtensions();
        for (Linearization lin : lins) {
            int linSize = lin.size();
            linearization.addAll(lin);

            List<Set<HBGNode>> visibilities = visExtensions(adt);
            for (Set<HBGNode> vis : visibilities) {
                linVisibility.updateNodeVisibility(linearization.getLast(), vis);
                checkConsistency(results, adt);
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

    private List<Set<HBGNode>> visExtensions(AbstractDataType adt) {
        Set<HBGNode> visible = new HashSet<>();
        HBGNode node = linearization.get(linearization.size() - 1);
        Set<HBGNode> prevs = node.getAllPrevs();
//        for (HBGNode prev : prevs) {
//            visible.addAll(linVisibility.getNodeVisibility(prev));
//        }
        visible.addAll(prevs);
        visible.add(node);

        List<HBGNode> candidate = new ArrayList<>();
        for (HBGNode node1 : linearization) {
            if (!visible.contains(node1)) {
                candidate.add(node1);
            }
        }

        return generateMinimalVis(visible, candidate, adt);
    }

    private List<Set<HBGNode>> generateMinimalVis(Set<HBGNode> base, List<HBGNode> ext, AbstractDataType adt) {
        List<Set<HBGNode>> results = new ArrayList<>();
        String retTrace = linearization.getRetValueTrace(linearization.size());
        HBGNode node = linearization.getLast();
        SubsetNode head = generateAllSubsets(ext);
        traverse(base, head, adt, retTrace, results);
        linVisibility.removeNodeVisibility(node);
        return results;
    }

    private void traverse(Set<HBGNode> base, SubsetNode node, AbstractDataType adt, String retTrace, List<Set<HBGNode>> results) {
        if (!node.valid) {
            return;
        }
        Set<HBGNode> vis = new HashSet<>(base);
//        for (HBGNode subnode : node.getSubset()) {
//            vis.addAll(linVisibility.getNodeVisibility(subnode));
//        }
        vis.addAll(node.getSubset());
        linVisibility.updateNodeVisibility(linearization.getLast(), vis);
        String excuteTrace = Validation.execute(adt, linearization, linVisibility).toString();
//        System.out.println(retTrace);
//        System.out.println(excuteTrace);
//        System.out.println();
        if (excuteTrace.equals(retTrace)) {
            results.add(vis);
            for (SubsetNode next : node.getNexts()) {
                invalidate(next);
            }
        } else {
            for (SubsetNode next : node.getNexts()) {
                traverse(base, next, adt, retTrace, results);
            }
        }
    }

    private static void invalidate(SubsetNode node) {
        node.valid = false;
        for (SubsetNode next : node.getNexts()) {
            invalidate(next);
        }
    }

    private static SubsetNode generateAllSubsets(List<HBGNode> candidate) {
        Stack<HBGNode> stack = new Stack<>();
        List<Set<HBGNode>> results = new ArrayList<>();
        generateAllSubsets(candidate, 0, stack, results);
        SubsetNode head = null;
        List<SubsetNode> subsets = new ArrayList<>(results.size());
        for (Set<HBGNode> result : results) {
            subsets.add(new SubsetNode(result));
        }

        for (int i = 0; i < subsets.size(); i++) {
            if (subsets.get(i).getSubset().size() == 0) {
                head = subsets.get(i);
            }
            for (int j = 0; j < subsets.size(); j++) {
                if (i != j && subsets.get(i).getSubset().size() + 1 == subsets.get(j).getSubset().size()
                        && subsets.get(j).getSubset().containsAll(subsets.get(i).getSubset())) {
                    subsets.get(i).addNext(subsets.get(j));
                }
            }
        }
        return head;
    }

    private static void generateAllSubsets(List<HBGNode> candidate, int index, Stack<HBGNode> stack, List<Set<HBGNode>> results) {
        if (candidate.size() == index) {
            Set<HBGNode> temp = new HashSet<>(stack);
            results.add(temp);
            return;
        }
        stack.push(candidate.get(index));
        generateAllSubsets(candidate, index + 1, stack, results);
        stack.pop();
        generateAllSubsets(candidate, index + 1, stack, results);
    }


    private List<Linearization> linExtensions() {
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
                boolean flag = true;
                for (HBGNode prev : node.getPrevs()) {
                    if (!linearization.contains(prev)) {
                        flag = false;
                        break;
                    }
                }
                if (flag && !linearization.contains(node)) {
                    adjacencyNodes.add(node);
                }
            }
            for (HBGNode node : happenBeforeGraph) {
                if (node.getPrevs().isEmpty() && !linearization.contains(node)) {
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

    private static void enumerateAllSeqs(List<HBGNode> nodes, Stack<HBGNode> stack, List<Linearization> seqs, List<Integer> visited) {
        if (!stack.isEmpty()) {
            seqs.add(new Linearization(stack));
            if (stack.size() == nodes.size()) {
                return;
            }
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (visited.get(i) == 0) {
                stack.push(nodes.get(i));
                visited.set(i, 1);
                enumerateAllSeqs(nodes, stack, seqs, visited);
                visited.set(i, 0);
                stack.pop();
            }
        }
    }

    static class SubsetNode {
        private Set<HBGNode> subset;
        private List<SubsetNode> nexts = new ArrayList();
        public boolean valid = true;
        public SubsetNode(Set<HBGNode> subset) {
            this.subset = subset;
        }

        public List<SubsetNode> getNexts() {
            return nexts;
        }

        public Set<HBGNode> getSubset() {
            return subset;
        }

        public void addNext(SubsetNode next) {
            nexts.add(next);
        }

        @Override
        public String toString() {
            if (nexts.size() != 0) {
                return subset.toString() + ":\n" + nexts.toString();
            } else {
                return subset.toString() + "end";
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<HBGNode> list = new ArrayList<>();
        list.add(new HBGNode(new Invocation(), 1));
        list.add(new HBGNode(new Invocation(), 2));
        list.add(new HBGNode(new Invocation(), 3));
//        List<Linearization> results = enumerateAllSeqs(list);
//        for (Linearization linearization : results) {
//            System.out.println(linearization);
//        }
        HappenBeforeGraph happenBeforeGraph = Program.load("minimal-1.json");
        HashMap<String, Set<String>> results = new HashMap<>();
        new MinimalExtension(happenBeforeGraph).checkConsistency(results, new MyHashMap());
        System.out.println(results.size());
        for (Map.Entry<String, Set<String>> entry: results.entrySet()) {
            System.out.println(entry.getKey().toString());
            for (String vis : entry.getValue()) {
                System.out.println(vis);
            }
            System.out.println();
        }

        //System.out.println(happenBeforeGraph.generateLins().size());

//        for (HBGNode node : happenBeforeGraph) {
//            System.out.println(node.getAllPrevs());
//        }

        //SubsetNode head = generateAllSubsets(list);

        //System.out.println(head.toString());
    }
}
