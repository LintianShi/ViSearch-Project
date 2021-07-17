package validation;

import arbitration.VisibilityType;
import com.google.common.collect.Multimap;
import datatype.AbstractDataType;
import datatype.RRpq;
import history.HBGNode;
import history.HappenBeforeGraph;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.collect.HashMultimap;
import util.Pair;
import util.PairOfPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HBGPreprocessor {
    private List<Pair> extractCommonHBRelation(List<List<Pair>> hbs) {
        List<Pair> results = new ArrayList<>();
        HashMap<Pair, Integer> map = new HashMap<>();
        for (List<Pair> list : hbs) {
            for (Pair hb : list) {
                if (!map.containsKey(hb)) {
                    map.put(hb, 1);
                } else {
                    int count = map.get(hb);
                    map.put(hb, count + 1);
                }

            }
        }

        for (Pair hb : map.keySet()) {
            if (map.get(hb) == hbs.size()) {
                results.add(hb);
            }
        }
        return results;
    }

    private List<PairOfPair> removeCommonRelations(List<PairOfPair> incompatibleRelations, List<Pair> commonRelations) {
        List<PairOfPair> cleanIncompatibleRelations = new ArrayList<>();
        for (PairOfPair pairOfPair : incompatibleRelations) {
            boolean flag = true;
            for (Pair common : commonRelations) {
                ImmutablePair<Integer, Integer> reversePair = new ImmutablePair<>(common.getRight(), common.getLeft());
                if (pairOfPair.getLeft().equals(reversePair) || pairOfPair.getRight().equals(reversePair)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                cleanIncompatibleRelations.add(pairOfPair);
            }
        }
        return cleanIncompatibleRelations;
    }

    private List<PairOfPair> extractIncompatibleHBRelation(List<List<Pair>> hbs, List<List<HBGNode>> relatedNodes) {
        HashSet<PairOfPair> pairSet = new HashSet<>();
        for (List<Pair> list : hbs) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                        PairOfPair pairOfPair = new PairOfPair(list.get(i), list.get(j));
                        pairSet.add(pairOfPair);
                }
            }
        }

        List<Pair> allPairs = new ArrayList<>();
        for (int i = 0; i < relatedNodes.size(); i++) {
            for (int j = i + 1; j < relatedNodes.size(); j++) {
                for (int k = 0; k < relatedNodes.get(i).size(); k++) {
                    for (int h = 0; h < relatedNodes.get(j).size(); h++) {
                        allPairs.add(new Pair(relatedNodes.get(j).get(h).getId(), relatedNodes.get(i).get(k).getId()));
                        allPairs.add(new Pair(relatedNodes.get(i).get(k).getId(), relatedNodes.get(j).get(h).getId()));
                    }
                }
            }
        }

        List<PairOfPair> incompatiblePair = new ArrayList<>();

        for (int i = 0; i < allPairs.size(); i++) {
            for (int j = i + 1; j < allPairs.size(); j++) {
                if (i != j && (i % 2 != 0 && i + 1 != j)) {
                    PairOfPair pairOfPair = new PairOfPair(allPairs.get(i), allPairs.get(j));
                    if (!pairSet.contains(pairOfPair)) {
                        incompatiblePair.add(pairOfPair);
                    }
                }
            }
        }

        return incompatiblePair;
    }

    private void addHBRelations(HappenBeforeGraph happenBeforeGraph, List<Pair> hbs) {
        for (Pair hb : hbs) {
            HBGNode prev = happenBeforeGraph.get(hb.getLeft());
            HBGNode next = happenBeforeGraph.get(hb.getRight());
            prev.addNextNode(next);
            next.addPrevNode(prev);
        }
    }

    private Multimap<Pair, Pair> generateRuleTable(List<PairOfPair> incompatibleRelations) {
        Multimap<Pair, Pair> ruleTable = HashMultimap.create();
        for (PairOfPair pairOfPair : incompatibleRelations) {
            ruleTable.put(pairOfPair.getLeft(), pairOfPair.getRight());
            ruleTable.put(pairOfPair.getRight(), pairOfPair.getLeft());
        }
        return ruleTable;
    }

    public void preprocess(HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
        for (HBGNode node : happenBeforeGraph) {
            if (node.getInvocation().getMethodName().equals("rwfzscore")
            || (node.getInvocation().getMethodName().equals("rwfzmax") && !node.getInvocation().getRetValue().equals("null") )) {
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
                if (subSearch.getResults().size() == 0) {
                    System.out.println("no abstract execution");
                    continue;
                }



                List<List<Pair>> hbs = new ArrayList<>();
                for (SearchState state1 : subSearch.getResults()) {
                    hbs.add(state1.extractHBRelation());
                }

                List<Pair> commonHBs = extractCommonHBRelation(hbs);
                List<PairOfPair> incompatibleRelations = removeCommonRelations(extractIncompatibleHBRelation(hbs, relatedNodes), commonHBs);
                addHBRelations(happenBeforeGraph, commonHBs);
                Multimap<Pair, Pair> ruleTable = generateRuleTable(incompatibleRelations);
                happenBeforeGraph.setRuleTable(ruleTable);

                if (node.getId() == 436) {
                    for (List<HBGNode> list : relatedNodes) {
                        for (HBGNode op : list) {
                            System.out.print(op.toString() + " => ");
                        }
                        System.out.println();
                    }
//                    System.out.println();
//                    for (SearchState searchState : subSearch.getResults()) {
//                        System.out.println(searchState);
//                    }

//                    for (SearchState ss : subSearch.getResults()) {
//                        System.out.println(ss);
//                    }
//                    System.out.println();
//                    for (ImmutablePair<Integer, Integer> hb : commonHBs) {
//                        System.out.println(happenBeforeGraph.get(hb.left).toString()
//                                + "=>" + happenBeforeGraph.get(hb.right).toString());
//                    }
                    //System.out.println(hbs);
                    System.out.println(commonHBs);
                    System.out.println(incompatibleRelations);

                }

                System.out.println(commonHBs.size());
                System.out.println(incompatibleRelations.size());
            }
        }
    }
}

