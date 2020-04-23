package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class CompleteVisibilityPredicate extends VisibilityPredicate {
    public boolean vis(Set<Node> visibility, Linearization prefixLin) {
        for (int i = 0; i < prefixLin.size(); i++) {
            if (!visibility.contains(prefixLin.get(i))) {
                return false;
            }
        }
        return true;
    }
}
