package generator;

import trace.Invocation;

public interface InvocationGenerator {
    Invocation generate(String methodName);
}
