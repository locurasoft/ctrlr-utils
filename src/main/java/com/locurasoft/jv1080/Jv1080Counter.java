package com.locurasoft.jv1080;

public class Jv1080Counter {
    public static void main(String[] args) {
        printCoarsePitchA();
    }

    private static void printPhaserManual() {
        for (int i = 100; i <= 290; i += 10) {
            System.out.print(String.format("\"%d Hz\", ", i));
        }

        for (int i = 300; i <= 980; i += 20) {
            System.out.print(String.format("\"%d Hz\", ", i));
        }

        for (float i = 1; i <= 8; i += 0.1) {
            System.out.print(String.format("\"%.1f kHz\", ", i));
        }
    }

    private static void printPhaserRate() {
        for (float i = 0.05F; i <= 4.95F; i += 0.05) {
            System.out.print(String.format("\"%.2f Hz\", ", i));
        }

        for (float i = 5; i <= 6.9; i += 0.1) {
            System.out.print(String.format("\"%.1f Hz\", ", i));
        }

        for (float i = 7; i <= 10; i += 0.5) {
            System.out.print(String.format("\"%.1f Hz\", ", i));
        }
    }


    private static void printDelayTimeCenter() {
//        200 to 1000 ms, 200-545:5ms,550-1000:10ms
        for (int i = 200; i <= 545; i += 5) {
            System.out.print(String.format("\"%d ms\", ", i));
        }

        for (int i = 550; i <= 1000; i += 10) {
            System.out.print(String.format("\"%d ms\", ", i));
        }

    }

    private static void printPreDelayTime() {
//        0-0.49:0.1,5-9.5:0.5,10-49:1,50-100:2 ms
        for (float i = 0; i <= 0.49; i += 0.1) {
            System.out.print(String.format("\"%.2f ms\", ", i));
        }

        for (float i = 0.5F; i <= 9.5; i += 0.5) {
            System.out.print(String.format("\"%.1f ms\", ", i));
        }

        for (int i = 10; i <= 49; i += 1) {
            System.out.print(String.format("\"%d ms\", ", i));
        }

        for (int i = 50; i <= 100; i += 2) {
            System.out.print(String.format("\"%d ms\", ", i));
        }
    }

    private static void printDegree() {
        for (int i = 0; i <= 180; i += 2) {
            System.out.print(String.format("\"%d\", ", i));
        }
    }

    private static void printFeedbackLevel() {
//        -98% to 98%, 2% step
        for (int i = -98; i <= 0; i += 2) {
            System.out.print(String.format("\"%d%s\", ", i, "%"));
        }

        for (int i = 2; i <= 98; i += 2) {
            System.out.print(String.format("\"%d%s\", ", i, "%"));
        }
    }

    private static void printCoarsePitchA() {
//        -24..+12
        for (int i = -24; i <= 12; i += 1) {
            System.out.print(String.format("\"%d\", ", i));
        }
    }

    private static void printDelayTimeLeft() {
//        0ms to 500ms: 0-4.9:0.1,5-9.5:0.5,10-39:1,40-290:10,300-500:20ms step
        for (float i = 0; i <= 4.9; i += 0.1) {
            System.out.print(String.format("\"%.1f ms\", ", i));
        }

        for (float i = 5; i <= 9.5; i += 0.5) {
            System.out.print(String.format("\"%.1f ms\", ", i));
        }

        for (int i = 10; i <= 39; i += 1) {
            System.out.print(String.format("\"%d ms\", ", i));
        }

        for (int i = 40; i <= 290; i += 10) {
            System.out.print(String.format("\"%d ms\", ", i));
        }

        for (int i = 300; i <= 500; i += 20) {
            System.out.print(String.format("\"%d ms\", ", i));
        }
    }
}
