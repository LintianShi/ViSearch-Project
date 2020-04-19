package visibility;

import trace.Invocation;
import trace.Linearization;
import trace.Node;
import java.util.List;
import java.util.Set;

public interface Visibility {
    public Set<Node> vis(Linearization prefixLin);
}
