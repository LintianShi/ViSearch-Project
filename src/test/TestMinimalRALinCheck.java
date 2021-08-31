package test;

import datatype.*;
import history.*;
import traceprocessing.RawTraceProcessor;
import validation.*;

public class TestMinimalRALinCheck {
    public static void minimalExtensionRaLinCheck(String output, HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
//        SearchConfiguration configuration1 = new SearchConfiguration(1, -1, 50, 10);
//        configuration1.setAdt(adt);
//        MinimalVisSearch vfs = new MinimalVisSearch(configuration1);
//        vfs.init(happenBeforeGraph);
//        vfs.checkConsistency();
//        List<SearchState> states = vfs.getAllSearchState();
//
//        int i = 0;
//        for (SearchState state : states) {
//            System.out.println(Integer.toString(i) + ":" + state.toString());
//            i++;
//        }
//
//
//        vfs.init(happenBeforeGraph, states.get(38));
//        vfs.checkConsistency();
//        states = vfs.getAllSearchState();
//
//        vfs.init(happenBeforeGraph, states.get(35));
//        vfs.checkConsistency();
//        states = vfs.getAllSearchState();

//        vfs.init(happenBeforeGraph, states.get(41));
//        vfs.checkConsistency();
//        states = vfs.getAllSearchState();



//        List<SearchState> slist = new ArrayList<>();
//        slist.add(states.get(41));
//        slist.add(states.get(51));



//        SearchConfiguration configuration2 = new SearchConfiguration(0, -1, -1, -10);
//        configuration2.setAdt(adt);
//        MultiThreadSearch multiThreadSearch = new MultiThreadSearch(happenBeforeGraph, configuration2);
//        multiThreadSearch.startSearch(slist);


        SearchConfiguration configuration1 = new SearchConfiguration(0, -1, -1, 0);
        configuration1.setAdt(adt);
        configuration1.setEnablePrickOperation(true);
        configuration1.setEnableOutputSchedule(true);
        MinimalVisSearch vfs = new MinimalVisSearch(configuration1);
        vfs.init(happenBeforeGraph);
        vfs.checkConsistency();
//        for (SearchState s : states) {
//            System.out.println(s.toString());
//        }
//        Pair<Linearization, LinVisibility> result = vfs.getResult();
//        try {
//            FileWriter fileWriter = new FileWriter(new File(output));
//            fileWriter.write("====================Linearization====================\n");
//            //System.out.println("====================Linearization====================");
//            for (HBGNode node : result.getLeft()) {
//                fileWriter.write(node.getInvocation().getRetValue() + ", ");
//            }
//            fileWriter.write("\n");
//
//            fileWriter.write("====================Visibility====================\n");
//            for (HBGNode node : result.getLeft()) {
//                fileWriter.write("#" + node.getInvocation().getRetValue() + " => { ");
//                for (HBGNode visNode : result.getLeft().prefix(node)) {
//                    if (result.getRight().getNodeVisibility(node).contains(visNode)) {
//                        fileWriter.write(visNode.getInvocation().getRetValue() + ", ");
//                    }
//                }
//                fileWriter.write(" }\n");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) throws Exception {
        RawTraceProcessor rp = new RawTraceProcessor();
        rp.load("trace");
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(new RedisRpq()).generateHappenBeforeGraph();
        new HBGPreprocessor().preprocess(happenBeforeGraph, new RedisRpq());

        //happenBeforeGraph.printStartNodes();

        TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RedisRpq());
        // two clients 240/520/60/500/60
        // 25 clients 160/150/160
        // 35 clients 140/120/150
        // 45 clients 140/140/160
    }
}
