package history;

import com.google.common.collect.Multimap;
import datatype.AbstractDataType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.Linearization;

import java.util.*;

public class HappenBeforeGraph implements Iterable<HBGNode> {
    private List<HBGNode> startNodes = new ArrayList<>();
    private HashMap<Integer, HBGNode> nodes = new HashMap<>();
    private int[][] programOrders;
    private int threadNum;
    private Set<HBGNode> nodesWithoutPrev = null;
    private Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> ruleTable = null;

    public HappenBeforeGraph(List<SubProgram> subPrograms, HappenBefore happenBefore) {
        int index = 0;
        programOrders = new int[subPrograms.size()][2];
        for (int k = 0; k < subPrograms.size(); k++) {
            SubProgram sp = subPrograms.get(k);
            programOrders[k][0] = index;
            for (int i = 0; i < sp.size(); i++) {
                HBGNode node = new HBGNode(sp.get(i), index);
                node.setThreadId(k);
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

    public HappenBeforeGraph(List<List<HBGNode>> nodes) {
        for (List<HBGNode> list : nodes) {
            for (int i = 0; i < list.size(); i++) {
                this.nodes.put(list.get(i).getId(), list.get(i));
                if (i == 0) {
                    startNodes.add(list.get(0));
                    continue;
                } else {
                    list.get(i - 1).setPo(list.get(i));
                    list.get(i - 1).addNextNode(list.get(i));
                    list.get(i).addPrevNode(list.get(i - 1));
                }
            }
        }
    }

    public Iterator<HBGNode> iterator() {
        return nodes.values().iterator();
    }

    public int size() {
        return nodes.size();
    }

    public HBGNode get(int id) {
        return nodes.get(id);
    }

    public List<HBGNode> getStartNodes() {
        return startNodes;
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

    public void setRuleTable(Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> ruleTable) {
        this.ruleTable = ruleTable;
    }

    public boolean isRuleTableExist() {
        return ruleTable != null;
    }

    public Collection<ImmutablePair<Integer, Integer>> getIncompatibleRelations(ImmutablePair<Integer, Integer> pair) {
        if (ruleTable == null) {
            return null;
        }
        if (ruleTable.containsKey(pair)) {
            return ruleTable.get(pair);
        } else {
            return null;
        }
    }

    public void addHBRelation(int left, int right) {
        HBGNode prevNode = get(left);
        HBGNode nextNode = get(right);
        prevNode.addNextNode(nextNode);
        nextNode.addPrevNode(prevNode);
    }

    public void removeHBRelation(int left, int right) {
        HBGNode prevNode = get(left);
        HBGNode nextNode = get(right);
        prevNode.removeNextNode(nextNode);
        nextNode.removePrevNode(prevNode);
    }

    public void print() {
        ;
    }

    public void printStartNodes() {
        for (HBGNode node : startNodes) {
            System.out.println(node.toString());
        }
    }
}


