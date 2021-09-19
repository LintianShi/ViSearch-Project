package checking;

import datatype.RedisRpq;
import validation.SearchConfiguration;

public class RpqChecker {
    public static void main(String[] args) throws Exception {
        AdtChecker checker = new AdtChecker(new RedisRpq());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                setAdt(new RedisRpq()).
                setEnablePrickOperation(false).
                setEnableOutputSchedule(true).build();
        checker.normalCheck("rpq_trace/Rpq_default_3_1_2000_1631873920", configuration, true);
    }
}
