package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicVisibility implements Visibility {
    public Set<Node> vis(Linearization prefixLin) {
        Node node = prefixLin.get(prefixLin.size() - 1);
        Set<Node> result = allHappenBefore(node);
        return result;
    }

    private Set<Node> allHappenBefore(Node node) {
        Set<Node> result = new HashSet<>();
        for (Node prev : node.getPrevs()) {
            result.addAll(allHappenBefore(prev));
        }
        result.add(node);
        return result;
    }
}
