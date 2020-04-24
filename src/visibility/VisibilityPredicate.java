package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public abstract class VisibilityPredicate {
    public abstract boolean check(Set<Node> visibility, Linearization prefixLin, LinVisibility linVisibility);

    protected Set<Node> getAllHappenBefore(Node node) {
        Set<Node> result = new HashSet<>();
        getAllHappenBefore(node, result);
        return result;
    }

    private void getAllHappenBefore(Node node, Set<Node> result) {
        result.add(node);
        if (node.getPrevs().size() == 0) {
            return;
        } else {
            for (Node p : node.getPrevs()) {
                getAllHappenBefore(p, result);
            }
        }
    }
}
