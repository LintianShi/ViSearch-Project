import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HappenBeforeGraph {
    private List<Node> startNodes = new ArrayList<>();
    private HashMap<Integer, Node> nodes = new HashMap<>();

    public HappenBeforeGraph(List<SubProgram> subPrograms, HappenBefore happenBefore) {
        int index = 0;
        for (SubProgram sp : subPrograms) {
            for (int i = 0; i < sp.size(); i++) {
                Node node = new Node(sp.get(i));
                nodes.put(index, node);
                index++;
                if (i == 0) {
                    startNodes.add(node);
                }
            }
        }

        for (int i = 0; i < happenBefore.size(); i++) {
            Pair pair = happenBefore.get(i);
            nodes.get(pair.getA()).addNextNode(nodes.get(pair.getB()));
        }
    }

    public void print() {
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i));
        }
    }
}


