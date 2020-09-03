package test;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class ManualRecurse {
    private PriorityQueue<StateFrame> frameStack = new PriorityQueue<>();
    private List<Integer> elements;

    public ManualRecurse(List<Integer> elements) {
        this.elements = elements;
    }

    public List<String> enumerate() {
        ArrayList<Integer> stack = new ArrayList<>();
        List<String> results = new ArrayList<>();
        StateFrame init = new StateFrame(stack, 0);
        frameStack.offer(init);
        while (!frameStack.isEmpty()) {
            System.out.println(frameStack);
            StateFrame state = frameStack.poll();
            if (state.isPruned) {
                continue;
            }
            if (state.index >= elements.size()) {
                results.add(state.stack.toString());
                if (state.stack.size() == 1 && state.stack.get(0) == 3) {
                    prune(state.stack);
                }
                continue;
            }

            ArrayList<Integer> temp = (ArrayList<Integer>) state.stack.clone();
            temp.add(elements.get(state.index));
            frameStack.offer(new StateFrame(temp, state.index + 1));
            frameStack.offer(new StateFrame((ArrayList<Integer>)state.stack.clone(), state.index + 1));
        }
        return results;
    }

    private void prune(List<Integer> list) {
        for (StateFrame stateFrame : frameStack) {
            if (stateFrame.stack.containsAll(list)) {
                stateFrame.isPruned = true;
            }
        }
        for (Integer i : list) {
            elements.remove(i);
        }
    }

    public static void main(String[] args) {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);
        List<String> results = new ManualRecurse(data).enumerate();
        for (String s : results) {
            System.out.println(s);
        }
    }
}

class StateFrame implements Comparable<StateFrame> {
    public StateFrame(ArrayList<Integer> stack, int index) {
        this.stack = stack;
        this.index = index;
        this.key = new ImmutablePair<>(stack.size(), index);
    }
    public ArrayList<Integer> stack;
    public int index;
    public boolean isPruned = false;
    private Pair<Integer, Integer> key;

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
