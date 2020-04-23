package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class MonotonicVisibility extends VisibilityPredicate {
    public boolean vis(Set<Node> visibility, Linearization prefixLin) {
        Node node = prefixLin.get(prefixLin.size() - 1);
        Set<Node> hb = getAllHappenBefore(node);
        for (Node n : hb) {
            if (!visibility.containsAll(prefixLin.getNodeVisibility(node))) {
                return false;
            }
        }
        return true;
    }
}
