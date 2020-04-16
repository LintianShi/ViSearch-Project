import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Linearization {
    private List<Invocation> lin = new ArrayList<>();

    public Linearization() {
        ;
    }

    public Linearization(Stack<Node> stack) {
        for (Node node : stack) {
            lin.add(node.getInvocation());
        }
    }

    public List<Invocation> getLin() {
        return lin;
    }

    public String toString() {
        String temp = new String("{");
        for (Invocation i : lin) {
            temp += JSON.toJSONString(i) + "; ";
        }

        return temp + "}";
    }
}
