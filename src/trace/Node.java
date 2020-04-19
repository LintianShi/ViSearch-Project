package trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import visibility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node implements Visibility  {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();
    private List<Node> prevs = new ArrayList<>();
    private int threshold = 0;

    public Node() {
        ;
    }

    public Node(Invocation invocation, int id) {
        this.invocation = invocation;
        invocation.setId(id);
    }



    public boolean checkThreshold() {
        return threshold == prevs.size();
    }

    public void increaseThreshlod() {
        for (Node node : nexts) {
            node.threshold++;
        }
    }

    public void decreaseThreshlod() {
        for (Node node : nexts) {
            node.threshold--;
        }
    }

    public void addNextNode(Node next) {
        nexts.add(next);
    }
    public void addPrevNode(Node prev) {
        prevs.add(prev);
    }

    public int getId() {
        return invocation.getId();
    }

    public List<Node> getNexts() {
        return nexts;
    }

    public List<Node> getPrevs() {
        return prevs;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public Set<Node> vis(Linearization prefixLin) {
        String visibility = getInvocation().getVisibility();
        if (visibility.equals("COMPLETE")) {
            return new CompleteVisibility().vis(prefixLin);
        } else if (visibility.equals("CAUSAL")) {
            return new CausalVisibility().vis(prefixLin);
        } else if (visibility.equals("PEER")) {
            return new PeerVisibility().vis(prefixLin);
        } else if (visibility.equals("MONOTONIC")) {
            return new MonotonicVisibility().vis(prefixLin);
        } else if (visibility.equals("BASIC")) {
            return new BasicVisibility().vis(prefixLin);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
       String temp = "\"INVOCATION\":" + JSON.toJSONString(invocation);
//       + ", {\"NEXTS\":[";
//       for (Node n : nexts) {
//           temp += JSON.toJSONString(n.getInvocation()) + " ";
//       }
//       return temp + "]}";
        return temp;
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
