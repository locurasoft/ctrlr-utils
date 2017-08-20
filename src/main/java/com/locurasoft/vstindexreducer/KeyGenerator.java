package com.locurasoft.vstindexreducer;

public class KeyGenerator {
    public static String[] NOTES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    public static int[] OCTAVES = {-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static void main(String[] args) {
        for (int octave : OCTAVES) {
            for (String note : NOTES) {
                System.out.println(String.format("%s%d", note, octave));
            }
        }
    }
}
