package history;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class Invocation {
    @JSONField(name = "METHOD NAME", ordinal = 1)
    private String methodName;
    @JSONField(name = "ARGUMENTS", ordinal = 2)
    private List<Object> arguments = new ArrayList<Object>();
    @JSONField(name = "RETVALUE", ordinal = 3)
    private String retValue;

    private int id;
    @JSONField(serialize=false)
    private Pair<Integer, Integer> pairID;
    @JSONField(serialize=false)
    private String operationType = "UPDATE";
    @JSONField(serialize=false)
    public transient static HashMap<String, String> visibility = new HashMap<>();

    public Invocation() {
        ;
    }

    public void setRetValue(String retValue) {
        this.retValue = retValue;
    }

    public String getRetValue() {
        return retValue;
    }

    public void setMethodName(String name) {
        methodName = name;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    public void addArguments(Object object) {
        arguments.add(object);
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pair<Integer, Integer> getPairID() {
        return pairID;
    }

    public void setPairID(Pair<Integer, Integer> pairID) {
        this.pairID = pairID;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return methodName + ":" + arguments.toString() + ":" + retValue;
    }

    public static void main(String[] args) {
        Invocation invocation = new Invocation();
        invocation.setMethodName("put");
        invocation.addArguments((Object)"key");
        invocation.addArguments((Object)1);
        System.out.println(JSON.toJSONString(invocation));

        Invocation newinvocation = JSON.parseObject("{\"METHOD NAME\":\"put\",\"ARGUMENTS\":[\"key\",1]}", Invocation.class);
        System.out.println(newinvocation.getMethodName());
        System.out.println(newinvocation.getArguments());
    }
}


