package checking;

import arbitration.VisibilityType;
import datatype.RiakMap;
import datatype.RiakSet;
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
    public void testDataSet(String filepath, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        File baseFile = new File(filepath);
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("experiment_data/set321_COMPLETE.txt")));
        int i = 0;
        for (File file : files) {
            i++;
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            AdtChecker checker = new AdtChecker(new RiakMap());
            SearchConfiguration configuration = new SearchConfiguration.Builder()
                    .setAdt(new RiakSet())
                    .setEnableIncompatibleRelation(false)
                    .setEnablePrickOperation(false)
                    .setEnableOutputSchedule(false)
                    .setVisibilityType(visibilityType)
                    .setFindAllAbstractExecution(false)
                    .build();
            Boolean result = checker.multiThreadCheck(file.toString(), configuration, false);
            if (!result) {
                bw.write(file.toString() + "\n");
            }
        }
        bw.close();
    }

    public void testDataSet(List<String> dataset, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("experiment_data/set321_" + visibilityType.name() + ".txt")));
        for (String file : dataset) {
            testTrace(file, true, visibilityType);
        }
    }

    public String testTrace(String filename, boolean enableMulti, VisibilityType visibilityType) throws Exception {
        AdtChecker checker = new AdtChecker(new RiakSet());
        SearchConfiguration configuration = new SearchConfiguration.Builder()
                .setAdt(new RiakMap())
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

        return filename + ":" + result;

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


        String datatype = "set321";
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                new SetChecker().testDataSet("D:\\" + datatype + "\\result", true, VisibilityType.COMPLETE);
            } else {
                List<String> r = new SetChecker().filter("experiment_data/" + datatype + "_" + VisibilityType.values()[i].name() + ".txt");
                new SetChecker().testDataSet(r, true, VisibilityType.values()[i]);
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
                .setVisibilityType(VisibilityType.CAUSAL)
                .setFindAllAbstractExecution(false)
                .build();
        result = checker.normalCheck(filename, configuration, false);
        countDownLatch.countDown();
    }
}
