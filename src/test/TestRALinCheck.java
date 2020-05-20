package test;

import com.alibaba.fastjson.JSON;
import datatype.ORSet;
import datatype.RGA;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import trace.*;
import validation.OperationTypes;
import validation.Validation;
import visibility.LinVisibility;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TestRALinCheck {
    public void checkWithQueryUpdate() throws Exception {
        Validation vv = new Validation();
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
                Invocation invocation2 = new Invocation();
                invocation2.setOperationType("UPDATE");
                invocation2.setMethodName("rem");

                return new ImmutablePair<>(invocation1, invocation2);
            }
        });

        File filename = new File("ralin2.json");
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        Program program = JSON.parseObject(jsonfile, Program.class);
        program.assignID();
        program.extendQueryUpdate(operationTypes, queryUpdateExtension);
        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();

        List<Linearization> linearizations = list.get(0).generateLins();
//        int index = -1;
//        int[] order = {0, 1, 5, 6, 2, 3, 7, 8, 4, 9};
//        for (Linearization linearization : linearizations) {
//            boolean flag = true;
//            for (int  i = 0; i < linearization.size(); i++) {
//                if (linearization.get(i).getId() != order[i]) {
//                    flag = false;
//                    break;
//                }
//            }
//            if (flag)
//                index = linearizations.indexOf(linearization);
//        }

        System.out.println(linearizations.get(9));
        Linearization linearization = linearizations.get(9);
        LinVisibility visibility = new LinVisibility();

        HashSet<HBGNode> temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(4));
        visibility.updateNodeVisibility(linearization.get(4), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(6));
        visibility.updateNodeVisibility(linearization.get(6), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(8));
        visibility.updateNodeVisibility(linearization.get(8), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(9));
        visibility.updateNodeVisibility(linearization.get(9), temp);

        Behaviour behaviour = vv.crdtExecute(new ORSet(), linearization, visibility);
        System.out.println("===========================Not Complete Visible============================");
        behaviour.printRetTrace();
        System.out.println();

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        visibility.updateNodeVisibility(linearization.get(4), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(6));
        visibility.updateNodeVisibility(linearization.get(6), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(8));
        visibility.updateNodeVisibility(linearization.get(8), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(9));
        visibility.updateNodeVisibility(linearization.get(9), temp);

        behaviour = vv.crdtExecute(new ORSet(), linearization, visibility);
        System.out.println("===========================Complete Visible============================");
        behaviour.printRetTrace();
        System.out.println();

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        visibility.updateNodeVisibility(linearization.get(4), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(6));
        visibility.updateNodeVisibility(linearization.get(6), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(8));
        visibility.updateNodeVisibility(linearization.get(8), temp);

        temp = new HashSet<>();
        temp.add(linearization.get(0));
        temp.add(linearization.get(1));
        temp.add(linearization.get(2));
        temp.add(linearization.get(3));
        temp.add(linearization.get(4));
        temp.add(linearization.get(5));
        temp.add(linearization.get(6));
        temp.add(linearization.get(7));
        temp.add(linearization.get(9));
        visibility.updateNodeVisibility(linearization.get(9), temp);

        behaviour = vv.crdtExecute(new ORSet(), linearization, visibility);
        System.out.println("===========================Half Visible============================");
        behaviour.printRetTrace();
        System.out.println();
    }

    public void checkWithoutQueryUpdate() {
        /*RA-Lin Check without QUERYUPDATE*/
        Validation vv = new Validation();
        vv.loadTrace("ralin1.json");
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setOperationType("addAfter", "UPDATE");
        operationTypes.setOperationType("read", "QUERY");
        QueryUpdateExtension queryUpdateExtension = new QueryUpdateExtension();
        Set<Behaviour> behaviours = vv.RALinCheck(operationTypes, queryUpdateExtension, new RGA());
        for (Behaviour behaviour : behaviours) {
            behaviour.printRetTrace();
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        TestRALinCheck test = new TestRALinCheck();
        //test.checkWithoutQueryUpdate();
        test.checkWithQueryUpdate();
    }
}
