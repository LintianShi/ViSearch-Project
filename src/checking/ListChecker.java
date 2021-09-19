package checking;

import datatype.RedisList;
import validation.SearchConfiguration;

public class ListChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RedisList());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                setAdt(new RedisList()).
                setEnablePrickOperation(false).
                setEnableOutputSchedule(true).build();
        checker.normalCheck("list_trace/List_default_3_1_600_1631158025", configuration, false);
    }
}
