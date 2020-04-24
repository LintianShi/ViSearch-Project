package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class MonotonicVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<Node> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        Node node = prefixLin.get(prefixLin.size() - 1);
        Set<Node> hb = getAllHappenBefore(node);
        for (Node n : hb) {
            if (!visibility.containsAll(linVisibility.getNodeVisibility(n))) {
                return false;
            }
        }
        return true;
    }
}
