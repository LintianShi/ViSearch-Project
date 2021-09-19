package validation;

import arbitration.VisibilityType;
import com.google.common.collect.Multimap;
import datatype.AbstractDataType;
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
    private List<ImmutablePair<Integer, Integer>> extractCommonHBRelation(List<List<ImmutablePair<Integer, Integer>>> hbs) {
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

    private List<PairOfPair> removeCommonRelations(List<PairOfPair> incompatibleRelations, List<ImmutablePair<Integer, Integer>> commonRelations) {
        List<PairOfPair> cleanIncompatibleRelations = new ArrayList<>();
        for (PairOfPair pairOfPair : incompatibleRelations) {
            boolean flag = true;
            for (ImmutablePair<Integer, Integer> common : commonRelations) {
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

    private List<PairOfPair> extractIncompatibleHBRelation(List<List<ImmutablePair<Integer, Integer>>> hbs, List<List<HBGNode>> relatedNodes) {
        HashSet<PairOfPair> pairSet = new HashSet<>();
        for (List<ImmutablePair<Integer, Integer>> list : hbs) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                        PairOfPair pairOfPair = new PairOfPair(list.get(i), list.get(j));
                        pairSet.add(pairOfPair);
                }
            }
        }

        List<ImmutablePair<Integer, Integer>> allPairs = new ArrayList<>();
        for (int i = 0; i < relatedNodes.size(); i++) {
            for (int j = i + 1; j < relatedNodes.size(); j++) {
                for (int k = 0; k < relatedNodes.get(i).size(); k++) {
                    for (int h = 0; h < relatedNodes.get(j).size(); h++) {
                        allPairs.add(new ImmutablePair<>(relatedNodes.get(j).get(h).getId(), relatedNodes.get(i).get(k).getId()));
                        allPairs.add(new ImmutablePair<>(relatedNodes.get(i).get(k).getId(), relatedNodes.get(j).get(h).getId()));
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

    private void addHBRelations(HappenBeforeGraph happenBeforeGraph, List<ImmutablePair<Integer, Integer>> hbs) {
        for (ImmutablePair<Integer, Integer> hb : hbs) {
            //System.out.println(happenBeforeGraph.get(hb.getLeft()).toString() + " => " + happenBeforeGraph.get(hb.getRight()).toString());
            happenBeforeGraph.addHBRelation(hb.getLeft(), hb.getRight());
        }
    }

    private Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> generateRuleTable(List<PairOfPair> incompatibleRelations) {
        Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> ruleTable = HashMultimap.create();
        for (PairOfPair pairOfPair : incompatibleRelations) {
            ruleTable.put(pairOfPair.getLeft(), new ImmutablePair<>(pairOfPair.getRight().right, pairOfPair.getRight().left));
            ruleTable.put(new ImmutablePair<>(pairOfPair.getRight().right, pairOfPair.getRight().left), pairOfPair.getLeft());
            ruleTable.put(pairOfPair.getRight(), new ImmutablePair<>(pairOfPair.getLeft().right, pairOfPair.getLeft().left));
            ruleTable.put(new ImmutablePair<>(pairOfPair.getLeft().right, pairOfPair.getLeft().left), pairOfPair.getRight());
        }
        return ruleTable;
    }

    public void preprocess(HappenBeforeGraph happenBeforeGraph, AbstractDataType adt) {
        List<PairOfPair> incompatibleRelations = new ArrayList<>();

        for (HBGNode node : happenBeforeGraph) {
            if (adt.isReadCluster(node.getInvocation())) {
                System.out.println(node.toString());
                List<List<HBGNode>> relatedNodes = adt.getRelatedOperations(node, happenBeforeGraph);
                System.out.println(relatedNodes.toString());

                HappenBeforeGraph subHBGraph = new HappenBeforeGraph(relatedNodes);
                System.out.println("Sub graph size: " + subHBGraph.size());
                if (subHBGraph.size() > 5) {
                    continue;
                }

                SearchConfiguration configuration = new SearchConfiguration.Builder()
                                                            .setAdt(adt)
                                                            .setFindAllAbstractExecution(true)
                                                            .setEnablePrickOperation(false)
                                                            .setVisibilityType(VisibilityType.CAUSAL)
                                                            .setEnableOutputSchedule(false)
                                                            .setEnableIncompatibleRelation(false)
                                                            .build();
                MinimalVisSearch subSearch = new MinimalVisSearch(configuration);
                subSearch.init(subHBGraph);
                subSearch.checkConsistency();
                if (subSearch.getResults().size() == 0) {
                    System.out.println("no abstract execution");
                    continue;
                }

                List<List<ImmutablePair<Integer, Integer>>> hbs = new ArrayList<>();
                for (SearchState state1 : subSearch.getResults()) {
                    hbs.add(state1.extractHBRelation());
                }

                List<ImmutablePair<Integer, Integer>> commonHBs = extractCommonHBRelation(hbs);
                List<PairOfPair> subIncompatibleRelations = removeCommonRelations(extractIncompatibleHBRelation(hbs, relatedNodes), commonHBs);
                addHBRelations(happenBeforeGraph, commonHBs);
                System.out.printf("Common: %d, incompatible: %d\n", commonHBs.size(), subIncompatibleRelations.size());
                for (PairOfPair pair : subIncompatibleRelations) {
                    System.out.printf("<%s -> %s, %s ->%s>\n", happenBeforeGraph.get(pair.getLeft().left).toString(), happenBeforeGraph.get(pair.getLeft().right).toString(), happenBeforeGraph.get(pair.getRight().left).toString(), happenBeforeGraph.get(pair.getRight().right).toString());
                }
                System.out.println(subIncompatibleRelations.toString());
                incompatibleRelations.addAll(subIncompatibleRelations);

                System.out.println("Common relation size: " + commonHBs.size());
                System.out.println("Incompatible relation size: " + subIncompatibleRelations.size());
            }
        }

        Multimap<ImmutablePair<Integer, Integer>, ImmutablePair<Integer, Integer>> ruleTable = generateRuleTable(incompatibleRelations);
        happenBeforeGraph.setRuleTable(ruleTable);
    }
}

