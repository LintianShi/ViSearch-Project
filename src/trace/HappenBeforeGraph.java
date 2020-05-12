package trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class HappenBeforeGraph {
    private List<Node> startNodes = new ArrayList<>();
    private HashMap<Integer, Node> nodes = new HashMap<>();
    private int[][] programOrders;

    public HappenBeforeGraph(List<SubProgram> subPrograms, HappenBefore happenBefore) {
        int index = 0;
        programOrders = new int[subPrograms.size()][2];
        for (int k = 0; k < subPrograms.size(); k++) {
            SubProgram sp = subPrograms.get(k);
            programOrders[k][0] = index;
            for (int i = 0; i < sp.size(); i++) {
                Node node = new Node(sp.get(i), index);
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
            nodes.get(hbPair.getA()).addNextNode(nodes.get(hbPair.getB()));
            nodes.get(hbPair.getB()).addPrevNode(nodes.get(hbPair.getA()));
        }
    }

    public List<Linearization> generateLins() {
        int[] index = new int[programOrders.length];
        for (int i = 0; i < index.length; i++) {
            index[i] = programOrders[i][0];
        }
        List<Linearization> lins = new ArrayList<>();

        Stack<Node> stack = new Stack<>();
        generateLin(index, stack, lins);
        return lins;
    }

    private void generateLin(int[] index, Stack<Node> stack, List<Linearization> lins) {
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
                Node node = stack.pop();
                node.decreaseThreshlod();
            }
        }
    }

    private boolean isValid(Node node) {
        return node.checkThreshold();
    }

    private boolean isEnd(Stack<Node> stack) {
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


