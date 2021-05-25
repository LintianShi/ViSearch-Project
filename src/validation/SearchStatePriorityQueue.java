package validation;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchStatePriorityQueue {
    private int mode; //0: stack, 1: queue, 2: priority queue
    private Stack<SearchState> stack = new Stack<>();
    private Queue<SearchState> queue = new LinkedList<>();
    private PriorityQueue<SearchState> priorityQueue = new PriorityQueue<>();

    public SearchStatePriorityQueue() {
        this.mode = 0;
    }

    public SearchStatePriorityQueue(int mode) {
        if (mode >= 0 && mode <= 2) {
            this.mode = mode;
        } else {
            this.mode = 0;
        }
    }

    public boolean offer(SearchState searchState) {
        if (mode == 0) {
            return (stack.push(searchState) != null);
        } else if (mode == 1) {
            return queue.offer(searchState);
        } else {
            return priorityQueue.offer(searchState);
        }
    }

    public SearchState peek() {
        if (mode == 0) {
            return stack.peek();
        } else if (mode == 1) {
            return queue.peek();
        } else {
            return priorityQueue.peek();
        }
    }

    public SearchState poll() {
        if (mode == 0) {
            return stack.pop();
        } else if (mode == 1) {
            return queue.poll();
        } else {
            return priorityQueue.poll();
        }
    }

    public boolean isEmpty() {
        if (mode == 0) {
            return stack.isEmpty();
        } else if (mode == 1) {
            return queue.isEmpty();
        } else {
            return priorityQueue.isEmpty();
        }
    }

    public int size() {
        if (mode == 0) {
            return stack.size();
        } else if (mode == 1) {
            return queue.size();
        } else {
            return priorityQueue.size();
        }
    }

    public String toString() {
        if (mode == 0) {
            return stack.toString();
        } else if (mode == 1) {
            return queue.toString();
        } else {
            return priorityQueue.toString();
        }
    }

}
