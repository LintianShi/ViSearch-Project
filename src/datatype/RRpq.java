package datatype;

import history.Invocation;
import org.apache.commons.lang3.tuple.Pair;
import rawtrace.CrdtOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class RRpq extends AbstractDataType {
    private ArrayList<Element> data = new ArrayList<>();
    private HashMap<Integer, Element> map = new HashMap<>();

    private void shiftUp(int s) {
        int j = s, i = (j - 1) / 2;
        Element temp = data.get(j);
        while (j > 0)
        {
            if (data.get(i).getVal() >= temp.getVal())
                break;
            else
                {
                    data.set(j, data.get(i));
                    data.get(j).setIndex(j);
                    j = i;
                    i = (i - 1) / 2;
                }
        }
        temp.setIndex(j);
        data.set(j, temp);
    }

    private void shiftDown(int s)
    {
        int i = s, j = 2 * i + 1, tail = data.size() - 1;
        Element temp = data.get(i);
        while (j <= tail)
        {
            if (j < tail && data.get(j).getVal() <= data.get(j + 1).getVal())
                j++;
            if (temp.getVal() >= data.get(j).getVal())
                break;
        else
            {
                data.set(i, data.get(j));
                data.get(i).setIndex(i);
                i = j;
                j = i * 2 + 1;
            }
        }
        temp.setIndex(i);
        data.set(i, temp);
    }

    private void add(int k, double v)
    {
        if (!map.containsKey(k)) {
            Element element = new Element(k, v);
            map.put(k, element);
            data.add(element);
            shiftUp(data.size() - 1);
        }
    }

    private void rem(int k)
    {
        if (map.containsKey(k))
        {
            int i = map.get(k).getIndex();
            map.remove(k);
            data.set(i, data.get(data.size() - 1));
            data.remove(data.size() - 1);
            shiftDown(i);
        }
    }

    private void inc(int k, double i)
    {
        if (i == 0)
            return;
        if (map.containsKey(k))
        {
            map.get(k).inc(i);
            if (i > 0)
                shiftUp(map.get(k).getIndex());
            else
                shiftDown(map.get(k).getIndex());
        }
    }

    private String max()
    {
        if (data.size() == 0) {
            return "NONE";
        } else {
            Element max = data.get(0);
            return Integer.toString(max.getEle()) + ":" + Double.toString(max.getVal());
        }
    }

    private String score(Integer k) {
        if (data.size() == 0 || !map.containsKey(k)) {
            return Integer.toString(k) + ":" + "NONE";
        } else {
            Double val = map.get(k).getVal();
            return Integer.toString(k) + ":" + Double.toString(val);
        }
    }




    public Invocation transformCrdtOperation(CrdtOperation crdtOperation) {
        if (crdtOperation.isOrigin()) {
            Invocation invocation = new Invocation();
            invocation.setMethodName(crdtOperation.getOperationName());
            invocation.setRetValue(crdtOperation.getOperationName());
            ArrayList<String> args = crdtOperation.getArguments();

            if (crdtOperation.getOperationName().equals("rwfzadd")) {
                Integer ele = Integer.parseInt(args.get(0));
                Double value = Double.parseDouble(args.get(1));
                invocation.addArguments(ele);
                invocation.addArguments(value);
                String ret = crdtOperation.getOperationName();
                ret += ":" + Integer.toString(ele) + ":" + Double.toString(value);
                invocation.setRetValue(ret);
                invocation.setOperationType("UPDATE");
            } else if (crdtOperation.getOperationName().equals("rwfzincrby")) {
                Integer ele = Integer.parseInt(args.get(0));
                Double value = Double.parseDouble(args.get(1));
                invocation.addArguments(ele);
                invocation.addArguments(value);
                String ret = crdtOperation.getOperationName();
                ret += ":" + Integer.toString(ele) + ":" + Double.toString(value);
                invocation.setRetValue(ret);
                invocation.setOperationType("UPDATE");
            } else if (crdtOperation.getOperationName().equals("rwfzrem")) {
                Integer ele = Integer.parseInt(args.get(0));
                invocation.addArguments(ele);
                String ret = crdtOperation.getOperationName();
                ret += ":" + Integer.toString(ele);
                invocation.setRetValue(ret);
                invocation.setOperationType("UPDATE");
            } else if (crdtOperation.getOperationName().equals("rwfzscore")) {
                String arg1 = args.get(0);
                Integer ele = Integer.parseInt(arg1.substring(0, arg1.length() - 1));
                invocation.addArguments(ele);
                invocation.setOperationType("QUERY");
                if (args.get(1).equals("NONE")) {
                    invocation.setRetValue(args.get(0) + args.get(1));
                } else {
                    Double val = Double.parseDouble(args.get(1));
                    invocation.setRetValue(args.get(0) + Double.toString(val));
                }
            } else if (crdtOperation.getOperationName().equals("rwfzmax")) {
                Double val = Double.parseDouble(args.get(1));
                invocation.setRetValue(args.get(0) + Double.toString(val));
                invocation.setOperationType("QUERY");
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

    public String rwfzadd(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        Double v = (Double) invocation.getArguments().get(1);
        add(k, v);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + Double.toString(v);
    }

    public String rwfzrem(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        rem(k);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k);
    }

    public String rwfzincrby(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        Double i = (Double) invocation.getArguments().get(1);
        inc(k, i);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + Double.toString(i);
    }

    public String rwfzscore(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        return score(k);
        //return invocation.getMethodName();
    }

    public String rwfzmax(Invocation invocation) {
        return max();
        //return invocation.getMethodName();
    }

    @Override
    public void reset() {
        map = new HashMap<>();
        data = new ArrayList<>();
    }
    public void print() {;}

    public static void main(String[] args) {
        Element element1 = new Element(1, 11.0);
        Element element2 = new Element(2, 22.0);
        ArrayList<Element> list = new ArrayList<>();
        list.add(element1);
        list.add(element1);
        Element temp = list.get(0);
        temp.inc(33.0);
        System.out.println(list.get(1).getVal());
    }
}

class Element {
    private Integer ele;
    private Double val;
    private Integer index;

    public Element() {
        this.ele = 0;
        this.val = 0.0;
    }

    public Element(Integer ele, Double val) {
        this.ele = ele;
        this.val = val;
    }

    public Double getVal() {
        return val;
    }

    public Integer getEle() {
        return ele;
    }

    public void setEle(Integer ele) {
        this.ele = ele;
    }

    public void setVal(Double val) {
        this.val = val;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void inc(Double i) {
        val += i;
    }
}
