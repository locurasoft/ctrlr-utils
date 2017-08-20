package com.locurasoft.aupresetor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class Main {
    private static final String PACKAGE_PREFIX = "com.locurasoft.aupresetor.drivers";
    public static final String FXP2AUPRESET_CMD = "./fxp2aupreset.app/Contents/MacOS/fxp2aupreset %s aumu CTRL INST jucePluginState";

    public static void main(String[] args) throws Exception {
        String inputFolder = args[0];
        String outputFolder = args[1];
        String driverName = args[2];
        Class<?> driverClass = Class.forName(PACKAGE_PREFIX + "." + driverName);
        IDriver driver = (IDriver) driverClass.newInstance();

        driver.setInputFolder(inputFolder);
        driver.setOutputFolder(outputFolder);
        driver.generateFxps();

        String[] cmd = {"/bin/sh", "-c", String.format(FXP2AUPRESET_CMD, outputFolder)};
        if (ProcessUtil.execProcess(cmd)) {
            System.out.println("Done!");
        } else {
            System.out.println("fxp2aupreset failed!");
        }
    }
}
