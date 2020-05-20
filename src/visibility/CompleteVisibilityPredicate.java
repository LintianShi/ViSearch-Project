package visibility;

import trace.HBGNode;
import trace.Linearization;

import java.util.Set;

public class CompleteVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        if (prefixLin.size() != visibility.size()) {
            return false;
        }
        for (int i = 0; i < prefixLin.size(); i++) {
            if (!visibility.contains(prefixLin.get(i))) {
                return false;
            }
        }
        return true;
    }
}
