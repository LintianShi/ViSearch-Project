package checking;

import datatype.RiakSet;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;
import validation.SearchConfiguration;

public class SetChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakSet())
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(true)
                .setEnableOutputSchedule(true).build();
        checker.check("set_trace/Set_default_3_3_300_1", configuration, true);
        checker.readResult("set_trace/Set_default_3_3_300_1/result.obj");
    }
}
