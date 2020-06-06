package datatype;

import trace.Invocation;

import java.util.HashMap;

public class MyHashMap extends AbstractDataType {
    HashMap<Integer, Integer> data = new HashMap<>();

    private String put(Invocation invocation) {
        Integer key = (Integer) invocation.getArguments().get(0);
        Integer value = (Integer) invocation.getArguments().get(1);
        Integer ret = data.put(key, value);
        if (ret == null) {
            return "null";
        } else {
            return Integer.toString(ret);
        }
    }

    private String contains(Invocation invocation) {
        boolean result = data.containsValue(invocation.getArguments().get(0));
        if (result) {
            return "true";
        } else {
            return "false";
        }
    }

    private String get(Invocation invocation) {
        Integer key = (Integer) invocation.getArguments().get(0);
        Integer ret = data.get(key);
        if (ret == null) {
            return "null";
        } else {
            return Integer.toString(ret);
        }
    }

    @Override
    public void reset() {
        data = new HashMap<>();
    }
}
