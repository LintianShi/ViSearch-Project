package test;

import datatype.AbstractDataType;
import datatype.RedisRpq;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.MinimalVisSearch;
import validation.MultiThreadSearch;
import validation.SearchConfiguration;
import validation.SearchState;

import java.util.List;

public class TestMultiThreadCheck {
        public static void minimalExtensionRaLinCheck(String output, HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
//        SearchConfiguration configuration1 = new SearchConfiguration(1, -1, 50, 10);
//        configuration1.setAdt(adt);
//        MinimalVisSearch vfs = new MinimalVisSearch(configuration1);
//        vfs.init(happenBeforeGraph);
//        vfs.checkConsistency();
//        List<SearchState> states = vfs.getAllSearchState();

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
//
//        vfs.init(happenBeforeGraph, states.get(41));
//        vfs.checkConsistency();
//        states = vfs.getAllSearchState();
//
//
//
//        List<SearchState> slist = new ArrayList<>();
//        slist.add(states.get(41));
//        slist.add(states.get(51));



//        SearchConfiguration configuration2 = new SearchConfiguration(0, -1, -1, -10);
//        configuration2.setAdt(adt);
//        MultiThreadSearch multiThreadSearch = new MultiThreadSearch(happenBeforeGraph, configuration2);
//        multiThreadSearch.startSearch(states);

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

            //test.TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RedisRpq());
        }
}
