package generator;

import history.Invocation;

public class RGAInvocationGenerator {
    private Invocation generateRead() {
        Invocation invocation = new Invocation();
        invocation.setMethodName("read");
        invocation.setOperationType("QUERY");
        return invocation;
    }
}
