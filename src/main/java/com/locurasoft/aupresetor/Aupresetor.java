package com.locurasoft.aupresetor;

import com.locurasoft.utils.ProcessUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;

import static com.locurasoft.utils.ByteUtils.reverse;
import static com.locurasoft.utils.ByteUtils.trimPatchname;

/**
 * Hello world!
 */
public class Aupresetor {
    private static final int PATCH_NAME_LENGTH = 28;
    private static final String PACKAGE_PREFIX = "com.locurasoft.aupresetor.drivers";
    public static final String FXP2AUPRESET_SH = "./fxp2aupreset.app/Contents/MacOS/fxp2aupreset %s aumu CTRL INST jucePluginState";
    private static final String FXP2AUPRESET_CMD = "python ./fxp2aupreset/fxp2aupreset.py --type aumu --subtype CTRL --manufacturer INST --state_key jucePluginState \"%s\"";

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        String outputPath = args[1];
        String driverName = args[2];
        Class<?> driverClass = Class.forName(PACKAGE_PREFIX + "." + driverName);
        IPanelDriver driver = (IPanelDriver) driverClass.newInstance();

        File outputFolder = new File(outputPath);
        if (outputFolder.exists()) {
            System.out.println("Output folder already exists...");
            System.exit(0);
        } else {
            outputFolder.mkdir();
        }

        driver.setInputFolder(inputPath);
        driver.setOutputFolder(outputFolder.getAbsolutePath());

        HashSet<File> folders = new HashSet<>();
        Map<File, String> panels = driver.generatePanels();
        for (Map.Entry<File, String> entry : panels.entrySet()) {
            File bpanelz = writeBpanelz(entry.getKey());
            File fxpFile = writeFxp(bpanelz, entry.getValue());
            folders.add(fxpFile.getParentFile());
        }

        for (File folder : folders) {
            writeAupresets(folder);
        }
    }

    static File writeBpanelz(File panelFile) throws IOException, InterruptedException {
        String[] cmd = {"cmd.exe", "/C", String.format("Ctrlr-Debug-Win32.exe --panelFile=\"%s\"", panelFile)};
        if (ProcessUtils.execProcess(cmd)) {
            System.out.println("ctrlr Done!");
        } else {
            System.out.println("ctrlr failed!");
            return panelFile;
        }

        return new File(panelFile.getParent(), panelFile.getName().replace(".panel", ".bpanelz"));
    }


    static void writeAupresets(File folder) throws IOException, InterruptedException {
        String[] cmd = { "cmd.exe", "/C", String.format(FXP2AUPRESET_CMD, folder.getAbsolutePath()) };
        //        String[] cmd = {"/bin/sh", "-c", String.format(FXP2AUPRESET_CMD, outputPath)};
        if (ProcessUtils.execProcess(cmd)) {
            System.out.println("fxp2aupreset Done!");
        } else {
            System.out.println("fxp2aupreset failed!");
        }
    }

    static File writeFxp(File bpanelzFile, String patchName) throws IOException {
        File fxpFile = new File(bpanelzFile.getParent(), bpanelzFile.getName().replace(".bpanelz", ".fxp"));
        byte[] bpanelzBytes = IOUtils.toByteArray(new FileInputStream(bpanelzFile));
        FileOutputStream os = new FileOutputStream(fxpFile);
        os.write("CcnK".getBytes()); // chunkMagic
        os.write(ByteBuffer.allocate(4).putInt(bpanelzBytes.length + 62).array()); // size of chunk
        os.write("FPCh".getBytes()); // opaque chunk
        os.write(new byte[]{ 0x0, 0x0, 0x0, 0x1 }); // format version
        os.write("CTRL".getBytes()); // fx unique ID
        os.write(new byte[]{ 0x0, 0x0, 0x2, 0x1C }); // fx version
        os.write(new byte[]{ 0x0, 0x0, 0x0, 0x1 }); // num of parameters
        os.write(trimPatchname(patchName, PATCH_NAME_LENGTH));
        os.write(ByteBuffer.allocate(4).putInt(bpanelzBytes.length + 10).array());
        os.write(new byte[]{ 0x40, 0x10, 0x0, 0x0 }); // params
        os.write(reverse(ByteBuffer.allocate(4).putInt(bpanelzBytes.length).array())); // size of program data
        os.write(bpanelzBytes);
        os.write(new byte[]{ 0x0, 0x0 });
        os.flush();
        os.close();

        System.out.println("fxp Done!");
        return fxpFile;
    }

}
