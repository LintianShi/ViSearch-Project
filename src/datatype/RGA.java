package datatype;

import history.Invocation;

import java.util.ArrayList;

public class RGA extends AbstractDataType {
    private ArrayList<String> data = new ArrayList<>();

    private String addAfter(Invocation invocation) {
        String first = (String) invocation.getArguments().get(0);
        String second = (String) invocation.getArguments().get(1);
        if (first.equals("")) {
            data.add(0, second);
        } else {
            int index = data.indexOf(first);
            if (index != -1) {
                data.add(index + 1, second);
            }
        }
        return "null";
    }

    private String read(Invocation invocation) {
        String res = "";
        for (int i = 0; i < data.size(); i++) {
            res += data.get(i);
        }
        return res;
    }

    @Override
    public void reset() {
        data = new ArrayList<>();
    }

    @Override
    public void print() {
        System.out.println("print: " + data.toString());
    }
}
