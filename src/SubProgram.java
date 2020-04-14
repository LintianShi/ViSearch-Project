import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

class SubProgram {
    @JSONField(name = "INVOCATIONS")
    private List<Invocation> invocations = new ArrayList<>();

    public SubProgram() {
        ;
    }

    public void setInvocations(List<Invocation> invocations) {
        this.invocations = invocations;
    }

    public List<Invocation> getInvocations() {
        return invocations;
    }

    public void addInvocation(Invocation invocation) {
        invocations.add(invocation);
    }

    public Invocation get(int i) {
        if (i >= 0 && i < invocations.size())
            return invocations.get(i);
        else
            return null;
    }

    public int size() {
        return invocations.size();
    }
}