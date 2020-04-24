package visibility;

import trace.Linearization;
import trace.Node;

import java.util.Set;

public class BasicVisibilityPredicate extends VisibilityPredicate {
    public boolean check(Set<Node> visibility, Linearization prefixLin, LinVisibility linVisibility) {
        Node node = prefixLin.get(prefixLin.size() - 1);
        Set<Node> hb = getAllHappenBefore(node);
        if(visibility.containsAll(hb)) {
            return true;
        } else {
            return false;
        }
    }
}
