package validation;

import java.util.HashMap;

public class Specification {
    private HashMap<String, String> specifications = new HashMap<>();

    public String getSpecification(String methodName) {
        return specifications.get(methodName);
    }

    public void setSpecification(String methodName, String vis) {
        specifications.put(methodName, vis);
    }
}
