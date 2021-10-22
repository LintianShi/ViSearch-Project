package traceprocessing;

import datatype.AbstractDataType;
import history.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RawTraceProcessor {
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
        for (String filename : fileList) {
            if (filename.endsWith(".trc")) {
                ArrayList<Record> thread = new ArrayList<>();
                BufferedReader br = new BufferedReader(new FileReader(filename));
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
    }

    public Program generateProgram(AbstractDataType adt) {
        List<List<Invocation>> subPrograms = new ArrayList<>();
        for (int i = 0; i < rawTrace.size(); i++) {
            List<Invocation> invocations = new ArrayList<>();
            for (Record r : rawTrace.get(i)) {
                invocations.add(r.generateInvocation(adt));
            }
            subPrograms.add(invocations);
        }
        return new Program(subPrograms);
    }

    public static void main(String[] args) throws Exception {
        RawTraceProcessor rp = new RawTraceProcessor();
        rp.load("trace");

    }
}

