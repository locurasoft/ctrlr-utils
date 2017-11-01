package com.locurasoft.cs1x;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CS1xIncrementer {
    public static void main(String[] args) {
        String s = "[122] = 64, [127] = 64, [135] = 64, [136] = 64, [137] = 64, [138] = 64, [139] = 64, [141] = 64, [142] = 64, [143] = 64, [144] = 64, [147] = 64, [148] = 64, [150] = 64, [151] = 64, [152] = 64, [153] = 64, [154] = 64, [155] = 64, [156] = 64, [157] = 64, [158] = 64,";
        Pattern pattern = Pattern.compile("\\[(\\d+)\\] = (\\d+),");
        Matcher matcher = pattern.matcher(s);
        int l = 3;
        while (matcher.find()) {
            System.out.print(String.format("[%d] = %s, ", Integer.parseInt(matcher.group(1)) + 52 * l, matcher.group(2)));
        }
    }
}
