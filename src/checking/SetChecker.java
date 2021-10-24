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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SetChecker {
    public static void main(String[] args) throws Exception {
        //File baseFile = new File("D:\\set311_with_size\\result");
        File baseFile = new File("test");
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        //int i = 0;
        //for (File file : files) {
        //    i++;
        File file = files[0];
            System.out.println(file.toString());
            CountDownLatch countDownLatch = new CountDownLatch(1);
            CheckerThread checkerThread = new CheckerThread(file.toString(), countDownLatch);
            Thread thread = new Thread(checkerThread);
            thread.start();
            countDownLatch.await();
            //thread.stop();
//            if (i % 1000 == 0) {
//                System.out.println(i);
//            }
            if (!checkerThread.result) {
                System.out.println(file.toString() + ":" + checkerThread.result);
            }
        //}

//        AdtChecker checker = new AdtChecker(new RiakSet());
//        SearchConfiguration configuration = new SearchConfiguration.Builder()
//                .setAdt(new RiakSet())
//                .setEnableIncompatibleRelation(false)
//                .setEnablePrickOperation(false)
//                .setEnableOutputSchedule(false)
//                .setVisibilityType(VisibilityType.MONOTONIC)
//                .setFindAllAbstractExecution(true)
//                .build();
//        checker.normalCheck("test_false.trc", configuration, false);
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
                .setVisibilityType(VisibilityType.MONOTONIC)
                .setFindAllAbstractExecution(false)
                .build();
        result = checker.normalCheck(filename, configuration, false);
        countDownLatch.countDown();
    }
}
