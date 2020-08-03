package generator;

import history.Invocation;

public interface InvocationGenerator {
    Invocation generate(String methodName);
}
