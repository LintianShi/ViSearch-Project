package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompleteVisibility implements Visibility {
    public Set<Node> vis(Linearization prefixLin) {
        Set<Node> result = new HashSet<>();
        for (int i = 0; i < prefixLin.size(); i++) {
            result.add(prefixLin.get(i));
        }
        return result;
    }
}
