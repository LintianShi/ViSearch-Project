package visibility;

import history.HBGNode;
import history.Linearization;

import java.util.HashSet;
import java.util.Set;

public abstract class VisibilityPredicate {
    public abstract boolean check(Set<HBGNode> visibility, Linearization prefixLin, LinVisibility linVisibility);

    protected Set<HBGNode> getAllHappenBefore(HBGNode node) {
        Set<HBGNode> result = new HashSet<>();
        getAllHappenBefore(node, result);
        return result;
    }

    private void getAllHappenBefore(HBGNode node, Set<HBGNode> result) {
        result.add(node);
        if (node.getPrevs().size() == 0) {
            return;
        } else {
            for (HBGNode p : node.getPrevs()) {
                getAllHappenBefore(p, result);
            }
        }
    }
}
