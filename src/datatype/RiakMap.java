package datatype;

import history.Invocation;
import traceprocessing.Record;
import validation.OperationTypes;

import java.util.HashMap;
import java.util.HashSet;

public class RiakMap extends AbstractDataType {
    private HashMap<Integer, Integer> data = new HashMap<>();

    public String getOperationType(String methodName) {
        if (operationTypes == null) {
            operationTypes = new OperationTypes();
            operationTypes.setOperationType("put", "UPDATE");
            operationTypes.setOperationType("get", "QUERY");
            operationTypes.setOperationType("containsValue", "QUERY");
            return operationTypes.getOperationType(methodName);
        } else {
            return operationTypes.getOperationType(methodName);
        }
    }

    public boolean isRelated(Invocation src, Invocation dest) {
        if (src.getOperationType().equals("UPDATE")) {
            return false;
        } else if (src.getOperationType().equals("QUERY")) {
            if (src.getId() == dest.getId()) {
                return true;
            }
            if (src.getMethodName().equals("get")) {
                Integer key = (Integer) src.getArguments().get(0);
                if (dest.getMethodName().equals("put") && dest.getArguments().get(0).equals(key)) {
                    return true;
                } else {
                    return false;
                }
            } else if (src.getMethodName().equals("containsValue")) {
                Integer value = (Integer) src.getArguments().get(0);
                if (dest.getOperationType().equals("UPDATE") && dest.getArguments().get(1).equals(value)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public Invocation generateInvocation(Record record) {
        Invocation invocation = new Invocation();
        invocation.setRetValue(record.getRetValue());
        invocation.setMethodName(record.getOperationName());
        invocation.setOperationType(getOperationType(record.getOperationName()));

        if (record.getOperationName().equals("put")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
            invocation.addArguments(Integer.parseInt(record.getArgument(1)));
        } else if (record.getOperationName().equals("get")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
        } else if (record.getOperationName().equals("containsValue")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
        } else {
            System.out.println("Unknown operation");
        }
        return invocation;
    }

    public boolean isReadCluster(Invocation invocation) {
        if (invocation.getOperationType().equals("QUERY")) {
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        data = new HashMap<>();
    }

    public void print() {
        System.out.println(data.toString());
    }

    public int hashCode() {
        return data.hashCode();
    }

    public AbstractDataType createInstance() {
        return new RiakSet();
    }

    public String put(Invocation invocation) {
        Integer key = (Integer) invocation.getArguments().get(0);
        Integer value = (Integer) invocation.getArguments().get(1);
        data.put(key, value);
        return "null";
    }

    public String get(Invocation invocation) {
        Integer key = (Integer) invocation.getArguments().get(0);
        Integer value = data.get(key);
        if (value != null) {
            return Integer.toString(value);
        } else {
            return "null";
        }
    }

    public String containsValue(Invocation invocation) {
        Integer value = (Integer) invocation.getArguments().get(0);
        if (data.containsValue(value)) {
            return "true";
        } else {
            return "false";
        }
    }
}
