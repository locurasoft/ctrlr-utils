package com.locurasoft.vstindexreducer;

import java.util.Locale;

public class CS1xValueGenerator {

    private static String BELOW_ZERO = "\"D%d>W\", ";
    private static String ZERO = "\"D=W\", ";
    private static String ABOVE_ZERO = "\"D<W%d\", ";

    private static String ER_BELOW_ZERO = "\"E%d>R\", ";
    private static String ER_ZERO = "\"E=R\", ";
    private static String ER_ABOVE_ZERO = "\"E<R%d\", ";
    public static void main(String[] args) {
        performanceBank();
    }

    static void performanceBank() {
        for (int i = 0; i < 128; i++) {
            System.out.println(i);
        }
    }

    static void detune() {
        for (int i = -128; i < 128; i++) {
            float f = ((float)i) / 10;
            System.out.println(String.format("%.1f", f));
        }
    }

    static void arpTempo() {
        for (int i = 0; i <= 201; i++) {
            String s = "";
            if (i == 0) {
                s = "MIDI";
            } else {
                s = Integer.toString(i + 39);
            }
            System.out.println(String.format("%s=%d", s, i));
        }
    }

    static void pan() {
        for (int i = 0; i < 128; i++) {
            if (i == 0) {
                System.out.println("random");
            } else if (i < 0x40) { // L
                System.out.println(String.format("L%d", -1 * (i - 64)));
            } else if (i == 0x40) {
                System.out.println("C");
            } else {
                System.out.println(String.format("R%d", (i - 64)));
            }
        }
    }

    static void dryWet() {
        for (int i = -63; i <= 63; i++) {
            if (i < 0) {
                System.out.print(String.format(BELOW_ZERO, i * -1));
            } else if (i == 0) {
                System.out.print(String.format(ZERO));
            } else {
                System.out.print(String.format(ABOVE_ZERO, i));
            }
        }
    }

    static void lfoPhaseDiff() {
        System.out.print("{ ");
        for (int i = 0; i <= 124; i++) {
            int t = (i - 64) * 3;
            System.out.print("\"" + t + "\"");
            if (i != 124) {
                System.out.print(", ");
            }
        }
        System.out.print(" }");
    }

    static void erRevBalance() {
        for (int i = -63; i <= 63; i++) {
            if (i < 0) {
                System.out.print(String.format(ER_BELOW_ZERO, i * -1));
            } else if (i == 0) {
                System.out.print(String.format(ER_ZERO));
            } else {
                System.out.print(String.format(ER_ABOVE_ZERO, i));
            }
        }

    }

    static void eqMidWidth() {
        for (int i = 10; i <= 120; i++) {
            float f = ((float) i) / 10;
            System.out.print(String.format("\"%.1f\", ", f).replaceFirst(",", "."));
        }
    }
}
