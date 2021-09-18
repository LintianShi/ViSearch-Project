package arbitration;

import history.HBGNode;
import history.HappenBeforeGraph;

import java.io.Serializable;
import java.util.*;

public class Linearization implements Serializable, Iterable<HBGNode> {
    private List<HBGNode> lin = new ArrayList<>();

    public Linearization() {
        ;
    }

    public Linearization(Stack<HBGNode> stack) {
        for (HBGNode node : stack) {
            lin.add(node);
        }
    }

    public void add(HBGNode node) {
        lin.add(node);
    }

    public void addAll(Linearization linearization) {
        for (HBGNode node : linearization) {
            lin.add(node);
        }
    }

    public String getRetValueTrace(int index) {
        ArrayList<String> retTrace = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            retTrace.add(lin.get(i).getInvocation().getRetValue());
        }
        return  retTrace.toString();
    }

    public boolean contains(HBGNode node) {
        return lin.contains(node);
    }

    public boolean contains(Integer id) {
        for (HBGNode node : lin) {
            if (node.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public HBGNode getLast() {
        return lin.get(lin.size() - 1);
    }

    public HBGNode get(int index) {
        return lin.get(index);
    }

    public int size() {
        return lin.size();
    }

    public int getQueryOperationSize() {
        int sz = 0;
        for (HBGNode node : lin) {
            if (node.getInvocation().getOperationType().equals("QUERY")) {
                sz++;
            }
        }
        return sz;
    }

    public Iterator<HBGNode> iterator() {
        return lin.iterator();
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

    public Linearization prefix(HBGNode node) {
        for (int i = 0; i < lin.size(); i++) {
            if (node.equals(lin.get(i))) {
                return prefix(i);
            }
        }
        return null;
    }

    public Set<HBGNode> getAdjacencyNodes(HappenBeforeGraph happenBeforeGraph) {
        Set<HBGNode> adjacencyNodes = new HashSet<>();
        Set<HBGNode> expansion = new HashSet<>();
        for (HBGNode node : lin) {
            for (HBGNode next :happenBeforeGraph.getNexts(node)) {
                if (!lin.contains(next)) {
                    expansion.add(next);
                }
            }
        }
        for (HBGNode node : expansion) {
            boolean flag = true;
            for (HBGNode prev : happenBeforeGraph.getPrevs(node)) {
                if (!lin.contains(prev)) {    //节点所有的前驱必须都已经被包含在全序里
                    flag = false;
                    break;
                }
            }
            if (flag) {
                adjacencyNodes.add(node);
            }
        }
        return adjacencyNodes;
    }

    public List<Linearization> extendLin(Set<HBGNode> adjNodes) {
        List<Linearization> extentLins = new ArrayList<>();
        for (HBGNode node : adjNodes) {
            Linearization linearization = (Linearization) this.clone();
            linearization.add(node);
            extentLins.add(linearization);
        }
        return extentLins;
    }

    public List<LinVisibility> generateAllNodeVisibility() {
        HashMap<HBGNode, List<Set<HBGNode>>> result = new HashMap<>();
        for (HBGNode node : lin) {
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
                HBGNode n = lin.get(i);
                v.updateNodeVisibility(n, result.get(n).get(index[i]));
            }
            linVisibilities.add(v);
        } while (updateIndex(index, result));

        return  linVisibilities;
    }

    private boolean updateIndex(int[] index, HashMap<HBGNode, List<Set<HBGNode>>> result) {
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

    private List<Set<HBGNode>> generateNodeVisibility(HBGNode node) {
        Linearization prefixLin = prefix(node);
        List<Set<HBGNode>> result = new ArrayList<>();
        Stack<HBGNode> stack = new Stack<>();
        //System.out.println("ping" + node.getInvocation().getPairID().toString());
        generateNodeVisibility(prefixLin, 0, stack, result);
        //System.out.println("pong" + node.getInvocation().getPairID().toString());
        return result;
    }

    private void generateNodeVisibility(Linearization prefixLin, int index, Stack<HBGNode> stack, List<Set<HBGNode>> result) {
        if (index == prefixLin.size() - 1) {
            Set<HBGNode> temp = new HashSet<>(stack);
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
        ArrayList<String> list = new ArrayList<>();
        for (HBGNode node : lin) {
            list.add(node.toString());
        }
        return list.toString();
    }

    @Override
    public Object clone() {
        Linearization newLin = new Linearization();
        newLin.lin = new ArrayList<>(this.lin);
        return newLin;
    }
}
