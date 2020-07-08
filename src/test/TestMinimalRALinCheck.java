package test;

import datatype.MyHashMap;
import datatype.RGA;
import trace.HappenBeforeGraph;
import trace.Program;
import validation.MinimalExtension;
import validation.OperationTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestMinimalRALinCheck {
    public static void main(String[] args) throws Exception {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setOperationType("addAfter", "QUERY");
        operationTypes.setOperationType("read", "QUERY");
        HappenBeforeGraph happenBeforeGraph = Program.load("ralin1.json", operationTypes);

        HashMap<String, Set<String>> results = new HashMap<>();
        new MinimalExtension(happenBeforeGraph).checkConsistency(results, new RGA());
        System.out.println(results.size());
        for (Map.Entry<String, Set<String>> entry: results.entrySet()) {
            System.out.println(entry.getKey().toString());
            for (String vis : entry.getValue()) {
                System.out.println(vis);
            }
            System.out.println();
        }
    }
}
