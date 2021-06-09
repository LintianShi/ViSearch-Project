package history;

import datatype.AbstractDataType;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.Linearization;

import java.util.*;

public class HappenBeforeGraph implements Iterable<HBGNode> {
    private List<HBGNode> startNodes = new ArrayList<>();
    private HashMap<Integer, HBGNode> nodes = new HashMap<>();
    private int[][] programOrders;
    private int threadNum;
    private Set<HBGNode> nodesWithoutPrev = null;

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
                    nodes.get(index-1).setPo(node);
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

    public List<HBGNode> getRelatedOperation(HBGNode node, AbstractDataType adt) {
        List<HBGNode> list = new ArrayList<>();
        for (HBGNode startNode : startNodes) {
            HBGNode temp = startNode;
            //System.out.println(temp.getPo());
            while (temp.getPo() != null) {
                //System.out.println("!");
                if (adt.isRelated(node.getInvocation(), temp.getInvocation())) {
                    //System.out.println("related");
                    list.add(temp.clone());
                }
                temp = temp.getPo();
            }
        }
        return list;
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

    public void printStartNodes() {
        for (HBGNode node : startNodes) {
            System.out.println(node.toString());
        }
    }
}


