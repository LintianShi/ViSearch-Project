import java.util.*;

public class BacktracingTest {
    private int[][] data;
    private int[] index = {0, 0, 0};

    private Stack<Integer> R = new Stack<>();

    public BacktracingTest() {
        data = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = j * 3 + i;
            }
        }
    }

    public boolean examine(int num) {
        return num < 3;
    }

    public boolean isEnd() {
        return R.size() == 9;
    }

    public void backtrace() {
        for (int i = 0; i < 3; i++) {
            if (examine(index[i])) {
                R.push(data[i][index[i]]);
                index[i]++;
                backtrace();
                index[i]--;
                R.pop();
            }
        }

        if (isEnd()) {
            System.out.println(R.toString());
        }
    }

    public void print() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println(data[i][j]);
            }
        }
    }

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        Iterator<Integer> iterator = s.iterator();
        for (Integer i : s) {
            System.out.println(i);
        }
    }
}
