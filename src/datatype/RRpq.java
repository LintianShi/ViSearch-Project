package datatype;

import crdttrace.CrdtOperation;
import history.Invocation;

import java.math.BigDecimal;
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
            if (data.get(i).getVal().doubleValue() >= temp.getVal().doubleValue())
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
        Element temp;
        if (i < data.size()) {
            temp = data.get(i);
        } else {
            return;
        }
        while (j <= tail)
        {
            if (j < tail && data.get(j).getVal().doubleValue() <= data.get(j + 1).getVal().doubleValue())
                j++;
            if (temp.getVal().doubleValue() >= data.get(j).getVal().doubleValue())
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

    private void add(int k, BigDecimal v)
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

    private void inc(int k, BigDecimal i)
    {
        if (i.doubleValue() == 0)
            return;
        if (map.containsKey(k))
        {
            map.get(k).inc(i);
            if (i.doubleValue() > 0)
                shiftUp(map.get(k).getIndex());
            else
                shiftDown(map.get(k).getIndex());
        }
    }

    private String max()
    {
        if (data.size() == 0) {
            return "rwfzscore:" + "NONE";
        } else {
            Element max = data.get(0);
            BigDecimal val = max.getVal();
            return "rwfzmax:" + Integer.toString(max.getEle()) + ":" + val.stripTrailingZeros().toPlainString();
        }
    }

    private String score(Integer k) {
        if (data.size() == 0 || !map.containsKey(k)) {
            return "rwfzscore:" + Integer.toString(k) + ":" + "NONE";
        } else {
            BigDecimal val = map.get(k).getVal();
            return "rwfzscore:" + Integer.toString(k) + ":" + val.stripTrailingZeros().toPlainString();
        }
    }

    public boolean isRelated(Invocation src, Invocation dest) {
        if (src.getOperationType().equals("UPDATE")) {
            return false;
        } else if (src.getOperationType().equals("QUERY")) {
            if (src.getMethodName().equals("rwfzscore")) {
                Integer ele = (Integer) src.getArguments().get(0);
                if (dest.getOperationType().equals("UPDATE") && (Integer) dest.getArguments().get(0) == ele) {
                    return true;
                } else {
                    return false;
                }
            } else if (src.getMethodName().equals("rwfzmax")) {
                return true;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public Invocation transformCrdtOperation(CrdtOperation crdtOperation) {
        if (crdtOperation.isOrigin()) {
            Invocation invocation = new Invocation();
            invocation.setMethodName(crdtOperation.getOperationName());
            invocation.setRetValue(crdtOperation.getOperationName());
            ArrayList<String> args = crdtOperation.getArguments();

            if (crdtOperation.getOperationName().equals("rwfzadd") || crdtOperation.getOperationName().equals("rwfzincrby")) {
                Integer ele = Integer.parseInt(args.get(0));
                //Double value = Double.parseDouble(args.get(1));
                BigDecimal value = new BigDecimal(args.get(1));
                invocation.addArguments(ele);
                invocation.addArguments(value);
                String ret = crdtOperation.getOperationName();
                ret += ":" + args.get(0) + ":" + args.get(1);
                invocation.setRetValue(ret);
                invocation.setOperationType("UPDATE");
            } else if (crdtOperation.getOperationName().equals("rwfzrem")) {
                Integer ele = Integer.parseInt(args.get(0));
                invocation.addArguments(ele);
                String ret = crdtOperation.getOperationName();
                ret += ":" + args.get(0);
                invocation.setRetValue(ret);
                invocation.setOperationType("UPDATE");
            } else if (crdtOperation.getOperationName().equals("rwfzscore")) {
                String arg1 = args.get(0);
                Integer ele = Integer.parseInt(arg1.substring(0, arg1.length() - 1));
                invocation.addArguments(ele);
                invocation.setOperationType("QUERY");
                if (args.get(1).equals("NONE")) {
                    invocation.setRetValue(crdtOperation.getOperationName() + ":" + args.get(0) + args.get(1));
                } else {
                    BigDecimal value = new BigDecimal(args.get(1));
                    invocation.setRetValue(crdtOperation.getOperationName() + ":" + args.get(0) + value.stripTrailingZeros().toPlainString());
                }
            } else if (crdtOperation.getOperationName().equals("rwfzmax")) {
                BigDecimal value = new BigDecimal(args.get(1));
                invocation.setRetValue(crdtOperation.getOperationName() + ":" + args.get(0) + value.stripTrailingZeros().toPlainString());
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
        BigDecimal v = (BigDecimal) invocation.getArguments().get(1);
        add(k, v);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + v.toString();
    }

    public String rwfzrem(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        rem(k);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k);
    }

    public String rwfzincrby(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        BigDecimal i = (BigDecimal) invocation.getArguments().get(1);
        inc(k, i);
        //return "NULL";
        return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + i.toString();
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
//        Element element1 = new Element(1, 11.0);
//        Element element2 = new Element(2, 22.0);
//        ArrayList<Element> list = new ArrayList<>();
//        list.add(element1);
//        list.add(element1);
//        Element temp = list.get(0);
//        temp.inc(33.0);
//        System.out.println(list.get(1).getVal());
    }
}

class Element {
    private Integer ele;
    private BigDecimal val;
    private Integer index;

    public Element() {
        this.ele = 0;
        this.val = new BigDecimal(0.0);
    }

    public Element(Integer ele, BigDecimal val) {
        this.ele = ele;
        this.val = val;
    }

    public BigDecimal getVal() {
        return val;
    }

    public Integer getEle() {
        return ele;
    }

    public void setEle(Integer ele) {
        this.ele = ele;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void inc(BigDecimal i) {
        val = val.add(i);
    }
}
