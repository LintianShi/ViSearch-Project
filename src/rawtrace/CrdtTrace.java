package rawtrace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CrdtTrace {
    private List<CrdtOperation> starts = new ArrayList<>();

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
            while ((str = br.readLine()) != null) {
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

    public void print() {
        int i = 0;
        for (CrdtOperation head : starts) {
            System.out.println("Server" + Integer.toString(i) + ":");
            CrdtOperation c = head;
            for (int j = 0; j < 22; j++) {
                System.out.println(c.toString());
                c = c.getPo();
            }
            i++;
        }
    }

    public static void main(String[] args) {
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            fileList.add("server" + Integer.toString(i) + "_crdt.trc");
        }
        CrdtTrace trace = new CrdtTrace(fileList);
        trace.extendHappenBeforeRelation();
        trace.print();
    }
}
