package history;

import java.io.Serializable;
import java.util.*;

public class HBGNode implements Serializable {
    private Invocation invocation;

    public HBGNode() {
        ;
    }

    public HBGNode(Invocation invocation, int id) {
        this.invocation = invocation;
        invocation.setId(id);
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
