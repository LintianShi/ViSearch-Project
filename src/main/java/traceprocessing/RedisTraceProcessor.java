package traceprocessing;

import datatype.AbstractDataType;
import history.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RedisTraceProcessor extends TraceProcessor {
    protected void load(String filepath) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

