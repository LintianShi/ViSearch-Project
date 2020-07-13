package trace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import validation.OperationTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Program {
    @JSONField(name = "SUBPROGRAMS", ordinal = 1)
    private List<SubProgram> subPrograms = new ArrayList<>();

    @JSONField(name = "HBS", ordinal = 2)
    private List<HappenBefore> hbs = new ArrayList<>();

    public Program() {
        ;
    }

    public List<SubProgram> getSubPrograms() {
        return subPrograms;
    }

    public void setSubPrograms(List<SubProgram> subPrograms) {
        this.subPrograms = subPrograms;
    }

    public List<HappenBefore> getHbs() {
        return hbs;
    }

    public void setHbs(List<HappenBefore> hbs) {
        this.hbs = hbs;
    }

    public List<HappenBeforeGraph> generateHappenBeforeGraphs() {
        List<HappenBeforeGraph> graphs = new ArrayList<>();
        for (HappenBefore hb : hbs) {
            graphs.add(new HappenBeforeGraph(subPrograms, hb));
        }
        return graphs;
    }

    public void assignID() {
        int index = 0;
        for (int i = 0; i < subPrograms.size(); i++) {
            for (int j = 0; j < subPrograms.get(i).size(); j++) {
                subPrograms.get(i).get(j).setPairID(new ImmutablePair<>(i, j));
                subPrograms.get(i).get(j).setId(index);
                index++;
            }
        }
    }

    public void extendQueryUpdate(OperationTypes operationTypes, QueryUpdateExtension queryUpdateExtension) {
        for (SubProgram subProgram : subPrograms) {
            for (Invocation invocation : subProgram.getInvocations()) {
                String operationType = operationTypes.getOperationType(invocation.getMethodName());
                if (operationType != null) {
                    invocation.setOperationType(operationType);
                } else {
                    invocation.setOperationType("UPDATE");
                }
            }
        }

        for (SubProgram subProgram : subPrograms) {
            for (int i = 0; i < subProgram.size(); i++) {
                Invocation invocation = subProgram.get(i);
                if (invocation.getOperationType().equals("QUERYUPDATE")) {
                    Function<Invocation, Pair<Invocation, Invocation>> mapFunction = queryUpdateExtension.getMethodMapFunction(invocation.getMethodName());
                    if (mapFunction == null) {
                        continue;
                    }
                    Pair<Invocation, Invocation> query_update = mapFunction.apply(invocation);
                    int index = subProgram.getInvocations().indexOf(invocation);
                    subProgram.getInvocations().set(index, query_update.getLeft());
                    subProgram.getInvocations().add(index + 1, query_update.getRight());

                    //update HB relation due to query-update
                    for (HappenBefore hb : hbs) {
                        for (HBPair hbPair : hb.getHappenBefore()) {
                            if (hbPair.getPrev().equals(invocation.getPairID())) {
                                hbPair.increasePrev();
                            } else if (hbPair.getPrev().getLeft() == invocation.getPairID().getLeft()
                                    && hbPair.getPrev().getRight() > invocation.getPairID().getRight()) {
                                hbPair.increasePrev();
                            } else if (hbPair.getNext().getLeft() == invocation.getPairID().getLeft()
                                    && hbPair.getNext().getRight() > invocation.getPairID().getRight()) {
                                hbPair.increaseNext();
                            }
                        }
                    }
                }
            }
        }
        assignID();
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public static HappenBeforeGraph load(String fileName) throws Exception {
        File filename = new File(fileName);
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        Program program = JSON.parseObject(jsonfile, Program.class);

        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();
        return list.get(0);
    }

    public static HappenBeforeGraph load(String fileName, OperationTypes operationTypes, QueryUpdateExtension queryUpdateExtension) throws Exception {
        File filename = new File(fileName);
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        Program program = JSON.parseObject(jsonfile, Program.class);
        program.assignID();
        program.extendQueryUpdate(operationTypes, queryUpdateExtension);

        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();
        return list.get(0);
    }

    public static void main(String[] args) throws Exception {
        File filename = new File("ralin1.json");
        Long filelength = filename.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = new FileInputStream(filename);
        in.read(filecontent);
        String jsonfile = new String(filecontent, "UTF-8");
        //System.out.println(jsonfile);
        Program program = JSON.parseObject(jsonfile, Program.class);
        System.out.println(JSON.toJSONString(program));

        List<HappenBeforeGraph> list = program.generateHappenBeforeGraphs();
        for (HappenBeforeGraph g : list) {
            System.out.println("--------------start---------------");
            //g.print();
            Invocation.visibility.put("put", "COMPLETE");
            Invocation.visibility.put("remove", "COMPLETE");
            Invocation.visibility.put("contains", "BASIC");
            List<Linearization> lins = g.generateLins();
            for (Linearization l : lins) {
                System.out.println(l);
            }
            System.out.println("--------------end---------------");
        }
    }
}