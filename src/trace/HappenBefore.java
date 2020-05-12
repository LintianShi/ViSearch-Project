package trace;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class HappenBefore {
    @JSONField(name="HAPPENBEFORE")
    private List<HBPair> happenBefore = new ArrayList<>();

    public HappenBefore() {
        ;
    }

    public void addHappenBefore(HBPair hbPair) {
        happenBefore.add(hbPair);
    }

    public List<HBPair> getHappenBefore() {
        return happenBefore;
    }

    public void setHappenBefore(List<HBPair> happenBefore) {
        this.happenBefore = happenBefore;
    }

    public HBPair get(int i) {
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

