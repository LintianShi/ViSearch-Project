package validation;

import arbitration.VisibilityType;
import datatype.AbstractDataType;
import datatype.RRpq;
import history.HBGNode;
import history.HappenBeforeGraph;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
                ImmutablePair<Integer, Integer> reversePair = new ImmutablePair<>(common.right, common.left);
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
            HBGNode prev = happenBeforeGraph.get(hb.left);
            HBGNode next = happenBeforeGraph.get(hb.right);
            prev.addNextNode(next);
            next.addPrevNode(prev);
        }
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




                List<List<ImmutablePair<Integer, Integer>>> hbs = new ArrayList<>();
                for (SearchState state1 : subSearch.getResults()) {
                    hbs.add(state1.extractHBRelation());
                }

                List<ImmutablePair<Integer, Integer>> commonHBs = extractCommonHBRelation(hbs);
                List<PairOfPair> incompatibleRelations = removeCommonRelations(extractIncompatibleHBRelation(hbs, relatedNodes), commonHBs);
                addHBRelations(happenBeforeGraph, commonHBs);
                if (node.getId() == 1002) {
                    System.out.println(subSearch.getResults().get(0));
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

class PairOfPair {
    private ImmutablePair<Integer, Integer> pair1;
    private ImmutablePair<Integer, Integer> pair2;

    public PairOfPair(ImmutablePair<Integer, Integer> pair1, ImmutablePair<Integer, Integer> pair2) {
        int hash1 = pair1.hashCode();
        int hash2 = pair2.hashCode();
        if (hash1 < hash2) {
            this.pair1 = pair1;
            this.pair2 = pair2;
        } else {
            this.pair1 = pair2;
            this.pair2 = pair1;
        }

    }

    public ImmutablePair<Integer, Integer> getLeft() {
        return pair1;
    }

    public ImmutablePair<Integer, Integer> getRight() {
        return pair2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PairOfPair that = (PairOfPair) o;

        return new EqualsBuilder()
                .append(pair1, that.pair1)
                .append(pair2, that.pair2)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pair1)
                .append(pair2)
                .toHashCode();
    }

    public String toString() {
        return pair1.toString() + " " + pair2.toString();
    }
}
