package com.locurasoft.cs1x;

import com.google.common.collect.ImmutableMap;

import java.math.BigInteger;
import java.util.Map;

public class CS1xCounter {

    private static final int HEADER = 9;
    private static final int FOOTER = 2;
    private static final int[] OFFSETS = {0x2E, 0x15, 0x09, 0x29, 0x29, 0x29, 0x29};

    private static final Map<Integer, Integer> LAYER_MAP = ImmutableMap.<Integer, Integer>builder()
            .put(0x4, 24)
            .put(0x9, 64)
            .put(0x13, 63)
            .put(0x14, 63)
            .put(0x15, 63)
            .put(0x17, 64)
            .put(0x18, 63)
            .put(0x19, 63)
            .put(0x1A, 64)
            .put(0x1D, 64)
            .put(0x1E, 63)
            .put(0x20, 31)
            .put(0x21, 63)
            .put(0x22, 15)
            .put(0x23, 63)
            .put(0x24, 63)
            .put(0x25, 64)
            .put(0x26, 63)
            .put(0x27, 64)
            .put(0x28, 63)
            .build();

    private static final Map<String, Integer> COMMON_MAP = ImmutableMap.<String, Integer>builder()
            .put("1-24", 32)
            .put("1-25", 32)
            .put("1-26", 32)
            .put("1-27", 32)
            .put("1-28", 32)
            .put("3-0", 64)
            .put("3-3", 24)
            .put("3-4", 64)
            .build();

    public static void main(String[] args) {
        printCommon();
        for (int i = 1; i <= 4; i++) {
            printLayer(i);
        }
    }

    private static void printCommon() {
        for (Map.Entry<String, Integer> entry : COMMON_MAP.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("\\-");
            int incr = 0;
            int offs = Integer.parseInt(split[0]);
            BigInteger bi = new BigInteger(split[1], 16);
            for (int i = 1; i < offs; i++) {
                incr += FOOTER;
                incr += OFFSETS[i - 1];
            }
            incr += HEADER;
            incr += bi.intValue();
            System.out.println(String.format("[%d] = %d,", incr, entry.getValue()));
        }
    }

    private static void printLayer(int index) {
        for (Map.Entry<Integer, Integer> entry : LAYER_MAP.entrySet()) {
            Integer key = entry.getKey();
            int incr = 0;
            for (int i = 1; i < index + 3; i++) {
                incr += FOOTER;
                incr += OFFSETS[i - 1];
            }
            incr += HEADER;
            incr += key;
            System.out.println(String.format("[%d] = %d,", incr, entry.getValue()));
        }
    }
}
