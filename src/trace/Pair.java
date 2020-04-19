package trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class Pair {
    @JSONField(name = "A", ordinal = 1)
    private Integer a;
    @JSONField(name = "B", ordinal = 2)
    private Integer b;

    public Pair() {
        ;
    }

    public Pair(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }

    public Integer getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public static void main(String[] args) {
        Pair pair = new Pair(1,2);
        HappenBefore happenBefore = new HappenBefore();
        happenBefore.addHappenBefore(pair);
        System.out.println(JSON.toJSONString(happenBefore));
    }
}
