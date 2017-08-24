package com.locurasoft.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class ProcessUtils {
    private ProcessUtils() {

    }

    public static boolean execProcess(String[] cmd) throws InterruptedException, IOException {
        System.out.println("Executing: " + Arrays.toString(cmd));

        Process proc = Runtime.getRuntime().exec(cmd);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        return proc.waitFor(10, TimeUnit.MINUTES);
    }
}
