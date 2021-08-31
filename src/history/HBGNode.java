package history;

import java.io.Serializable;
import java.util.*;

public class HBGNode implements Serializable {
    private Invocation invocation;
    private transient Set<HBGNode> nexts = new HashSet<>();
    private transient Set<HBGNode> prevs = new HashSet<>();
    private transient HBGNode po;
    private transient int threshold = 0;
    private transient Set<HBGNode> allPrevs = null;

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

    public void removeNextNode(HBGNode next) {
        if (next != null)
            nexts.remove(next);
    }
    public void removePrevNode(HBGNode prev) {
        if (prev != null)
            prevs.remove(prev);
    }

    public int getId() {
        return invocation.getId();
    }

    public int getThreadId() {
        return invocation.getThreadId();
    }

    public void setThreadId(int threadId) {
        invocation.setThreadId(threadId);
    }

    public Set<HBGNode> getNexts() {
        return nexts;
    }

    public Set<HBGNode> getPrevs() {
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
        return Integer.toString(getId()) + " " + invocation.toString();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public HBGNode clone() {
        return new HBGNode(this.invocation, this.getId());
    }

    public boolean equals(Object node) {
        return this.hashCode() == node.hashCode();
    }
}
