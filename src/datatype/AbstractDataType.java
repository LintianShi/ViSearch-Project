package datatype;

import history.Invocation;
import traceprocessing.Record;
import validation.OperationTypes;

import java.lang.reflect.Method;

public abstract class AbstractDataType {
    protected OperationTypes operationTypes = null;

    public final String invoke(Invocation invocation) throws Exception {
        String methodName = invocation.getMethodName();
        Class clazz = this.getClass();
        Method method = clazz.getDeclaredMethod(methodName, Invocation.class);
        method.setAccessible(true);
//        System.out.println(invocation.getMethodName());
//        System.out.println(invocation.getRetValue());
//        for (int i = 0; i < invocation.getArguments().size(); i++) {
//            System.out.println((String)invocation.getArguments().get(i));
//        }
        return (String)method.invoke(this, invocation);
    }

    public abstract boolean isRelated(Invocation src, Invocation dest);

    public abstract boolean isReadCluster(Invocation invocation);

    public abstract void reset();

    public abstract void print();

    public abstract int hashCode();

    public abstract Invocation generateInvocation(Record record);

    public abstract AbstractDataType createInstance();

    public abstract String getOperationType(String methodName);
}
