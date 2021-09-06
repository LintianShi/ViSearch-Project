package checking;

import datatype.RiakMap;
import validation.SearchConfiguration;

public class MapChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakMap());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                setAdt(new RiakMap()).
                setEnablePrickOperation(false).
                setEnableOutputSchedule(true).build();
        checker.normalCheck("map_trace/Map_default_3_3_300_1", configuration, false);
    }
}
