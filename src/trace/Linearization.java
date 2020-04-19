package trace;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Linearization {
    private List<Node> lin = new ArrayList<>();

    public Linearization() {
        ;
    }

    public Linearization(Stack<Node> stack) {
        for (Node node : stack) {
            lin.add(node);
        }
    }

    public List<Node> getLin() {
        return lin;
    }

    public void add(Node node) {
        lin.add(node);
    }

    public Node get(int index) {
        return lin.get(index);
    }

    public int size() {
        return lin.size();
    }

    public Linearization prefix(int index) {
        if (index < 0 && index >= lin.size()) {
            return null;
        } else {
            Linearization sub = new Linearization();
            for (int i = 0; i <= index; i++) {
                sub.add(lin.get(i));
            }
            return sub;
        }
    }

    public Linearization prefix(Node node) {
        for (int i = 0; i < lin.size(); i++) {
            if (node.equals(lin.get(i))) {
                return prefix(i);
            }
        }
        return null;
    }



    public String toString() {
        String temp = new String("{");
        for (Node n : lin) {
            temp += JSON.toJSONString(n.getInvocation()) + "; ";
        }

        return temp + "}";
    }
}
