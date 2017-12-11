package com.locurasoft.d50;

public class D50Counter {
    private static final String[] NOTES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    public static void main(String[] args) {

        for (int i = 1; i <= 7; i++) {
            int offset = 0;
            if (i == 1) {
                offset = 9;
            }
            for (int j = offset; j < NOTES.length; j++) {
                String note = NOTES[j];
                System.out.println(">" + note + Integer.toString(i));
            }
        }
    }
}
