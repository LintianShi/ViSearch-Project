package visibility;

import trace.Node;

import java.util.HashMap;
import java.util.Set;

public class LinVisibility {
    private HashMap<Node, Set<Node>> visibility = new HashMap<>();

    public void setVisibility(HashMap<Node, Set<Node>> visibility) {
        this.visibility = visibility;
    }

    public void cleanVisibility() {
        visibility = new HashMap<>();
    }

    public Set<Node> getNodeVisibility(Node node) {
        return visibility.get(node);
    }

    public void updateNodeVisibility(Node node, Set<Node> vis) {
        visibility.put(node, vis);
    }

    public String toString() {
        return visibility.toString();
    }
}
