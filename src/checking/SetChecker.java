package checking;

import arbitration.VisibilityType;
import datatype.RiakMap;
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
        int i = 0;
        for (String file : dataset) {
            i++;
            CountDownLatch countDownLatch = new CountDownLatch(1);
            CheckerThread checkerThread = new CheckerThread(file, countDownLatch);
            Thread thread = new Thread(checkerThread);
            thread.start();
            countDownLatch.await(180, TimeUnit.SECONDS);
            thread.stop();
            if (i % 10 == 0) {
                System.out.println(i);
            }
            if (!checkerThread.result) {
                System.out.println(file.toString() + ":" + checkerThread.result);
            }
        }
    }

    public void testTrace(String filename, boolean enableMulti) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakMap());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakMap())
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(false)
                .setEnableOutputSchedule(false)
                .setVisibilityType(VisibilityType.COMPLETE)
                .setFindAllAbstractExecution(false)
                .build();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println("Starting " + df.format(new Date()));
        Boolean result;
        if (enableMulti) {
            result = checker.multiThreadCheck(filename, configuration, false);
        } else {
            result = checker.normalCheck(filename, configuration, false);
        }
        if (!result) {
            System.out.println(filename + ":" + result);
        }
//        System.out.println("Finishing " + df.format(new Date()));
//        System.out.println(filename + ":" + result);
//        System.out.println();
    }

    public static void main(String[] args) throws Exception {
//        new SetChecker().testTrace("D:\\set311_with_size\\result\\set311_default_5_3_15_1634987852347.trc", true);
        new SetChecker().testDataSet("D:\\map311_with_size\\result", true);
//        BufferedReader br = new BufferedReader(new FileReader(new File("experiment_data/set311_complete_violation.txt")));
//        String str = null;
//        int i = 0;
//        while ((str = br.readLine()) != null) {
//            //if (i >= 34)
//                new SetChecker().testTrace(str, true);
//            //i++;
//        }
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
