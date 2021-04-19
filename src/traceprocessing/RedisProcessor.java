package traceprocessing;

import datatype.AbstractDataType;
import history.*;
import validation.OperationTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RedisProcessor implements TraceProcessor {
    private List<List<Record>> rawTrace;

    public void load(String filepath) throws Exception {
        List<String> fileList = new ArrayList<String>();
        File baseFile = new File(filepath);
        if (baseFile.isFile() || !baseFile.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                fileList.add(file.getAbsolutePath());
            }
        }

        rawTrace = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            ArrayList<Record> thread = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(fileList.get(i)));
            String temp;
            while ((temp = br.readLine()) != null) {
                if (temp.length() > 10) {
                    Record record = new Record(temp);
                    thread.add(record);
                }
            }
            rawTrace.add(thread);
        }
    }

    public Program getProgram() {
        return getProgram(null);
    }

    public Program getProgram(OperationTypes operationTypes) {
        //Program program = new Program();
        HappenBefore happenBefore = new HappenBefore();
        for (int i = 0; i < rawTrace.size(); i++) {
            for (int j = 0; j < rawTrace.size(); j++) {
                if (i == j) {
                    continue;
                }
                happenBefore.addHappenBefores(constructHb(rawTrace.get(i), i, rawTrace.get(j), j));
            }
        }
        List<SubProgram> subPrograms = new ArrayList<>();
        for (int i = 0; i < rawTrace.size(); i++) {
            List<Invocation> invocations = new ArrayList<>();
            for (Record r : rawTrace.get(i)) {
                invocations.add(r.generateInvocation(operationTypes));
            }
            subPrograms.add(new SubProgram(invocations));
        }
        return new Program(subPrograms, happenBefore);
    }

    private List<HBPair> constructHb(List<Record> threadA, int A, List<Record> threadB, int B) {
        List<HBPair> hbRelations = new ArrayList<>();
        int i = 0;
        int j = 0;
        int last = -1;
        for ( ; i < threadA.size() && j < threadB.size(); ) {
            Record a = threadA.get(i);
            Record b = threadA.get(j);
            if (a.compareTo(b) == 0) {
                if (last == -1) {
                    i++;
                } else {
                    HBPair hb = new HBPair(new Integer[]{B,last}, new Integer[]{A,i});
                    hbRelations.add(hb);
                    last = -1;
                    i++;
                }
            } else if (a.compareTo(b) > 0) {
                last = j;
                j++;
            } else {
                last = -1;
                i++;
            }
        }
        if (last != -1) {
            HBPair hb = new HBPair(new Integer[]{B,last}, new Integer[]{A,i});
            hbRelations.add(hb);
        }
        return hbRelations;
    }

    public static void main(String[] args) throws Exception {
        RedisProcessor rp = new RedisProcessor();
        rp.load("trace");

    }
}

class Record implements Comparable<Record> {
    private long startTime;
    private long endTime;
    private String operationName;
    private List<Object> arguments = new ArrayList<>();
    private String retValue;

    public Record(String line) throws Exception {
        String[] cols = line.split(",");
        if (cols.length < 4) {
            throw new Exception();
        }
        this.startTime = Long.parseLong(cols[0]);
        this.endTime = Long.parseLong(cols[1]);
        this.operationName = "rwfz" + cols[2];
        this.retValue = cols[cols.length - 1];
        for (int i = 3; i < cols.length - 1; i++) {
            arguments.add(cols[i]);
        }
    }

    public Invocation generateInvocation() {
        Invocation invocation = new Invocation();
        invocation.setMethodName(operationName);
        invocation.setRetValue(retValue);
        invocation.setArguments(arguments);
        return invocation;
    }

    public Invocation generateInvocation(OperationTypes operationTypes) {
        if (operationTypes == null) {
            return generateInvocation();
        } else {
            Invocation invocation = generateInvocation();
            invocation.setOperationType(operationTypes.getOperationType(operationName));
            return invocation;
        }
    }

    @Override
    public int compareTo(Record o) {
        if (o.startTime > this.endTime) {
            return -1;
        } else if (o.endTime < this.startTime) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return Long.toString(startTime) + "," + Long.toString(endTime) + "," + operationName;
    }
}
