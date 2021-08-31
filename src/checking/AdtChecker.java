package checking;

import datatype.AbstractDataType;
import datatype.RiakSet;
import history.HappenBeforeGraph;
import traceprocessing.RawTraceProcessor;
import validation.HBGPreprocessor;
import validation.MinimalVisSearch;
import validation.SearchConfiguration;
import validation.SearchState;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class AdtChecker {
    protected AbstractDataType adt;

    public AdtChecker(AbstractDataType adt) {
        this.adt = adt;
    }

    public void check(String input, SearchConfiguration configuration) {
        HappenBeforeGraph happenBeforeGraph = load(input);
        MinimalVisSearch vfs = new MinimalVisSearch(configuration);
        vfs.init(happenBeforeGraph);
        vfs.checkConsistency();
        outputResult(input + "/result.obj", vfs.getResults());
    }

    protected HappenBeforeGraph load(String filename) {
        RawTraceProcessor rp = new RawTraceProcessor();
        try {
            rp.load(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(adt).generateHappenBeforeGraph();
        new HBGPreprocessor().preprocess(happenBeforeGraph, adt);
        return happenBeforeGraph;
    }

    protected void outputResult(String filename, List<SearchState> results) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readResult(String filename) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            List<SearchState> results = (List<SearchState>) ois.readObject();
            System.out.println(results.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                                                    setAdt(new RiakSet()).
                                                    setEnablePrickOperation(true).
                                                    setEnableOutputSchedule(true).build();
        checker.check("set_trace/Set_default_3_3_300_1", configuration);
        //checker.readResult("set_trace/Set_default_3_3_300_1/result.obj");
    }
}
