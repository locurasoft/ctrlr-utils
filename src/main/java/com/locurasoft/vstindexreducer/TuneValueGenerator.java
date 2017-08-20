package com.locurasoft.vstindexreducer;

public class TuneValueGenerator {
    private static int START = -50;
    private static int END = 50;
    private static double INCREMENT = 0.1;

    public static void main(String[] args) {
        int total = 0;
        for (double i = START; i <= END; i += INCREMENT) {
            System.out.println(String.format("%.2f", i));
            total++;
        }
        System.out.println("Total " + total);
    }
}
