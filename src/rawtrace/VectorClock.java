package rawtrace;

import java.util.ArrayList;

public class VectorClock {
    private ArrayList<Integer> vector = new ArrayList<>();
    private int id;

    public VectorClock(String str) {
        String[] r1 = str.split("\\|");
        this.id = Integer.parseInt(r1[1]);
        String[] r2 = r1[0].split(",");
        for (String s : r2) {
            vector.add(Integer.parseInt(s));
        }
    }

    public VectorClock(int id) {
        this.id = id;
    }

    public VectorClock() {
        this.id = -1;
    }

    public ArrayList<Integer> getVector() {
        return vector;
    }

    public int getVector(int i) {
        return vector.get(i);
    }

    public void setVector(ArrayList<Integer> vector) {
        this.vector = vector;
    }

    public void setVector(int index, Integer element) {
        this.vector.set(index, element);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        String result = "";
        boolean first = true;
        for (Integer i : vector) {
            if (first) {
                result += i.toString();
            } else {
                result += "," + i.toString();
            }
        }
        result += "|" + Integer.toString(id);
        return result;
    }

    public static void main(String[] args) {
        String test = "1,0,0,0,0,0,0,0,0|0";
        String[] r1 = test.split("\\|");
        System.out.println(r1[1]);
    }
}
