package util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class PairOfPair {
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
