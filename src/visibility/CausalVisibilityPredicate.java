package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class CausalVisibilityPredicate extends VisibilityPredicate {
    public boolean vis(Set<Node> visibility, Linearization prefixLin) {
        for (Node n : visibility) {
            if (!visibility.containsAll(prefixLin.getNodeVisibility(n))) {
                return false;
            }
        }
        return true;
    }
}
