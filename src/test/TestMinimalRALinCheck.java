package test;

import datatype.*;
import history.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import validation.MinimalExtension;
import validation.OperationTypes;
import visibility.LinVisibility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TestMinimalRALinCheck {
    public static void minimalExtensionRaLinCheck(HappenBeforeGraph happenBeforeGraph, OperationTypes operationTypes,
                                                                      QueryUpdateExtension queryUpdateExtension, AbstractDataType adt) {
        MinimalExtension minimalExtension = new MinimalExtension(happenBeforeGraph);
        minimalExtension.checkConsistency(adt);
        Pair<Linearization, LinVisibility> result = minimalExtension.getResult();
        System.out.println("====================Linearization====================");
        for (HBGNode node : result.getLeft()) {
            System.out.print(node.getInvocation().getRetValue() + ", ");
        }
        System.out.println();

        System.out.println("====================Visibility====================");
        for (HBGNode node : result.getLeft()) {
            System.out.print("#" + node.getInvocation().getRetValue() + " => { ");
            for (HBGNode visNode : result.getLeft().prefix(node)) {
                if (result.getRight().getNodeVisibility(node).contains(visNode)) {
                    System.out.print(visNode.getInvocation().getRetValue() + ", ");
                }
            }
            System.out.println(" }");
        }
    }

    public static void testORSet() throws Exception {
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
//                if (invocation.getArguments().get(0).equals("a"))
//                    invocation1.setRetValue("readIds:[5=a]");
//                if (invocation.getArguments().get(0).equals("b"))
//                    invocation1.setRetValue("readIds:[0=b]");
                invocation1.setRetValue("null");
                Invocation invocation2 = new Invocation();
                invocation2.setOperationType("UPDATE");
                invocation2.setMethodName("rem");
                invocation2.setRetValue("null");
//                if (invocation.getArguments().get(0).equals("a"))
//                    invocation2.setRetValue("rem:[5=a]");
//                if (invocation.getArguments().get(0).equals("b"))
//                    invocation2.setRetValue("rem:[0=b]");

                return new ImmutablePair<>(invocation1, invocation2);
            }
        });

        HappenBeforeGraph happenBeforeGraph = Program.load("ralin3-false.json", operationTypes, queryUpdateExtension);

        minimalExtensionRaLinCheck(happenBeforeGraph,operationTypes,queryUpdateExtension, new ORSet());
    }

    public static void testSimpleCounterExample() throws Exception {
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setOperationType("add", "UPDATE");
        operationTypes.setOperationType("read", "QUERY");
        operationTypes.setOperationType("remove", "UPDATE");
        HappenBeforeGraph happenBeforeGraph = Program.load("ralin3-false.json", operationTypes, null);

        minimalExtensionRaLinCheck(happenBeforeGraph,operationTypes,null, new SimpleSet());
    }


    public static void main(String[] args) throws Exception {
        //testORSet();
        testSimpleCounterExample();
    }
}
