package com.locurasoft.cs1x;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CS1xCounter2 {
    private static final int[] OFFSETS = { 9, 65, 98, 118, 170, 222, 274};

    public static void main(String[] args) {
        String line;
        Scanner stdin = new Scanner(System.in);
        while (stdin.hasNextLine() && !(line = stdin.nextLine()).equals("")) {
            String[] tokens = line.split("\\-");
            int offset = OFFSETS[Integer.parseInt(tokens[0]) - 1];
            int parsedResult = (int) Long.parseLong(tokens[1], 16);
            System.out.println(offset + parsedResult);
        }
        stdin.close();
    }
}
