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
        List<String> fileList = new ArrayList<String>();
        List<String> result = new ArrayList<>();
        File baseFile = new File("set311_1/result");
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                CheckerThread checkerThread = new CheckerThread(file.toString(), countDownLatch);
                Thread thread = new Thread(checkerThread);
                thread.start();
                countDownLatch.await(60, TimeUnit.SECONDS);
                thread.stop();
                result.add(file.toString() + ":" + checkerThread.result);
                if (!checkerThread.result) {
                    System.out.println(file.toString());
                    System.out.println(checkerThread.result);
                }
            }
        }
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
                .setVisibilityType(VisibilityType.COMPLETE)
                .build();
        checker.normalCheck(filename, configuration, false);
        result = true;
        countDownLatch.countDown();
    }
}
