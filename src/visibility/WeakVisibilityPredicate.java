package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public class WeakVisibilityPredicate extends VisibilityPredicate {
    public boolean vis(Set<Node> visibility, Linearization prefixLin) {
        return true;
    }
}
