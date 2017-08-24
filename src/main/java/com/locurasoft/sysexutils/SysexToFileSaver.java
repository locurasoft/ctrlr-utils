package com.locurasoft.sysexutils;

import com.locurasoft.utils.SysexUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SysexToFileSaver {

    public static void main(String[] args) throws IOException {
        File file = new File("out.syx");
        FileOutputStream fos = new FileOutputStream(file);
        String sysex = args[0];
        byte[] bytes = SysexUtils.stringToSysex(sysex);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

}
