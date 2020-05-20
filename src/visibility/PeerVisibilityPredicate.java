package visibility;

import trace.HBGNode;
import trace.Linearization;

import java.util.Set;

public class PeerVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        if (new MonotonicVisibilityPredicate().check(visibility, prefixLin, linVisibility)) {
            for (HBGNode n : visibility) {
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
