package validation;

import com.alibaba.fastjson.JSON;
import datatype.AbstractDataType;
import history.*;
import arbitration.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class Validation {
    private Program program;
    private List<HappenBeforeGraph> happenBeforeGraphs;

    public void loadTrace(String filename) {
        File file = new File(filename);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            String jsonfile = new String(filecontent, "UTF-8");
            program = JSON.parseObject(jsonfile, Program.class);
            program.assignID();
            //happenBeforeGraphs = program.generateHappenBeforeGraphs();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void initializeHappenBeforeGraphs() {
        happenBeforeGraphs = program.generateHappenBeforeGraphs();
    }

    public static Behaviour crdtExecute(AbstractDataType adt, Linearization lin, LinVisibility visibility) {
        Behaviour rets = new Behaviour();
        try {
            for (int i = 0; i < lin.size(); i++) {
                Invocation targetInvocation = lin.get(i).getInvocation();
                if (targetInvocation.getOperationType().equals("UPDATE")) {
                    for (int j = 0; j <= i; j++) {
                        HBGNode node = lin.get(j);
                        if (node.getInvocation().getOperationType().equals("UPDATE")) {
                            String ret = adt.invoke(node.getInvocation());
                            if (i == j) {
                                rets.add(node.getId(), ret);
                            }
                        }
                    }
                } else if (targetInvocation.getOperationType().equals("QUERY")) {
                    Set<HBGNode> vis = visibility.getNodeVisibility(lin.get(i));
                    for (int j = 0; j <= i; j++) {
                        HBGNode node = lin.get(j);
                        if (node.getInvocation().getOperationType().equals("UPDATE") && vis.contains(node)) {
                            adt.invoke(node.getInvocation());
                        } else if (i == j) {
                            String ret = adt.invoke(node.getInvocation());
                            if (ret != null) {
                                rets.add(node.getId(), ret);
                                if (i == lin.size() - 1) {
                                    System.out.println(node.getInvocation().getRetValue() + " + " + ret);
                                }
                            }

                        }
                    }
                }
                adt.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rets;
    }

    public static Behaviour execute(AbstractDataType adt, Linearization lin, LinVisibility visibility) {
        Behaviour rets = new Behaviour();
        try {
            for (int i = 0; i < lin.size(); i++) {
                Set<HBGNode> vis = visibility.getNodeVisibility(lin.get(i));
                for (int j = 0; j <= i; j++) {
                    HBGNode node = lin.get(j);
                    if (vis.contains(node)) {
                        String ret = adt.invoke(node.getInvocation());
                        if (i == j) {
                            rets.add(node.getId(), ret);
                        }
                    }
                }
                adt.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rets;
    }

    public void printProgram() {
        System.out.println(program.toString());
    }

}
