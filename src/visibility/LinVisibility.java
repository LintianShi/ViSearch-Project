package visibility;

import history.HBGNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LinVisibility implements Iterable<HBGNode> {
    private HashMap<HBGNode, Set<HBGNode>> visibility = new HashMap<>();

    public void setVisibility(HashMap<HBGNode, Set<HBGNode>> visibility) {
        this.visibility = visibility;
    }

    public void cleanVisibility() {
        visibility = new HashMap<>();
    }

    public Set<HBGNode> getNodeVisibility(HBGNode node) {
        return visibility.get(node);
    }

    public void updateNodeVisibility(HBGNode node, Set<HBGNode> vis) {
        visibility.put(node, vis);
    }

    public void removeNodeVisibility(HBGNode node) {
        visibility.remove(node);
    }

    public String toString() {
        return visibility.toString();
    }

    public int size() {
        return visibility.size();
    }

    public Iterator<HBGNode> iterator() {
        return visibility.keySet().iterator();
    }

    @Override
    public Object clone() {
        LinVisibility newVis = new LinVisibility();
        newVis.visibility = new HashMap<>(this.visibility);
        return newVis;
    }
}
