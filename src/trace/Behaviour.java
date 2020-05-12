package trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Behaviour {
    private HashMap<Integer, String> retValues = new HashMap<>();

    public void add(Integer id, String retValue) {
        retValues.put(id, retValue);
    }

    public int size() {
        return retValues.size();
    }

    public String getRetValue(Integer id) {
        return retValues.get(id);
    }

    @Override
    public String toString() {
        return retValues.toString();
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
