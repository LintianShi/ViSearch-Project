package rawtrace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CrdtOperation {
    public static enum CRDT_OPERATION_TYPE { PREPARE, EFFECT}

    private long timeStamp;
    private CRDT_OPERATION_TYPE type;
    private String operationName;
    private String crdtName;
    private ArrayList<String> arguments = new ArrayList<>();
    private VectorClock vectorClock = new VectorClock();

    private int uniqueID;
    private boolean origin = false;

    private List<CrdtOperation> hbs = new ArrayList<>();
    private CrdtOperation po = null;

    public CrdtOperation() {
        ;
    }

    public CrdtOperation(String str) {
        String r1[] = str.split(",", 2);
        this.timeStamp = Long.parseLong(r1[0]);

        String r2[] = r1[1].trim().split(" ");
        if (r2[0].equals("PREPARE:"))
            this.type = CRDT_OPERATION_TYPE.PREPARE;
        else if (r2[0].equals("EFFECT:")) {
            this.type = CRDT_OPERATION_TYPE.EFFECT;
        } else {
            System.err.println("No such CRDT Operation Type");
        }
        this.operationName = r2[1];
        this.crdtName = r2[2];
        for (int i = 3; i < r2.length - 1; i++) {
            this.arguments.add(r2[i]);
        }
        if (this.type == CRDT_OPERATION_TYPE.EFFECT)
            this.vectorClock = new VectorClock(r2[r2.length - 1]);

        String id = operationName + "," + crdtName;
        for (String arg : arguments) {
            id += "," + arg;
        }
        this.uniqueID = id.hashCode();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public CRDT_OPERATION_TYPE getType() {
        return type;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getCrdtName() {
        return crdtName;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public List<CrdtOperation> getHbs() {
        return hbs;
    }

    public void addHb(CrdtOperation hb) {
        this.hbs.add(hb);
    }

    public CrdtOperation getPo() {
        return po;
    }

    public void setPo(CrdtOperation po) {
        this.po = po;
    }

    public boolean isOrigin() {
        return origin;
    }

    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    public void print() {
        System.out.print("Time Stamp: ");
        System.out.println(timeStamp);
        if (type == CRDT_OPERATION_TYPE.PREPARE)
            System.out.println("Operation Type: PREPARE");
        else if (type == CRDT_OPERATION_TYPE.EFFECT)
            System.out.println("Operation Type: EFFECT");
        else
            System.out.println("Operation Type: UNKNOWN");
        System.out.println("Operation Name: " + operationName);
        System.out.println("CRDT Name: " + crdtName);
        System.out.print("Argument: ");
        for (String s : arguments) {
            System.out.print(s + " ");
        }
        System.out.println();
        System.out.println("Vector Clock: " + vectorClock.toString());
    }

//    public int hashCode() {
//        return uniqueID;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return uniqueID == obj.hashCode();
//    }

    public String toString() {
        String result = Long.toString(timeStamp) + ", ";
        if (type == CRDT_OPERATION_TYPE.PREPARE) {
            result += "PREPARE: ";
        } else if (type == CRDT_OPERATION_TYPE.EFFECT) {
            result += "EFFECT: ";
        }
        result += operationName + " " + crdtName + " ";
        for (String arg : arguments) {
            result += arg + " ";
        }
        result += vectorClock.toString();
        for (CrdtOperation hb : hbs) {
            result += "Hb: " + hb.toString();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        FileReader file = new FileReader("server1_crdt.trc");
        BufferedReader br = new BufferedReader(file);
        CrdtOperation o = new CrdtOperation(br.readLine());
        o.print();
        CrdtOperation c = new CrdtOperation(br.readLine());
        c.print();
    }
}

