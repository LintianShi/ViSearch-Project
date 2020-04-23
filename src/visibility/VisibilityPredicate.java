package visibility;

import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.Set;

public abstract class VisibilityPredicate {
    public abstract boolean vis(Set<Node> visibility, Linearization prefixLin);

    protected Set<Node> getAllHappenBefore(Node node) {
        Set<Node> result = new HashSet<>();
        for (Node p : node.getPrevs()) {
            getAllHappenBefore(p, result);
        }
        return result;
    }

    private void getAllHappenBefore(Node node, Set<Node> result) {
        if (node.getPrevs().size() == 0) {
            return;
        } else {
            result.add(node);
            for (Node p : node.getPrevs()) {
                getAllHappenBefore(p, result);
            }
        }
    }
}
