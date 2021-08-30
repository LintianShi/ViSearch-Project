package datatype;

import history.Invocation;
import traceprocessing.Record;
import validation.OperationTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RRpq extends AbstractDataType {
    private ArrayList<Element> data = new ArrayList<>();
    private HashMap<Integer, Element> map = new HashMap<>();

    public AbstractDataType createInstance() {
        return new RRpq();
    }

    public String getOperationType(String methodName) {
        if (operationTypes == null) {
            operationTypes = new OperationTypes();
            operationTypes.setOperationType("rem", "UPDATE");
            operationTypes.setOperationType("add", "UPDATE");
            operationTypes.setOperationType("incrby", "UPDATE");
            operationTypes.setOperationType("max", "QUERY");
            operationTypes.setOperationType("score", "QUERY");
            return operationTypes.getOperationType(methodName);
        } else {
            return operationTypes.getOperationType(methodName);
        }
    }

    public boolean isRelated(Invocation src, Invocation dest) {
        if (src.getOperationType().equals("UPDATE")) {
            return false;
        } else if (src.getOperationType().equals("QUERY")) {
            if (src.getId() == dest.getId()) {
                return true;
            }
            if (src.getMethodName().equals("score")) {
                Integer ele = (Integer) src.getArguments().get(0);
                if (dest.getOperationType().equals("UPDATE") && dest.getArguments().get(0).equals(ele)) {
                    return true;
                } else {
                    return false;
                }
            } else if (src.getMethodName().equals("zmax")) {
                if (src.getRetValue().equals("null")) {
                    return false;
                }
                Integer ele = Integer.parseInt(src.getRetValue().split(" ")[0]);
                if (dest.getOperationType().equals("UPDATE") && dest.getArguments().get(0).equals(ele)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean isReadCluster(Invocation invocation) {
        if (invocation.getMethodName().equals("rwfzscore") || (invocation.getMethodName().equals("rwfzmax") && invocation.getRetValue().equals("null") )) {
            return true;
        } else {
            return false;
        }
    }

    public Invocation generateInvocation(Record record) {
        Invocation invocation = new Invocation();
        if (record.getRetValue().equals("null")) {
            invocation.setRetValue(record.getRetValue());
        } else {
            invocation.setRetValue(record.getRetValue() + ".0");
        }
        invocation.setMethodName(record.getOperationName());
        invocation.setOperationType(getOperationType(record.getOperationName()));

        if (record.getOperationName().equals("add")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
            invocation.addArguments(Double.parseDouble(record.getArgument(1)));
        } else if (record.getOperationName().equals("rem")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
        } else if (record.getOperationName().equals("incrby")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
            invocation.addArguments(Double.parseDouble(record.getArgument(1)));
        } else if (record.getOperationName().equals("max")) {
            //
        } else if (record.getOperationName().equals("score")) {
            invocation.addArguments(Integer.parseInt(record.getArgument(0)));
        } else {
            System.out.println("Unknown operation");
        }

        return invocation;
    }

    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public void reset() {
        map = new HashMap<>();
        data = new ArrayList<>();
    }
    public void print() {;}

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

    private void add(int k, Double v)
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

    private void inc(int k, Double i)
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
            //return "rwfzscore:" + "NONE";
            return "null";
        } else {
            Element max = data.get(0);
            Double val = max.getVal();
            //return "rwfzmax:" + Integer.toString(max.getEle()) + ":" + val.stripTrailingZeros().toPlainString();
            return Integer.toString(max.getEle()) + " " + val.toString();
        }
    }

    private String score(Integer k) {
        if (data.size() == 0 || !map.containsKey(k)) {
            //return "rwfzscore:" + Integer.toString(k) + ":" + "NONE";
            return "null";
        } else {
            Double val = map.get(k).getVal();
            //return "rwfzscore:" + Integer.toString(k) + ":" + val.stripTrailingZeros().toPlainString();
            return val.toString();
        }
    }

    public String add(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        Double i = (Double) invocation.getArguments().get(1);
        add(k, i);
        return "null";
        //return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + v.toString();
    }

    public String rem(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        rem(k);
        return "null";
        //return invocation.getMethodName() + ":" + Integer.toString(k);
    }

    public String incrby(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        Double i = (Double) invocation.getArguments().get(1);
        inc(k, i);
        return "null";
        //return invocation.getMethodName() + ":" + Integer.toString(k) + ":" + i.toString();
    }

    public String score(Invocation invocation) {
        Integer k = (Integer) invocation.getArguments().get(0);
        return score(k);
        //return invocation.getMethodName();
    }

    public String max(Invocation invocation) {
        return max();
        //return invocation.getMethodName();
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
        val = val + i;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ele, val, index);
    }
}
