package test;

import datatype.MyHashMap;
import datatype.ORSet;
import datatype.RGA;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import trace.HappenBeforeGraph;
import trace.Invocation;
import trace.Program;
import trace.QueryUpdateExtension;
import validation.MinimalExtension;
import validation.OperationTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TestMinimalRALinCheck {
    public static void main(String[] args) throws Exception {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setOperationType("add", "UPDATE");
        operationTypes.setOperationType("read", "QUERY");
        operationTypes.setOperationType("remove", "QUERYUPDATE");
        operationTypes.setOperationType("readIds", "QUERY");
        operationTypes.setOperationType("rem", "UPDATE");

        QueryUpdateExtension queryUpdateExtension = new QueryUpdateExtension();
        queryUpdateExtension.setMethodMapFunction("remove", new Function<Invocation, Pair<Invocation, Invocation>>() {
            @Override
            public Pair<Invocation, Invocation> apply(Invocation invocation) {
                Invocation invocation1 = new Invocation();
                invocation1.setOperationType("QUERY");
                invocation1.setMethodName("readIds");
                invocation1.setArguments(invocation.getArguments());
                if (invocation.getArguments().get(0).equals("a"))
                    invocation1.setRetValue("readIds:[5=a]");
                if (invocation.getArguments().get(0).equals("b"))
                    invocation1.setRetValue("readIds:[0=b]");
                Invocation invocation2 = new Invocation();
                invocation2.setOperationType("UPDATE");
                invocation2.setMethodName("rem");
                //invocation2.setRetValue(invocation.getRetValue());
                if (invocation.getArguments().get(0).equals("a"))
                    invocation2.setRetValue("rem:[5=a]");
                if (invocation.getArguments().get(0).equals("b"))
                    invocation2.setRetValue("rem:[0=b]");

                return new ImmutablePair<>(invocation1, invocation2);
            }
        });

        HappenBeforeGraph happenBeforeGraph = Program.load("ralin2.json", operationTypes, queryUpdateExtension);

        //happenBeforeGraph.print();

        HashMap<String, Set<String>> results = new HashMap<>();
        new MinimalExtension(happenBeforeGraph).checkConsistency(results, new ORSet());
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
