package validation;

import trace.*;
import visibility.LinVisibility;

import java.util.*;

public class MinimalExtension {
    public static boolean checkConsistency(HappenBeforeGraph happenBeforeGraph, Linearization linearization, LinVisibility linVisibility, Set<String> results) {
        if (isComplete(happenBeforeGraph, linearization)) {
            results.add(linearization.toString());
            return true;
        }
        List<Linearization> lins = linExtensions(happenBeforeGraph, linearization);
        for (Linearization lin : lins) {
            int linSize = lin.size();
            linearization.addAll(lin);

            //visExtension
            checkConsistency(happenBeforeGraph, linearization, linVisibility, results);

            for (int i = 0; i < linSize; i++) {
                linearization.removeLast();
            }
        }
        return true;
    }

    private static boolean isComplete(HappenBeforeGraph happenBeforeGraph, Linearization linearization) {
        return happenBeforeGraph.size() == linearization.size();
    }

    private static void visExtensions(HappenBeforeGraph happenBeforeGraph, Linearization linearization, LinVisibility linVisibility) {

    }

    private static List<Linearization> linExtensions(HappenBeforeGraph happenBeforeGraph, Linearization linearization) {
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
        Stack<HBGNode> stack = new Stack<>();
        List<Linearization> seqs = new ArrayList<>();
        List<Integer> visited = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            visited.add(0);
        }

        enumerateAllSeqs(nodes, stack, seqs,  visited);
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

    public static void main(String[] args) throws Exception {
//        List<HBGNode> list = new ArrayList<>();
//        list.add(new HBGNode(new Invocation(), 1));
//        list.add(new HBGNode(new Invocation(), 2));
//        list.add(new HBGNode(new Invocation(), 3));
//        List<Linearization> results = enumerateAllSeqs(list);
//        for (Linearization linearization : results) {
//            System.out.println(linearization);
//        }
        HappenBeforeGraph happenBeforeGraph = Program.load("minimal-1.json");
        Linearization linearization = new Linearization();
        LinVisibility linVisibility = new LinVisibility();
        Set<String> results = new HashSet<>();
        checkConsistency(happenBeforeGraph, linearization, linVisibility, results);
        System.out.println(results.size());

        System.out.println(happenBeforeGraph.generateLins().size());
    }
}
