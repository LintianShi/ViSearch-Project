package trace;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class HappenBeforeGraph {
    private List<HBGNode> startNodes = new ArrayList<>();
    private HashMap<Integer, HBGNode> nodes = new HashMap<>();
    private int[][] programOrders;

    public HappenBeforeGraph(List<SubProgram> subPrograms, HappenBefore happenBefore) {
        int index = 0;
        programOrders = new int[subPrograms.size()][2];
        for (int k = 0; k < subPrograms.size(); k++) {
            SubProgram sp = subPrograms.get(k);
            programOrders[k][0] = index;
            for (int i = 0; i < sp.size(); i++) {
                HBGNode node = new HBGNode(sp.get(i), index);
                nodes.put(index, node);
                if (i == 0) {
                    startNodes.add(node);
                } else {
                    nodes.get(index-1).addNextNode(node);
                    node.addPrevNode(nodes.get(index-1));
                }
                programOrders[k][1] = index;
                index++;
            }

        }

        for (int i = 0; i < happenBefore.size(); i++) {
            HBPair hbPair = happenBefore.get(i);
            nodes.get(transferPairToID(subPrograms, hbPair.getPrev())).addNextNode(nodes.get(transferPairToID(subPrograms, hbPair.getNext())));
            nodes.get(transferPairToID(subPrograms, hbPair.getNext())).addPrevNode(nodes.get(transferPairToID(subPrograms, hbPair.getPrev())));
        }
    }

    public int transferPairToID(List<SubProgram> subPrograms, Pair<Integer, Integer> pair) {
        int id = 0;
        for (int i = 0; i < pair.getLeft(); i++) {
            id += subPrograms.get(i).size();
        }
        id += pair.getRight();
        return id;
    }

    public LinearizationTree generateLinTree() {
        int[] index = new int[programOrders.length];
        for (int i = 0; i < index.length; i++) {
            index[i] = programOrders[i][0];
        }
        LinearizationTree linTree = new LinearizationTree();
        Stack<LinearizationTree.LTNode> stack = new Stack<>();
        stack.push(linTree.getRoot());
        generateLinTree(index, stack);
        return linTree;
    }

    private void generateLinTree(int[] index, Stack<LinearizationTree.LTNode> stack) {
        if (stack.size() == nodes.size() + 1) {
            return;
        }
        for (int i = 0; i < index.length; i++) {
            if (index[i] <= programOrders[i][1] && isValid(nodes.get(index[i]))) {
                LinearizationTree.LTNode ltNode = new LinearizationTree.LTNode(nodes.get(index[i]).getInvocation());
                stack.peek().addChildNode(ltNode);
                stack.push(ltNode);
                nodes.get(index[i]).increaseThreshlod();
                index[i]++;
                generateLinTree(index, stack);
                index[i]--;
                LinearizationTree.LTNode node = stack.pop();
                nodes.get(node.getInvocation().getId()).decreaseThreshlod();
            }
        }
    }

    public List<Linearization> generateLins() {
        int[] index = new int[programOrders.length];
        for (int i = 0; i < index.length; i++) {
            index[i] = programOrders[i][0];
        }
        List<Linearization> lins = new ArrayList<>();

        Stack<HBGNode> stack = new Stack<>();
        generateLin(index, stack, lins);
        return lins;
    }

    private void generateLin(int[] index, Stack<HBGNode> stack, List<Linearization> lins) {
        if (isEnd(stack)) {
            lins.add(new Linearization(stack));
        }
        for (int i = 0; i < index.length; i++) {
            if (index[i] <= programOrders[i][1] && isValid(nodes.get(index[i]))) {
                stack.push(nodes.get(index[i]));
                nodes.get(index[i]).increaseThreshlod();
                index[i]++;
                generateLin(index, stack, lins);
                index[i]--;
                HBGNode node = stack.pop();
                node.decreaseThreshlod();
            }
        }
    }

    private boolean isValid(HBGNode node) {
        return node.checkThreshold();
    }

    private boolean isEnd(Stack<HBGNode> stack) {
        return stack.size() == nodes.size();
    }

    public void print() {
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i));
        }
    }

    public static void main(String[] args) {
        ;
    }
}


