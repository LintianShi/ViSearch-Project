package checking;

import arbitration.VisibilityType;
import datatype.*;
import traceprocessing.Record;
import validation.MinimalVisSearch;
import validation.SearchConfiguration;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SetChecker {
    public void testDataSet(AbstractDataType adt, String filepath, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        File baseFile = new File(filepath);
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        //BufferedWriter bw = new BufferedWriter(new FileWriter(new File("experiment_data/set321_COMPLETE.txt")));
        int i = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Starting " + df.format(new Date()));
        for (File file : files) {
            i++;
//            if (i % 1000 == 0) {
//                System.out.println(i);
//            }
//            Boolean result = testTrace(adt, file.toString(), enableMulti, visibilityType);
//            if (!result) {
//                System.out.println(file.toString() + ":" + result);
//            }
            if (i<69) {
                continue;
            }
            System.out.print(file.toString() + ":");
            String result = measureVisibility(adt.createInstance(), file.toString());
            System.out.println(result);
        }
        System.out.println("Finishing " + df.format(new Date()));
    }

    public void testDataSet(AbstractDataType adt, List<String> dataset, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        //BufferedWriter bw = new BufferedWriter(new FileWriter(new File("experiment_data/set321_" + visibilityType.name() + ".txt")));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Starting " + df.format(new Date()));
        for (String file : dataset) {
            Boolean result = testTrace(adt, file, enableMulti, visibilityType);
            System.out.println(file + ":" + result);
        }
        System.out.println("Finishing " + df.format(new Date()));
    }

    public boolean testTrace(AbstractDataType adt, String filename, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        AdtChecker checker = new AdtChecker(adt);
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(adt)
                .setEnableIncompatibleRelation(false)
                .setEnablePrickOperation(false)
                .setEnableOutputSchedule(false)
                .setVisibilityType(visibilityType)
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
//        if (!result) {
//            System.out.println(filename + ":" + result);
//        }

        return result;

//        System.out.println("Finishing " + df.format(new Date()));
//        System.out.println(filename + ":" + result);
//        System.out.println();
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

    public String measureVisibility(AbstractDataType adt, String filename) throws Exception {
        for (int i = 0; i < 6; i++) {
            System.out.println("state:" + VisibilityType.values()[i].name());
            boolean result = testTrace(adt.createInstance(), filename, true, VisibilityType.values()[i]);
            if (result) {
                return VisibilityType.values()[i].name();
            }
        }
        return "undefined";
    }

    public static void main(String[] args) throws Exception {
//        String result = new SetChecker().measureVisibility(new RedisList(), "D:\\list_rwf\\result\\rwf_list_default_1636094400419538430.trc");
//        System.out.println(result);
        new SetChecker().testDataSet(new RedisList(), "D:\\list_rwf\\result", true, VisibilityType.COMPLETE);

//        boolean result = new SetChecker().testTrace(new RedisRpq(), "D:\\rpq_rwf_1\\result\\rwf_rpq_default_1636084479236446532.trc", true, VisibilityType.WEAK);
//        System.out.println(result);
//        boolean result1 = new SetChecker().testTrace(new RedisRpq(), "D:\\rpq_rwf_1\\result\\rwf_rpq_default_1636084634233693904.trc", true, VisibilityType.WEAK);
//        System.out.println(result1);

//        List<String> r = new SetChecker().filter("experiment_data/list_complete.txt");
//        new SetChecker().testDataSet(new RedisList(), r, true, VisibilityType.CAUSAL);

//        String result = new SetChecker().measureVisibility(new RedisList(), "D:\\list_rwf\\result\\rwf_list_default_1636094400419538430");
//        System.out.println(result);
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
