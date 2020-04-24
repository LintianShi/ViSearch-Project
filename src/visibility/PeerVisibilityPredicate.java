package visibility;

import trace.Linearization;
import trace.Node;

import java.util.Set;

public class PeerVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<Node> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        if (new MonotonicVisibilityPredicate().check(visibility, prefixLin, linVisibility)) {
            for (Node n : visibility) {
                if (!visibility.containsAll(getAllHappenBefore(n))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
