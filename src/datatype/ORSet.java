package datatype;

import history.Invocation;
import rawtrace.CrdtOperation;

import java.util.*;

public class ORSet extends AbstractDataType {
    HashMap<Integer, String> data = new HashMap<>();

    HashMap<Integer, Set<Integer>> queryUpdateCache = new HashMap<>();

    public String add(Invocation invocation) {
        data.put(invocation.getId(), (String)invocation.getArguments().get(0));
        //return "{" + Integer.toString(invocation.getId()) + ", " + (String)invocation.getArguments().get(0) + "}";
        //System.out.println("add-" + data.toString());
        //return (String)invocation.getArguments().get(0) + ":" + Integer.toString(invocation.getId());
        return invocation.getRetValue();
    }

    public String readIds(Invocation invocation) {
        Set<Integer> set = new HashSet<>();
        String arg = (String)invocation.getArguments().get(0);
        ArrayList<Map.Entry<Integer, String>> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            if (entry.getValue().equals(arg)) {
                set.add(entry.getKey());
                result.add(entry);
            }
        }
        queryUpdateCache.put(invocation.getId() + 1, set);
        //System.out.println("readIds-" + data.toString());
        //return "readIds:" + result.toString();
        return invocation.getRetValue();
    }

    public String rem(Invocation invocation) {
        Set<Integer> arg = queryUpdateCache.get(invocation.getId());
        ArrayList<Map.Entry<Integer, String>> result = new ArrayList<>();
        for (Integer i : arg) {
            result.add(new AbstractMap.SimpleEntry<>(i, data.get(i)));
            data.remove(i);
        }
        //System.out.println("remove-" + data.toString());
        //return "rem:" + result.toString();
        return invocation.getRetValue();
    }

    public String read(Invocation invocation) {
        Set<Map.Entry<Integer, String>> result = new HashSet<>();
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            result.add(entry);
        }
        //System.out.println("read-" + data.toString());
        return data.toString();
    }

    public Invocation transformCrdtOperation(CrdtOperation crdtOperation) {
        return null;
    }

    @Override
    public void print() {
        System.out.println("print: " + data.toString());
    }

    @Override
    public void reset() {
        data = new HashMap<>();
    }
}
