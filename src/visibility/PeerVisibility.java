package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeerVisibility implements Visibility {
    public Set<Node> vis(Linearization prefixLin) {
        Set<Node> monotonic = new MonotonicVisibility().vis(prefixLin);
        Set<Node> result = new HashSet<>();
        for (Node node : monotonic) {
            result.addAll(node.vis(prefixLin.prefix(node)));
        }
        return result;
    }
}
