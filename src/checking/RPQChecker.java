package checking;

import datatype.RedisRpq;
import datatype.RiakSet;
import history.HappenBeforeGraph;
import test.TestMinimalRALinCheck;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;

public class RPQChecker {
    public static void main(String[] args) throws Exception {
        RawTraceProcessor rp = new RawTraceProcessor();
        rp.load("rpq_trace/RPQ_default_3_1_300_2");
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(new RedisRpq()).generateHappenBeforeGraph();
        new HBGPreprocessor().preprocess(happenBeforeGraph, new RedisRpq());

        //happenBeforeGraph.printStartNodes();

        TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RedisRpq());
    }
}
