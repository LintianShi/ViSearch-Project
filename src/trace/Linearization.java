package trace;

import com.alibaba.fastjson.JSON;
import visibility.LinVisibility;

import java.util.*;

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

    public List<LinVisibility> generateAllNodeVisibility() {
        HashMap<Node, List<Set<Node>>> result = new HashMap<>();
        for (Node node : lin) {
            result.put(node, generateNodeVisibility(node));
        }
        //System.out.println("good");
        List<LinVisibility> linVisibilities = new ArrayList<>();
        int index[] = new int[lin.size()];
        for (int i = 0; i < index.length; i++) {
            index[i] = 0;
        }

        do {
            //System.out.println(Arrays.toString(index));
            LinVisibility v = new LinVisibility();
            for (int i = 0; i < lin.size(); i++) {
                Node n = lin.get(i);
                v.updateNodeVisibility(n, result.get(n).get(index[i]));
            }
            linVisibilities.add(v);
        } while (updateIndex(index, result));

        return  linVisibilities;
    }

    private boolean updateIndex(int[] index, HashMap<Node, List<Set<Node>>> result) {
        index[index.length  - 1]++;
        for (int i = index.length  - 1; i >= 0; i--) {
            if (index[i] == result.get(lin.get(i)).size()) {
                if (i != 0) {
                    index[i] = 0;
                    index[i-1]++;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private List<Set<Node>> generateNodeVisibility(Node node) {
        Linearization prefixLin = prefix(node);
        List<Set<Node>> result = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        //System.out.println("ping" + node.getInvocation().getPairID().toString());
        generateNodeVisibility(prefixLin, 0, stack, result);
        //System.out.println("pong" + node.getInvocation().getPairID().toString());
        return result;
    }

    private void generateNodeVisibility(Linearization prefixLin, int index, Stack<Node> stack, List<Set<Node>> result) {
        if (index == prefixLin.size() - 1) {
            Set<Node> temp = new HashSet<>(stack);
            temp.add(prefixLin.get(index));
            result.add(temp);
            return;
        }
        stack.push(prefixLin.get(index));
        generateNodeVisibility(prefixLin, index + 1, stack, result);
        stack.pop();
        generateNodeVisibility(prefixLin, index + 1, stack, result);
    }

    public String toString() {
        String temp = new String("{");
        for (Node n : lin) {
            temp += JSON.toJSONString(n.getInvocation()) + "; ";
        }

        return temp + "}";
    }
}
