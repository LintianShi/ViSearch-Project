package traceprocessing;

import history.Program;

public interface TraceProcessor {
    void load(String filepath) throws Exception;
    Program getProgram();
}
