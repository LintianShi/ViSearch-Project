package generator;

import datatype.AbstractDataType;
import trace.Invocation;

import java.util.ArrayList;
import java.util.List;

public class GeneratorSetting {
    protected AbstractDataType adt;
    protected List<InvocationGenerator> invocationGenerators = new ArrayList<>();

    public GeneratorSetting(AbstractDataType adt) {
        this.adt = adt;
    }

    public AbstractDataType getAdt() {
        return adt;
    }

    public void addInvocationGenerator(InvocationGenerator invocationGenerator) {
        invocationGenerators.add(invocationGenerator);
    }

    public InvocationGenerator getInvocationGenerator(int i) {
        return invocationGenerators.get(i);
    }
}
