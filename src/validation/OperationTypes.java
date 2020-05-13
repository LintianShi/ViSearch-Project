package validation;

import java.util.HashMap;

public class OperationTypes {
    private HashMap<String, String> operationTypes = new HashMap<>();

    public String getOperationType(String methodName) {
        return operationTypes.get(methodName);
    }

    public void setOperationType(String methodName, String operationType) {
        operationTypes.put(methodName, operationType);
    }
}
