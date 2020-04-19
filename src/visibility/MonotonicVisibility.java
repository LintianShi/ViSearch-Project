package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonotonicVisibility {
    public Set<Node> vis(Linearization prefixLin) {
        Set<Node> basic = new BasicVisibility().vis(prefixLin);
        Set<Node> result = new HashSet<>();
        for (Node node : basic) {
            result.addAll(node.vis(prefixLin.prefix(node)));
        }
        return result;
    }
}
