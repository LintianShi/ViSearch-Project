package visibility;

import trace.Linearization;
import trace.HBGNode;

import java.util.Set;

public class CausalVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        if (!new BasicVisibilityPredicate().check(visibility, prefixLin, linVisibility)) {
            return false;
        }
        for (HBGNode n : visibility) {
            if (!visibility.containsAll(linVisibility.getNodeVisibility(n))) {
                return false;
            }
        }
        return true;
    }
}
