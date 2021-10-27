package checking;

import arbitration.VisibilityType;
import datatype.RiakSet;
import traceprocessing.Record;
import validation.MinimalVisSearch;
import validation.SearchConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SetChecker {
    public void testDataSet(String filepath, boolean enableMulti) throws Exception {
        File baseFile = new File(filepath);
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        int i = 0;
        for (File file : files) {
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            testTrace(file.toString(), enableMulti);
        }
    }

    public void testDataSet(List<String> dataset, boolean enableMulti) throws Exception {
        for (String file : dataset) {
            testTrace(file, true);
        }
    }

    public void testTrace(String filename, boolean enableMulti) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakSet())
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(false)
                .setEnableOutputSchedule(false)
                .setVisibilityType(VisibilityType.BASIC)
                .setFindAllAbstractExecution(false)
                .build();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Starting " + df.format(new Date()));
        Boolean result;
        if (enableMulti) {
            result = checker.multiThreadCheck(filename, configuration, false);
        } else {
            result = checker.normalCheck(filename, configuration, false);
        }
//        if (!result) {
//            System.out.println(filename + ":" + result);
//        }
        System.out.println("Finishing " + df.format(new Date()));
        System.out.println(filename + ":" + result);
        System.out.println();
    }

    public List<String> filter(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
        List<String> result = new LinkedList<>();
        String str = null;
        while ((str = br.readLine()) != null) {
            if (str.endsWith(":false"))
                result.add(str.substring(0, str.lastIndexOf(':')));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        //new SetChecker().testTrace("D:\\set311_with_size\\result\\set311_default_5_3_15_1634985195455.trc");
//        new SetChecker().testDataSet("D:\\set311_with_size\\result", true);
//        BufferedReader br = new BufferedReader(new FileReader(new File("experiment_data/set311_complete_violation.txt")));
//        String str = null;
//        int i = 0;
//        while ((str = br.readLine()) != null) {
//            //if (i >= 34)
//                new SetChecker().testTrace(str, true);
//            //i++;
//        }
        List<String> r = new SetChecker().filter("experiment_data/set311_monotonic.txt");
        new SetChecker().testDataSet(r, true);
    }
}

class CheckerThread implements Runnable {
    public boolean result = false;
    public String filename;
    public AdtChecker checker;
    public CountDownLatch countDownLatch;

    public CheckerThread(String filename, CountDownLatch countDownLatch) {
        this.filename = filename;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakSet())
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(false)
                .setEnableOutputSchedule(false)
                .setVisibilityType(VisibilityType.CAUSAL)
                .setFindAllAbstractExecution(false)
                .build();
        result = checker.normalCheck(filename, configuration, false);
        countDownLatch.countDown();
    }
}
