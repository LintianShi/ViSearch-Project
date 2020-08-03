package validation;

import com.alibaba.fastjson.JSON;
import datatype.AbstractDataType;
import datatype.RGA;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import history.*;
import visibility.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Function;

public class Validation {
    private Program program;
    private List<HappenBeforeGraph> happenBeforeGraphs;

    public void loadTrace(String filename) {
        File file = new File(filename);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            String jsonfile = new String(filecontent, "UTF-8");
            program = JSON.parseObject(jsonfile, Program.class);
            program.assignID();
            //happenBeforeGraphs = program.generateHappenBeforeGraphs();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void initializeHappenBeforeGraphs() {
        happenBeforeGraphs = program.generateHappenBeforeGraphs();
    }

    private List<Linearization> generateLinearazations() {
        List<Linearization> lins = new ArrayList<>();
        for (HappenBeforeGraph g : happenBeforeGraphs) {
            lins.addAll(g.generateLins());
        }
        return lins;
    }

    public Set<Behaviour> visibilityRelaxationCheck(Specification specification, AbstractDataType adt) {
        initializeHappenBeforeGraphs();
        HashSet<Behaviour> behaviours = new HashSet<>();
        List<Linearization> lins = generateLinearazations();
        for (Linearization lin : lins) {
            List<LinVisibility> linVisibilities = lin.generateAllNodeVisibility();
            for (LinVisibility l : linVisibilities) {
                if (filter(lin, l, specification)) {
                    //System.out.println(lin.toString());
                    //System.out.println(l.toString());
                    Behaviour behaviour = execute(adt, lin, l);
                    //System.out.println(behaviour);
                    behaviours.add(behaviour);
                }
            }
        }
        return behaviours;
    }

    public Set<Behaviour> RALinCheck(OperationTypes operationTypes, QueryUpdateExtension queryUpdateExtension, AbstractDataType adt) {
        program.extendQueryUpdate(operationTypes, queryUpdateExtension);
        initializeHappenBeforeGraphs();
        HashSet<Behaviour> behaviours = new HashSet<>();
        List<Linearization> lins = generateLinearazations();
        System.out.println(lins.size());
        int sum = 0;
        for (Linearization lin : lins) {
            //System.out.println("sync");
            List<LinVisibility> linVisibilities = lin.generateAllNodeVisibility();
            //System.out.println("ack");
            for (LinVisibility l : linVisibilities) {
                if (filter(lin, l, "CAUSAL")) {
                    sum++;
                    //System.out.println(lin.toString());
                    //System.out.println(l.toString());
                    Behaviour behaviour = crdtExecute(adt, lin, l);
                    //System.out.println(behaviour);
                    behaviours.add(behaviour);
                }
            }
        }
        //System.out.println(sum);
        return behaviours;
    }

    public static Behaviour crdtExecute(AbstractDataType adt, Linearization lin, LinVisibility visibility) {
        Behaviour rets = new Behaviour();
        try {
            for (int i = 0; i < lin.size(); i++) {
                Invocation targetInvocation = lin.get(i).getInvocation();
                if (targetInvocation.getOperationType().equals("UPDATE")) {
                    for (int j = 0; j <= i; j++) {
                        HBGNode node = lin.get(j);
                        if (node.getInvocation().getOperationType().equals("UPDATE")) {
                            String ret = adt.invoke(node.getInvocation());
                            if (i == j) {
                                rets.add(node.getId(), ret);
                            }
                        }
                    }
                } else if (targetInvocation.getOperationType().equals("QUERY")) {
                    Set<HBGNode> vis = visibility.getNodeVisibility(lin.get(i));
                    for (int j = 0; j <= i; j++) {
                        HBGNode node = lin.get(j);
                        if (node.getInvocation().getOperationType().equals("UPDATE") && vis.contains(node)) {
                            adt.invoke(node.getInvocation());
                        } else if (i == j) {
                            String ret = adt.invoke(node.getInvocation());
                            if (ret != null)
                                rets.add(node.getId(), ret);
                        }
                    }
                }
                adt.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rets;
    }

    public static Behaviour execute(AbstractDataType adt, Linearization lin, LinVisibility visibility) {
        Behaviour rets = new Behaviour();
        try {
            for (int i = 0; i < lin.size(); i++) {
                Set<HBGNode> vis = visibility.getNodeVisibility(lin.get(i));
                for (int j = 0; j <= i; j++) {
                    HBGNode node = lin.get(j);
                    if (vis.contains(node)) {
                        String ret = adt.invoke(node.getInvocation());
                        if (i == j) {
                            rets.add(node.getId(), ret);
                        }
                    }
                }
                adt.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rets;
    }

    private boolean filter(Linearization linearization, LinVisibility linVisibility, String specification) {
        for (int i = 0; i < linearization.size(); i++) {
            Linearization prefixLin = linearization.prefix(i);
            Set<HBGNode> vis = linVisibility.getNodeVisibility(linearization.get(i));
            VisibilityPredicate predicate;
            if (specification == null) {
                predicate = new CompleteVisibilityPredicate();
            } else if (specification.equals("COMPLETE")) {
                predicate = new CompleteVisibilityPredicate();
            } else if (specification.equals("CAUSAL")) {
                predicate = new CausalVisibilityPredicate();
            } else if (specification.equals("PEER")) {
                predicate = new PeerVisibilityPredicate();
            } else if (specification.equals("MONOTONIC")) {
                predicate = new MonotonicVisibilityPredicate();
            } else if (specification.equals("BASIC")) {
                predicate = new BasicVisibilityPredicate();
            } else if (specification.equals("WEAK")) {
                predicate = new WeakVisibilityPredicate();
            } else {
                predicate = new CompleteVisibilityPredicate();
            }

            if (!predicate.check(vis, prefixLin, linVisibility)) {
                return false;
            }
        }
        return true;
    }

    private boolean filter(Linearization linearization, LinVisibility linVisibility, Specification specification) {
        for (int i = 0; i < linearization.size(); i++) {
            Linearization prefixLin = linearization.prefix(i);
            Set<HBGNode> vis = linVisibility.getNodeVisibility(linearization.get(i));
            String spec = specification.getSpecification(linearization.get(i).getInvocation().getMethodName());
            VisibilityPredicate predicate;
            if (spec == null) {
                predicate = new CompleteVisibilityPredicate();
            } else if (spec.equals("COMPLETE")) {
                predicate = new CompleteVisibilityPredicate();
            } else if (spec.equals("CAUSAL")) {
                predicate = new CausalVisibilityPredicate();
            } else if (spec.equals("PEER")) {
                predicate = new PeerVisibilityPredicate();
            } else if (spec.equals("MONOTONIC")) {
                predicate = new MonotonicVisibilityPredicate();
            } else if (spec.equals("BASIC")) {
                predicate = new BasicVisibilityPredicate();
            } else if (spec.equals("WEAK")) {
                predicate = new WeakVisibilityPredicate();
            } else {
                predicate = new CompleteVisibilityPredicate();
            }

            if (!predicate.check(vis, prefixLin, linVisibility)) {
                return false;
            }
        }
        return true;
    }

    public void printProgram() {
        System.out.println(program.toString());
    }

    public static void main(String[] args) {
        /*RA-Lin Check with QUERYUPDATE*/
        Validation vv = new Validation();
        vv.loadTrace("ralin2.json");
        OperationTypes operationTypes = new OperationTypes();
        operationTypes.setOperationType("add", "UPDATE");
        operationTypes.setOperationType("read", "QUERY");
        operationTypes.setOperationType("remove", "QUERYUPDATE");
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
        Set<Behaviour> behaviours = vv.RALinCheck(operationTypes, queryUpdateExtension, new RGA());
//        for (Behaviour behaviour : behaviours) {
//            behaviour.printRetTrace();
//            System.out.println();
//        }
    }
}
