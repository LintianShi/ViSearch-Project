package history;

import org.apache.commons.lang3.tuple.Pair;
import arbitration.Linearization;

import java.util.*;

public class HappenBeforeGraph implements Iterable<HBGNode> {
    private List<HBGNode> startNodes = new ArrayList<>();
    private HashMap<Integer, HBGNode> nodes = new HashMap<>();
    private int[][] programOrders;
    private int threadNum;
    private Set<HBGNode> nodesWithoutPrev = null;

    public HappenBeforeGraph(List<HBGNode> startNodes, HashMap<Integer, HBGNode> map) {
        this.startNodes = startNodes;
        this.nodes = map;
        this.threadNum = startNodes.size();
    }

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
        threadNum = subPrograms.size();
    }

    public Iterator<HBGNode> iterator() {
        return nodes.values().iterator();
    }

    public int size() {
        return nodes.size();
    }

    public int transferPairToID(List<SubProgram> subPrograms, Pair<Integer, Integer> pair) {
        int id = 0;
        for (int i = 0; i < pair.getLeft(); i++) {
            id += subPrograms.get(i).size();
        }
        id += pair.getRight();
        return id;
    }

    public Set<HBGNode> getNodesWithoutPrev() {
        if (this.nodesWithoutPrev != null) {
            return this.nodesWithoutPrev;
        } else {
            this.nodesWithoutPrev = findNodesWithoutPrev();
            return this.nodesWithoutPrev;
        }
    }

    private Set<HBGNode> findNodesWithoutPrev() {
        Set<HBGNode> list = new HashSet<>();
        for (HBGNode node : nodes.values()) {
            if (node.getPrevs().isEmpty()) {
                list.add(node);
            }
        }
        return list;
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

    private boolean checkBreakPoint(HBGNode breakPoint) {
        List<HBGNode> prevs = breakPoint.getPrevs();
        List<HBGNode> nexts = breakPoint.getNexts();
        if (prevs.size() != threadNum || nexts.size() != threadNum) {
            return false;
        }

        for (HBGNode prev : prevs) {
            boolean flag = false;
            if (prev.getPo() == breakPoint) {
                continue;
            }
            for (HBGNode next : nexts) {
                if (breakPoint.getPo() == breakPoint) {
                    continue;
                }
                if (prev.getPo() == next) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(HBGNode node) {
        return node.checkThreshold();
    }

    private boolean isEnd(Stack<HBGNode> stack) {
        return stack.size() == nodes.size();
    }

    public void print() {
        for (HBGNode node : this) {
            if (checkBreakPoint(node)) {
                System.out.println("break point: " + node.getInvocation().getRetValue());
                System.out.println("Prevs: ");
                for (HBGNode prev : node.getPrevs()) {
                    System.out.print(prev.getInvocation().getRetValue() + ", ");
                }
                System.out.println();
                System.out.println("Nexts: ");
                for (HBGNode next : node.getNexts()) {
                    System.out.print(next.getInvocation().getRetValue() + ", ");
                }
                System.out.println();
            }
            //System.out.println(node.getNexts().size());
        }
    }
}


