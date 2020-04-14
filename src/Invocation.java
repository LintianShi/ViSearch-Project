import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class Invocation {
    @JSONField(name = "METHOD NAME", ordinal = 1)
    private String methodName;
    @JSONField(name = "ARGUMENTS", ordinal = 2)
    private List<Object> arguments = new ArrayList<Object>();

    public Invocation() {
        ;
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

//    @Override
//    public String toString() {
//
//    }

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


