package validation;

import arbitration.VisibilityType;
import datatype.AbstractDataType;
import datatype.RRpq;
import history.HBGNode;
import history.HappenBeforeGraph;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HBGPreprocessor {
    public static List<ImmutablePair<Integer, Integer>> extractCommonHBRelation(List<List<ImmutablePair<Integer, Integer>>> hbs) {
        List<ImmutablePair<Integer, Integer>> results = new ArrayList<>();
        HashMap<ImmutablePair<Integer, Integer>, Integer> map = new HashMap<>();
        for (List<ImmutablePair<Integer, Integer>> list : hbs) {
            for (ImmutablePair<Integer, Integer> hb : list) {
                if (!map.containsKey(hb)) {
                    map.put(hb, 1);
                } else {
                    int count = map.get(hb);
                    map.put(hb, count + 1);
                }

            }
        }

        for (ImmutablePair<Integer, Integer> hb : map.keySet()) {
            if (map.get(hb) == hbs.size()) {
                results.add(hb);
            }
        }
        return results;
    }

    private void addHBRelations(HappenBeforeGraph happenBeforeGraph, List<ImmutablePair<Integer, Integer>> hbs) {
        for (ImmutablePair<Integer, Integer> hb : hbs) {
            HBGNode prev = happenBeforeGraph.get(hb.left);
            HBGNode next = happenBeforeGraph.get(hb.right);
            prev.addNextNode(next);
            next.addPrevNode(prev);
        }
    }

    public void preprocess(HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
        for (HBGNode node : happenBeforeGraph) {
            if (node.getInvocation().getMethodName().equals("rwfzscore")
            || (node.getInvocation().getMethodName().equals("rwfzmax") && node.getInvocation().getRetValue().equals("null") )) {
                List<List<HBGNode>> relatedNodes = happenBeforeGraph.getRelatedOperation(node, adt);
//                for (List<HBGNode> list : relatedNodes) {
//                    System.out.println(list);
//                }
                System.out.println(node.toString());

                HappenBeforeGraph subHBGraph = new HappenBeforeGraph(relatedNodes);
                SearchConfiguration configuration = new SearchConfiguration(0, -1, -1, 0);
                configuration.setAdt(new RRpq());
                configuration.setFindAllAbstractExecution(true);
                configuration.setEnablePrickOperation(false);
                configuration.setVisibilityType(VisibilityType.COMPLETE);
                configuration.setEnableOutputSchedule(false);
                MinimalVisSearch subSearch = new MinimalVisSearch(configuration);
                subSearch.init(subHBGraph);
                subSearch.checkConsistency();

                List<List<ImmutablePair<Integer, Integer>>> hbs = new ArrayList<>();

                List<ImmutablePair<Integer, Integer>> commonHBs = extractCommonHBRelation(hbs);
                addHBRelations(happenBeforeGraph, commonHBs);
//                for (ImmutablePair<Integer, Integer> hb : commonHBs) {
//                    System.out.println(happenBeforeGraph.get(hb.left).toString()
//                            + "=>" + happenBeforeGraph.get(hb.right).toString());
//                }
            }
        }
    }
}
