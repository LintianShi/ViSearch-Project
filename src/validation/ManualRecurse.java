package validation;

import history.HBGNode;
import history.Invocation;
import arbitration.VisibilityType;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ManualRecurse {
    private PriorityQueue<StateFrame> frameStack = new PriorityQueue<>();
    private List<HBGNode> elements;
    private boolean[] isPruned;
    private boolean start = false;

    public ManualRecurse(List<HBGNode> elements) {
        this.elements = elements;
        isPruned = new boolean[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            isPruned[i] = false;
        }
    }

    public List<HBGNode> enumerate() {
        if (!start) {
            ArrayList<HBGNode> stack = new ArrayList<>();
            StateFrame init = new StateFrame(stack, 0);
            frameStack.offer(init);
            start = true;
        }

        while (!frameStack.isEmpty()) {
            //System.out.println(frameStack);
            StateFrame state = frameStack.poll();
            if (state.isPruned) {
                continue;
            }
            if (state.index == elements.size()) {
                return state.stack;
            }
            if (!isPruned[state.index]) {
                ArrayList<HBGNode> temp = (ArrayList<HBGNode>) state.stack.clone();
                temp.add(elements.get(state.index));
                frameStack.offer(new StateFrame(temp, state.index + 1));
            }
            frameStack.offer(new StateFrame((ArrayList<HBGNode>)state.stack.clone(), state.index + 1));
        }
        return null;
    }

    public void prune(List<HBGNode> list) {
        for (StateFrame stateFrame : frameStack) {
            if (stateFrame.stack.containsAll(list)) {
                stateFrame.isPruned = true;
            }
        }
        for (HBGNode node : list) {
            isPruned[elements.indexOf(node)] = true;
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayList<HBGNode> data = new ArrayList<>();
        data.add(new HBGNode(new Invocation(), 1));
        data.add(new HBGNode(new Invocation(), 2));
        data.add(new HBGNode(new Invocation(), 3));
        data.add(new HBGNode(new Invocation(), 4));
        data.add(new HBGNode(new Invocation(), 5));
        ManualRecurse manualRecurse = new ManualRecurse(data);

        List<HBGNode> subset = null;
        while ((subset = manualRecurse.enumerate()) != null) {
//            if (subset.size() == 1 && subset.get(0).getId() == 2)
//            if (subset.size() == 2 && subset.get(0).getId() == 3 && subset.get(1).getId() == 4)
//            if (subset.size() == 0)
//                manualRecurse.prune(subset);
            for (HBGNode node : subset) {
                System.out.print(node.getId());
                System.out.print(" ");
            }
            System.out.println();
        }

    }
}



class StateFrame implements Comparable<StateFrame> {
    public ArrayList<HBGNode> stack;
    public int index;
    public boolean isPruned = false;

    public StateFrame(ArrayList<HBGNode> stack, int index) {
        this.stack = stack;
        this.index = index;
    }

    @Override
    public int compareTo(StateFrame o) {
        if (stack.size() > o.stack.size()) {
            return 1;
        } else if (stack.size() < o.stack.size()) {
            return -1;
        } else {
            if (index >= o.index) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    @Override
    public String toString() {
        String result = "Index: " + Integer.toString(index) + ";";
        result += stack.toString();
        return result;
    }
}
