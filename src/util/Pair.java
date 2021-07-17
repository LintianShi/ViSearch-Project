package util;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Pair {
    private ImmutablePair<Integer, Integer> pair;

    public Pair(Integer left, Integer right) {
        pair = new ImmutablePair<>(left, right);
    }

    public Integer getLeft() {
        return pair.left;
    }

    public Integer getRight() {
        return pair.right;
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return pair.equals(obj);
    }
}
