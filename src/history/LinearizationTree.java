package history;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class LinearizationTree {
    private LTNode root;

    public LinearizationTree() {
        root = new LTNode();
    }

    public LTNode getRoot() {
        return root;
    }

    public void print() {
        Queue<Pair<LTNode, Integer>> queue = new ArrayDeque<Pair<LTNode, Integer>>();
        queue.offer(new ImmutablePair<>(root, 0));
        int depth = 0;
        while (!queue.isEmpty()) {
            Pair<LTNode, Integer> pair = queue.poll();
            if (depth == pair.getRight()) {
            } else {
                System.out.println();
                depth++;
            }
            if (pair.getLeft().getInvocation() == null) {
                System.out.print("root");
            } else {
                System.out.print(pair.getLeft().getInvocation().toString() + "\t");
            }
            List<LTNode> list = pair.getLeft().getNexts();
            for (LTNode node : list) {
                queue.offer(new ImmutablePair<>(node, depth + 1));
            }
        }
    }

    static class LTNode {
        private Invocation invocation;
        private List<LTNode> nexts;

        public LTNode() {
            nexts = new ArrayList<>();
        }

        public LTNode(Invocation invocation) {
            this.invocation = invocation;
            nexts = new ArrayList<>();
        }

        public Invocation getInvocation() {
            return invocation;
        }

        public void setInvocation(Invocation invocation) {
            this.invocation = invocation;
        }

        public List<LTNode> getNexts() {
            return nexts;
        }

        public void addChildNode(LTNode ltNode) {
            nexts.add(ltNode);
        }
    }

    public static void main(String[] args) throws Exception {
        File filename = new File("test1.json");
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        Program program = JSON.parseObject(jsonfile, Program.class);

        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();
        for (HappenBeforeGraph g : list) {
            LinearizationTree linTree = g.generateLinTree();
            linTree.print();
        }
    }
}
