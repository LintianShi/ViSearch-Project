import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Invocation invocation;
    private List<Node> nexts = new ArrayList<>();

    public Node() {
        ;
    }

    public Node(Invocation invocation) {
        this.invocation = invocation;
    }

    public void addNextNode(Node next) {
        nexts.add(next);
    }

    public List<Node> getNexts() {
        return nexts;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String toString() {
       String temp = "\"INVOCATION\":" + JSON.toJSONString(invocation) + ", \"NEXTS\":[";
       for (Node n : nexts) {
           temp += JSON.toJSONString(n.getInvocation()) + " ";
       }
       return temp + "]";
    }
}
