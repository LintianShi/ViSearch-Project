import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();
    private List<Node> prevs = new ArrayList<>();
    private int threshold = 0;
    private int id;

    public Node() {
        ;
    }

    public Node(Invocation invocation, int id) {
        this.invocation = invocation;
        this.id = id;
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
        return id;
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

    public String toString() {
       String temp = "\"INVOCATION\":" + JSON.toJSONString(invocation);
//       + ", {\"NEXTS\":[";
//       for (Node n : nexts) {
//           temp += JSON.toJSONString(n.getInvocation()) + " ";
//       }
//       return temp + "]}";
        return temp;
    }
}
