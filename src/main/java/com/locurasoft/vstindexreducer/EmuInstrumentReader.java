package com.locurasoft.vstindexreducer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EmuInstrumentReader {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("c:\\ctrlr\\instruments.syx");
        byte[] bytes = Files.readAllBytes(path);
        int start = 5;
        Charset charset = StandardCharsets.UTF_8;
        while (true) {
            int length = getNextLength(bytes, start);
            if (length == -1) {
                return;
            }
            byte[] name = new byte[length - 3];
            System.arraycopy(bytes, start + 2, name, 0, length - 3);
            String s = new String(name, charset);
            System.out.println(String.format("%d-%d = %s", bytes[start], bytes[start+1], s));
            start += length;
        }
    }

    static int getNextLength(byte[] bytes, int start) {
        int length = 0;
        int i = start;
        while (i < bytes.length) {
            if (bytes[i] == 0) {
                break;
            } else {
                length++;
            }
            i++;
        }
        if (i == bytes.length) {
            return -1;
        }
        return length + 1;
    }
}
