package checking;

import datatype.RiakMap;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;

public class MapChecker {
    public static void main(String[] args) throws Exception {
        RawTraceProcessor rp = new RawTraceProcessor();
        rp.load("map_trace");
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(new RiakMap()).generateHappenBeforeGraph();
        new HBGPreprocessor().preprocess(happenBeforeGraph, new RiakMap());

        //happenBeforeGraph.printStartNodes();

        //TestMinimalRALinCheck.minimalExtensionRaLinCheck("result.txt", happenBeforeGraph, new RiakMap());
    }
}
