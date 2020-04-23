package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class PeerVisibilityPredicate extends VisibilityPredicate {
    public boolean vis(Set<Node> visibility, Linearization prefixLin) {
        if (new MonotonicVisibility().vis(visibility, prefixLin)) {
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
