package history;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.function.Function;

public class QueryUpdateExtension {
    private HashMap<String, Function<Invocation, Pair<Invocation, Invocation>>> map = new HashMap<>();

    public void setMethodMapFunction(String methodName, Function<Invocation, Pair<Invocation, Invocation>> mapFunction) {
        map.put(methodName, mapFunction);
    }

    public Function<Invocation, Pair<Invocation, Invocation>> getMethodMapFunction(String methodName) {
        return map.get(methodName);
    }
}
