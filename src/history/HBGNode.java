package history;

import java.util.*;

public class HBGNode {
    private Invocation invocation;
    private List<HBGNode> nexts = new ArrayList<>();
    private List<HBGNode> prevs = new ArrayList<>();
    private HBGNode po;
    private int threshold = 0;
    private Set<HBGNode> allPrevs = null;

    public HBGNode() {
        ;
    }

    public HBGNode(Invocation invocation, int id) {
        this.invocation = invocation;
        invocation.setId(id);
    }



    public boolean checkThreshold() {
        return threshold == prevs.size();
    }

    public void increaseThreshlod() {
        for (HBGNode node : nexts) {
            node.threshold++;
        }
    }

    public void decreaseThreshlod() {
        for (HBGNode node : nexts) {
            node.threshold--;
        }
    }

    public HBGNode getPo() {
        return po;
    }

    public void setPo(HBGNode po) {
        this.po = po;
    }

    public void addNextNode(HBGNode next) {
        if (next != null)
            nexts.add(next);
    }
    public void addPrevNode(HBGNode prev) {
        if (prev != null)
            prevs.add(prev);
    }

    public int getId() {
        return invocation.getId();
    }

    public List<HBGNode> getNexts() {
        return nexts;
    }

    public List<HBGNode> getPrevs() {
        return prevs;
    }

    public Set<HBGNode> getAllPrevs() {
        if (allPrevs != null) {
            return allPrevs;
        }
        allPrevs = new HashSet<>(prevs);
        for (HBGNode prevNode : prevs) {
            allPrevs.addAll(prevNode.getAllPrevs());
        }
        return allPrevs;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    @Override
    public String toString() { ;
        return invocation.toString();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
