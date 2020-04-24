package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class CausalVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<Node> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        if (!new BasicVisibilityPredicate().check(visibility, prefixLin, linVisibility)) {
            return false;
        }
        for (Node n : visibility) {
            if (!visibility.containsAll(linVisibility.getNodeVisibility(n))) {
                return false;
            }
        }
        return true;
    }
}
