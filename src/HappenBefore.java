import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class HappenBefore {
    @JSONField(name="HAPPENBEFORE")
    private List<Pair> happenBefore = new ArrayList<>();

    public HappenBefore() {
        ;
    }

    public void addHappenBefore(Pair pair) {
        happenBefore.add(pair);
    }

    public List<Pair> getHappenBefore() {
        return happenBefore;
    }

    public void setHappenBefore(List<Pair> happenBefore) {
        this.happenBefore = happenBefore;
    }

    public Pair get(int i) {
        if (i >= 0 && i < happenBefore.size()) {
            return happenBefore.get(i);
        } else {
            return null;
        }
    }

    public int size() {
        return happenBefore.size();
    }
}

