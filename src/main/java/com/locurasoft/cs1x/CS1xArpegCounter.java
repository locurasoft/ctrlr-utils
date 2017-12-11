package com.locurasoft.cs1x;

public class CS1xArpegCounter {

    public static void main(String[] args) {
        int offs = 40;
        for (int i = 0; i <= 201; i++) {
            if (i == 0) {
                System.out.println(String.format("MIDI=%d", i));
            } else {
                System.out.println(String.format("%d=%d", offs, i));
                offs++;
            }
        }
    }
}
