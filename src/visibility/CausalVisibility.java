package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CausalVisibility implements Visibility {
    public Set<Node> vis(Linearization prefixLin) {
        Set<Node> basic = new BasicVisibility().vis(prefixLin);
        int size = 0;
        while (size < basic.size()) {
            Set<Node> temp = new HashSet<>();
            for (Node node : basic) {
                temp.addAll(node.vis(prefixLin.prefix(node)));
            }
            size = basic.size();
            basic = temp;
        }
        return basic;
    }
}
