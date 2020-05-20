package visibility;

import trace.HBGNode;
import trace.Linearization;

import java.util.Set;

public class WeakVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        return true;
    }
}
