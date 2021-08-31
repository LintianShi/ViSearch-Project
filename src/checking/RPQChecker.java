package checking;

import datatype.RedisRpq;
import datatype.RiakSet;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;
import validation.SearchConfiguration;

public class RPQChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RedisRpq());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                setAdt(new RedisRpq()).
                setEnablePrickOperation(true).
                setEnableOutputSchedule(true).build();
        checker.check("rpq_trace/RPQ_default_3_1_300_1", configuration);
    }
}
