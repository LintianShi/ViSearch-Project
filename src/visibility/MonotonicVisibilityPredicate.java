package visibility;

import history.Linearization;
import history.HBGNode;

import java.util.Set;

public class MonotonicVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        HBGNode node = prefixLin.get(prefixLin.size() - 1);
        Set<HBGNode> hb = getAllHappenBefore(node);
        for (HBGNode n : hb) {
            if (!visibility.containsAll(linVisibility.getNodeVisibility(n))) {
                return false;
            }
        }
        return true;
    }
}
