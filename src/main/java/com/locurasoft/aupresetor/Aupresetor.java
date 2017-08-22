package com.locurasoft.aupresetor;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Hello world!
 */
public class Aupresetor {
    private static final String PACKAGE_PREFIX = "com.locurasoft.aupresetor.drivers";
    public static final String FXP2AUPRESET_CMD = "./fxp2aupreset.app/Contents/MacOS/fxp2aupreset %s aumu CTRL INST jucePluginState";

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        String outputPath = args[1];
        String driverName = args[2];
        Class<?> driverClass = Class.forName(PACKAGE_PREFIX + "." + driverName);
        IDriver driver = (IDriver) driverClass.newInstance();

        File outputFolder = new File(outputPath);
//        File aupresets = new File(outputFolder, "panel");
        if (outputFolder.exists()) {
            System.out.println("Output folder already exists...");
            System.exit(0);
        } else {
            outputFolder.mkdir();
        }

        driver.setInputFolder(inputPath);
        driver.setOutputFolder(outputFolder.getAbsolutePath());
        driver.generateFxps();

//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outputFolder, "fxp2aupreset.sh"))));
//        writer.println("#!/bin/bash");
//        writer.println();
//        writer.println("");
//
//        String[] cmd = {"/bin/sh", "-c", String.format(FXP2AUPRESET_CMD, outputPath)};
//        if (ProcessUtil.execProcess(cmd)) {
//            System.out.println("Done!");
//        } else {
//            System.out.println("fxp2aupreset failed!");
//        }
    }
}
