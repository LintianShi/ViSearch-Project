package trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class HBPair {
    @JSONField(name = "PREV", ordinal = 1)
    private Integer[] prev = new Integer[2];
    @JSONField(name = "NEXT", ordinal = 2)
    private Integer[] next = new Integer[2];

    public HBPair() {
        ;
    }

    public HBPair(Integer[] prev, Integer[] next) {
        this.prev = prev;
        this.next = next;
    }

    public Pair<Integer, Integer> getPrev() {
        return new ImmutablePair<>(prev[0], prev[1]);
    }

    public Pair<Integer, Integer> getNext() {
        return new ImmutablePair<>(next[0], next[1]);
    }

    public void setPrev(Integer[] prev) {
        this.prev = prev;
    }

    public void setNext(Integer[] next) {
        this.next = next;
    }

    public void increasePrev() {
        prev[1]++;
    }

    public void increaseNext() {
        next[1]++;
    }

    public static void main(String[] args) {
//        HBPair hbPair = new HBPair(1,2);
//        HappenBefore happenBefore = new HappenBefore();
//        happenBefore.addHappenBefore(hbPair);
//        System.out.println(JSON.toJSONString(happenBefore));
    }
}
