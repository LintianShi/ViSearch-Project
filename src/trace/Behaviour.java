package trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Behaviour {
    private HashMap<Integer, String> retValues = new HashMap<>();
    private ArrayList<String> retTrace = new ArrayList<>();

    public void add(Integer id, String retValue) {
        retValues.put(id, retValue);
        retTrace.add(retValue);
    }

    public int size() {
        return retValues.size();
    }

    public String getRetValue(Integer id) {
        return retValues.get(id);
    }

    public void printRetTrace() {
        System.out.print("{");
        for (String s : retTrace) {
            System.out.print(s + ", ");
        }
        System.out.print("}");
    }

    @Override
    public String toString() {
        return retTrace.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }
}
