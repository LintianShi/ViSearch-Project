package checking;

import datatype.RiakSet;
import validation.SearchConfiguration;

public class SetChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakSet())
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(false)
                .setEnableOutputSchedule(true)
                .build();
//        checker.normalCheck("set_trace/Set_default_3_3_300_1", configuration, true);
//        checker.readResult("set_trace/Set_default_3_3_300_1/result.obj");
        String filename = "set_trace/Set_default_3_3_100_300_1";
        checker.multiThreadCheck(filename, configuration, true);
        checker.readResult(filename + "/result.obj");
    }
}
