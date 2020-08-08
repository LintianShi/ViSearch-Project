package datatype;

import history.Invocation;
import rawtrace.CrdtOperation;

import java.util.ArrayList;

public class RRpq extends AbstractDataType {
    public Invocation transformCrdtOperation(CrdtOperation crdtOperation) {
        if (crdtOperation.isOrigin()) {
            Invocation invocation = new Invocation();
            invocation.setMethodName(crdtOperation.getOperationName());
            ArrayList<String> args = crdtOperation.getArguments();
            if (crdtOperation.getOperationName().equals("rzadd")) {
                Integer ele = Integer.parseInt(args.get(0));
                Double value = Double.parseDouble(args.get(1));
                invocation.addArguments(ele);
                invocation.addArguments(value);
            } else if (crdtOperation.getOperationName().equals("rzincrby")) {
                Integer ele = Integer.parseInt(args.get(0));
                Double value = Double.parseDouble(args.get(1));
                invocation.addArguments(ele);
                invocation.addArguments(value);
            } else if (crdtOperation.getOperationName().equals("rzrem")) {
                Integer ele = Integer.parseInt(args.get(0));
                invocation.addArguments(ele);
            } else if (crdtOperation.getOperationName().equals("rzscore")) {
                Integer ele = Integer.parseInt(args.get(0));
                invocation.addArguments(ele);
            } else if (crdtOperation.getOperationName().equals("rzmax")) {
                Integer ele = Integer.parseInt(args.get(0));
                invocation.addArguments(ele);
            } else {
                System.err.println("No such method");
                invocation = null;
            }
            return invocation;
        } else {
            System.err.println("Wrong CRDT Operation. Must be origin");
            return null;
        }
    }

    @Override
    public void reset() {;}
    public void print() {;}
}
