package datatype;

import history.Invocation;
import traceprocessing.Record;
import validation.OperationTypes;

import java.util.LinkedList;
import java.util.List;

public class RedisList extends AbstractDataType {
    private List<ListElement> data = new LinkedList<>();

    private String insert(Invocation invocation) {
        String prevId = (String) invocation.getArguments().get(0);
        String newId = (String) invocation.getArguments().get(1);
        String value = (String) invocation.getArguments().get(2);
        ListElement element = new ListElement(newId, value);
        if (prevId.equals("null")) {
            data.add(0, element);
        } else {
            int index = indexOfKey(prevId);
            if (index != -1) {
                data.add(index + 1, element);
            }
        }
        return "null";
    }

    private String remove(Invocation invocation) {
        String prevId = (String) invocation.getArguments().get(0);
        int index = indexOfKey(prevId);
        if (index != -1) {
            data.remove(index);
        }
        return "null";
    }

    private String get(Invocation invocation) {
        Integer index = (Integer) invocation.getArguments().get(0);
        if (index >= 0 && index < data.size()) {
            ListElement element = data.get(index);
            return element.getId() + ":" + element.getVal();
        }
        return "null";
    }

    private int indexOfKey(String id) {
        for (int i = 0; i < data.size(); i++) {
            ListElement element = data.get(i);
            if (element.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isRelated(Invocation src, Invocation dest) {
        return false;
    }

    public Invocation generateInvocation(Record record) {
        Invocation invocation = new Invocation();
        invocation.setRetValue(record.getRetValue());
        invocation.setMethodName(record.getOperationName());
        invocation.setOperationType(getOperationType(record.getOperationName()));

        if (record.getOperationName().equals("insert")) {
            invocation.addArguments(record.getArgument(0));
            invocation.addArguments(record.getArgument(1));
            invocation.addArguments(record.getArgument(2));
        } else if (record.getOperationName().equals("remove")) {
            invocation.addArguments(record.getArgument(0));
        } else if (record.getOperationName().equals("get")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
        } else {
            System.out.println("Unknown operation");
        }
        return invocation;
    }

    public AbstractDataType createInstance() {
        return new RedisList();
    }

    public String getOperationType(String methodName) {
        if (operationTypes == null) {
            operationTypes = new OperationTypes();
            operationTypes.setOperationType("insert", "UPDATE");
            operationTypes.setOperationType("remove", "UPDATE");
            operationTypes.setOperationType("get", "QUERY");
            return operationTypes.getOperationType(methodName);
        } else {
            return operationTypes.getOperationType(methodName);
        }
    }

    public void reset() {
        data = new LinkedList<>();
    }

    public void print() {
        System.out.println(data.toString());
    }

    public int hashCode() {
        return data.hashCode();
    }
}

class ListElement {
    String id;
    String val;
    public ListElement(String id, String val) {
        this.id = id;
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public String getVal() {
        return val;
    }
}
