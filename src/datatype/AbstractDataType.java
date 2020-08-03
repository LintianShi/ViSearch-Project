package datatype;

import history.Invocation;

import java.lang.reflect.Method;

public abstract class AbstractDataType {
    public final String invoke(Invocation invocation) throws Exception {
        String methodName = invocation.getMethodName();
        Class clazz = this.getClass();
        Method method = clazz.getDeclaredMethod(methodName, Invocation.class);
        method.setAccessible(true);
        return (String)method.invoke(this, invocation);
    }

    public abstract void reset();

    public abstract void print();
}
