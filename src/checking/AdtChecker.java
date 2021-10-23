package checking;

import datatype.AbstractDataType;
import datatype.RiakSet;
import history.HappenBeforeGraph;
import traceprocessing.RedisTraceProcessor;
import validation.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class AdtChecker {
    protected AbstractDataType adt;
    protected MinimalVisSearch vfs;

    public AdtChecker(AbstractDataType adt) {
        this.adt = adt;
    }

    public void normalCheck(String input, SearchConfiguration configuration, boolean enablePreprocess) {
        HappenBeforeGraph happenBeforeGraph = load(input);
        if (enablePreprocess) {
            preprocess(happenBeforeGraph);
        }
        this.vfs = new MinimalVisSearch(configuration);
        this.vfs.init(happenBeforeGraph);
        this.vfs.checkConsistency();
        //outputResult(input + "/result.obj", vfs.getResults());
    }

    public void multiThreadCheck(String input, SearchConfiguration configuration, boolean enablePreprocess) {
        HappenBeforeGraph happenBeforeGraph = load(input);
        if (enablePreprocess) {
            preprocess(happenBeforeGraph);
        }
        SearchConfiguration configuration1 = new SearchConfiguration.Builder()
                                                                .setAdt(new RiakSet())
                                                                .setEnableIncompatibleRelation(false)
                                                                .setEnableOutputSchedule(true)
                                                                .setEnablePrickOperation(false)
                                                                .setFindAllAbstractExecution(false)
                                                                .setVisibilityLimit(-1)
                                                                .setQueueLimit(10)
                                                                .setSearchMode(1)
                                                                .setSearchLimit(-1)
                                                                .build();
        MinimalVisSearch vfs1 = new MinimalVisSearch(configuration1);
        vfs1.init(happenBeforeGraph);
        vfs1.checkConsistency();
        System.out.println("starting multithread");
        List<SearchState> states = vfs1.getAllSearchState();
        System.out.println(states.size());

        MultiThreadSearch multiThreadSearch = new MultiThreadSearch(happenBeforeGraph, configuration);
        multiThreadSearch.startSearch(states);
        //outputResult(input + "/result.obj", multiThreadSearch.getResults());
    }

    protected void preprocess(HappenBeforeGraph happenBeforeGraph) {
        new HBGPreprocessor().preprocess(happenBeforeGraph, adt);
    }

    protected HappenBeforeGraph load(String filename) {
        RedisTraceProcessor rp = new RedisTraceProcessor();
        HappenBeforeGraph happenBeforeGraph = rp.generateProgram(filename, adt).generateHappenBeforeGraph();
        return happenBeforeGraph;
    }

    protected synchronized void outputResult(String filename, List<SearchState> results) {
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

    public int getExploredState() {
        return vfs.getStateExplored();
    }

    public static void main(String[] args) {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder().
                                                    setAdt(new RiakSet()).
                                                    setEnablePrickOperation(true).
                                                    setEnableOutputSchedule(true).build();
        checker.normalCheck("set_trace/Set_default_3_3_300_1", configuration, true);
        //checker.readResult("set_trace/Set_default_3_3_300_1/result.obj");
    }
}
