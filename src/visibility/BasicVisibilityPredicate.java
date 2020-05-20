package visibility;

import trace.HBGNode;
import trace.Linearization;

import java.util.Set;

public class BasicVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        HBGNode node = prefixLin.get(prefixLin.size() - 1);
        Set<HBGNode> hb = getAllHappenBefore(node);
        if(visibility.containsAll(hb)) {
            return true;
        } else {
            return false;
        }
    }
}
