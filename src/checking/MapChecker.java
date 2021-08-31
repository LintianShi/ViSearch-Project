package checking;

import datatype.RedisRpq;
import datatype.RiakMap;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;
import validation.SearchConfiguration;

public class MapChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakMap());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                setAdt(new RiakMap()).
                setEnablePrickOperation(true).
                setEnableOutputSchedule(false).build();
        checker.check("map_trace", configuration);
    }
}
