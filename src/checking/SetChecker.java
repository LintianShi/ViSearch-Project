package checking;

import datatype.RiakSet;
import history.HappenBeforeGraph;
import test.TestMinimalRALinCheck;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;

public class SetChecker {
    public static void main(String[] args) throws Exception {
        RawTraceProcessor rp = new RawTraceProcessor();
        rp.load("set_trace");
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(new RiakSet()).generateHappenBeforeGraph();
        new HBGPreprocessor().preprocess(happenBeforeGraph, new RiakSet());

        //happenBeforeGraph.printStartNodes();

        TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RiakSet());
    }
}
