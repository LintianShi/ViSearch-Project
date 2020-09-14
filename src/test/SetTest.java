package test;

import java.util.HashSet;

public class SetTest {
    public static void main(String[] args) {
        HashSet<String> set1 = new HashSet<>();
        set1.add("1");
        set1.add("4");
        set1.add("3");
        set1.add("2");

        HashSet<String> set2 = new HashSet<>();
        set2.add("2");
        set2.add("1");
        set2.add("3");
        set2.add("41");

        System.out.println(set1.equals(set2));
    }
}
