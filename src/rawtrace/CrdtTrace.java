package rawtrace;

import datatype.AbstractDataType;
import datatype.RRpq;
import history.HBGNode;
import history.HappenBeforeGraph;
import history.Invocation;
import history.QueryUpdateExtension;
import test.TestMinimalRALinCheck;
import validation.OperationTypes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrdtTrace {
    private List<CrdtOperation> starts = new ArrayList<>();
    private int size;

    public CrdtTrace() {
        ;
    }

    public CrdtTrace(List<String> fileNames) {
        for (String fileName : fileNames) {
            starts.add(loadFile(fileName));
        }
    }

    private CrdtOperation loadFile(String fileName) {
        try {
            FileReader file = new FileReader(fileName);
            BufferedReader br = new BufferedReader(file);
            String str;
            CrdtOperation head = null;
            CrdtOperation tail = null;
            int num = 0;
            while ((str = br.readLine()) != null && num < 10) {
                num++;
                CrdtOperation temp = new CrdtOperation(str);
                if (head == null) {
                    head = temp;
                    tail = temp;
                } else {
                    tail.setPo(temp);
                    tail = temp;
                }
            }

            return head;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void extendHappenBeforeRelation() {
        int k = 0;
        for (CrdtOperation head : starts) {
            int i = 0;
            CrdtOperation c = head;
            while (c != null) {
                if (c.getType() == CrdtOperation.CRDT_OPERATION_TYPE.PREPARE && c.getPo() != null) {
                    c.getPo().setOrigin(true);
                } else if (c.getType() == CrdtOperation.CRDT_OPERATION_TYPE.EFFECT && c.isOrigin() == true) {
                    addHappenBeforeRelation(c, head);
                }
                c = c.getPo();
            }
        }
        adjustHappenBeforeRelation();
        removeRedundantEffect();
    }

    private void addHappenBeforeRelation(CrdtOperation c, CrdtOperation head) {
        for (CrdtOperation start : starts) {
            if (!start.equals(head)) {
                CrdtOperation o = start;
                while (o != null) {
                    if (c.getUniqueID() == o.getUniqueID()) {
                        c.addHb(o);
                    }
                    o = o.getPo();
                }
            }
        }
    }

    private void adjustHappenBeforeRelation() {
        for (CrdtOperation head : starts) {
            CrdtOperation c = head;
            while (c != null) {
                if (c.isOrigin()) {
                    size++;
                    ArrayList<CrdtOperation> newHbList = new ArrayList<>();
                    for (CrdtOperation next : c.getHbs()) {
                        CrdtOperation newHb = findNextOriginEffect(next);
                        if (newHb != null) {
                            newHbList.add(newHb);
                        }
                    }
                    c.setHbs(newHbList);
                }
                c = c.getPo();
            }
        }
    }

    private CrdtOperation findNextOriginEffect(CrdtOperation operation) {
        while (operation != null)
        {
            if (operation.isOrigin()) {
                return operation;
            }
            operation = operation.getPo();
        }
        return null;
    }

    private void removeRedundantEffect() {
        ArrayList<CrdtOperation> newStarts = new ArrayList<>();
        for (CrdtOperation head : starts) {
            CrdtOperation current = head;
            CrdtOperation prev = null;
            while (current != null) {
                if (!current.isOrigin()) {
                    if (prev == null) {
                        head = current.getPo();
                        prev = null;
                        current = head;
                    } else {
                        prev.setPo(current.getPo());
                        current = current.getPo();
                    }
                } else {
                    prev = current;
                    current = current.getPo();
                }
            }
            newStarts.add(head);
        }
        starts = newStarts;
    }

    public HappenBeforeGraph generateHappenBeforeGraph(AbstractDataType adt) {
        HashMap<Integer, HBGNode> invocationMap = new HashMap<>();
        for (CrdtOperation head : starts) {
            CrdtOperation c = head;
            while (c != null) {
                Invocation invocation = adt.transformCrdtOperation(c);
                invocationMap.put(c.getUniqueID(), new HBGNode(invocation, c.getUniqueID()));
                c = c.getPo();
            }
        }

        for (CrdtOperation head : starts) {
            CrdtOperation c = head;
            while (c != null) {
                HBGNode node = invocationMap.get(c.getUniqueID());
                HBGNode poNode = (c.getPo() != null) ? invocationMap.get(c.getPo().getUniqueID()) : null;
                if (poNode != null) {
                    node.addNextNode(poNode);
                    poNode.addPrevNode(node);
                }

                for (CrdtOperation hbOp : c.getHbs()) {
                    HBGNode nextNode = invocationMap.get(hbOp.getUniqueID());
                    node.addNextNode(nextNode);
                    nextNode.addPrevNode(node);
                }
                c = c.getPo();
            }
        }

        List<HBGNode> startNodes = new ArrayList<>();
        for (CrdtOperation head : starts) {
            startNodes.add(invocationMap.get(head.getUniqueID()));
        }
        return new HappenBeforeGraph(startNodes, invocationMap);
    }

    public void extendQueryUpdate(OperationTypes operationTypes, QueryUpdateExtension queryUpdateExtension) {

    }

    public int size() {
        return size;
    }

    public void print() {
        int i = 0;
        for (CrdtOperation head : starts) {
            System.out.println("Server" + Integer.toString(i) + ":");
            CrdtOperation c = head;
            for (int j = 0; c != null && j < 22; j++) {
                System.out.println(c.toString());
                c = c.getPo();
            }
            i++;
        }
    }

    public static void main(String[] args) {
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            fileList.add("trace/server" + Integer.toString(i) + "_crdt.trc");
        }
        CrdtTrace trace = new CrdtTrace(fileList);
        trace.extendHappenBeforeRelation();
        //trace.print();

        HappenBeforeGraph happenBeforeGraph = trace.generateHappenBeforeGraph(new RRpq());
        System.out.println(trace.size());
        TestMinimalRALinCheck.minimalExtensionRaLinCheck(happenBeforeGraph, null, null, new RRpq());
    }
}
