package history;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import datatype.AbstractDataType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import arbitration.Linearization;

import java.util.*;

public class HappenBeforeGraph implements Iterable<HBGNode> {
    private List<HBGNode> startNodes = new ArrayList<>();
    private HashMap<Integer, HBGNode> nodes = new HashMap<>();
    private HashMap<Integer, Integer> poRelations = new HashMap<>();
    private Multimap<Integer, Integer> nextRelations = HashMultimap.create();
    private Multimap<Integer, Integer> prevRelations = HashMultimap.create();
    private Multimap<Integer, Integer> allPrevRelations = HashMultimap.create();
    private Set<HBGNode> nodesWithoutPrev = null;
    private Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> ruleTable = null;

    public HappenBeforeGraph(List<SubProgram> subPrograms, HappenBefore happenBefore) {
        int index = 0;
        for (int k = 0; k < subPrograms.size(); k++) {
            SubProgram sp = subPrograms.get(k);
            for (int i = 0; i < sp.size(); i++) {
                HBGNode node = new HBGNode(sp.get(i), index);
                node.setThreadId(k);
                nodes.put(index, node);
                if (i == 0) {
                    startNodes.add(node);
                } else {
                    poRelations.put(index - 1, index);
                    nextRelations.put(index - 1, index);
                    prevRelations.put(index, index - 1);
                }
                index++;
            }
        }

        for (int i = 0; i < happenBefore.size(); i++) {
            HBPair hbPair = happenBefore.get(i);
            addNextNode(transferPairToID(subPrograms, hbPair.getPrev()), transferPairToID(subPrograms, hbPair.getNext()));
            addPrevNode(transferPairToID(subPrograms, hbPair.getNext()), transferPairToID(subPrograms, hbPair.getPrev()));
        }
    }

    public HappenBeforeGraph(List<List<HBGNode>> nodes) {
        for (List<HBGNode> list : nodes) {
            for (int i = 0; i < list.size(); i++) {
                this.nodes.put(list.get(i).getId(), list.get(i));
                if (i == 0) {
                    startNodes.add(list.get(0));
                    continue;
                } else {
                    poRelations.put(list.get(i - 1).getId(), list.get(i).getId());
                    nextRelations.put(list.get(i - 1).getId(), list.get(i).getId());
                    prevRelations.put(list.get(i).getId(), list.get(i - 1).getId());
                }
            }
        }
    }

    public HBGNode getPo(HBGNode node) {
        Integer index = poRelations.get(node.getId());
        if (index == null) {
            return  null;
        }
        return nodes.get(index);
    }

    public Set<HBGNode> getNexts(HBGNode node) {
        Set<HBGNode> nexts = new HashSet<>();
        Collection<Integer> index = nextRelations.get(node.getId());
        if (index != null) {
            for (Integer i : index) {
                nexts.add(nodes.get(i));
            }
        }
        return  nexts;
    }

    public Set<HBGNode> getPrevs(HBGNode node) {
        Set<HBGNode> prevs = new HashSet<>();
        Collection<Integer> index = prevRelations.get(node.getId());
        if (index != null) {
            for (Integer i : index) {
                prevs.add(nodes.get(i));
            }
        }
        return  prevs;
    }

    public Set<HBGNode> getAllPrevs(HBGNode node) {
        Collection<Integer> index = getAllPrevs(node.getId());
        Set<HBGNode> allPrevs = new HashSet<>();
        for (Integer i : index) {
            allPrevs.add(nodes.get(i));
        }
        return allPrevs;
    }

    private Collection<Integer> getAllPrevs(Integer node) {
        Collection<Integer> prevs = allPrevRelations.get(node);
        if (prevs != null) {
            return prevs;
        }
        prevs = new HashSet<>();
        Collection<Integer> allPrevs = new HashSet<>(prevs);
        for (Integer prevNode : prevs) {
            allPrevs.addAll(getAllPrevs(prevNode));
        }
        return allPrevs;
    }

    public void addNextNode(HBGNode node, HBGNode next) {
        nextRelations.put(node.getId(), next.getId());
    }

    public void addPrevNode(HBGNode node, HBGNode prev) {
        prevRelations.put(node.getId(), prev.getId());
    }

    public void addNextNode(Integer node, Integer next) {
        nextRelations.put(node, next);
    }

    public void addPrevNode(Integer node, Integer prev) {
        prevRelations.put(node, prev);
    }

    public void removeNextNode(HBGNode node, HBGNode next) {
        nextRelations.remove(node.getId(), next.getId());
    }

    public void removePrevNode(HBGNode node, HBGNode prev) {
        prevRelations.remove(node.getId(), prev.getId());
    }

    public void removeNextNode(Integer node, Integer next) {
        nextRelations.remove(node, next);
    }

    public void removePrevNode(Integer node, Integer prev) {
        prevRelations.remove(node, prev);
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
            if (getPrevs(node).isEmpty()) {
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
        addNextNode(left, right);
        addPrevNode(right, left);
    }

    public void removeHBRelation(int left, int right) {
        removeNextNode(left, right);
        removePrevNode(right, left);
    }

    public void print() {
        ;
    }
}


