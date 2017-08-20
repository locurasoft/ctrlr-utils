package com.locurasoft.aupresetor.drivers;

import com.locurasoft.aupresetor.AbstractDriver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class BehringerModulizer1200DSP extends AbstractDriver {
    private static final int PATCH_FILE_SIZE = 10;

    public BehringerModulizer1200DSP() throws ParserConfigurationException, XPathExpressionException {
        super();
    }

    protected void generateFxp(Data data) throws Exception {
        byte[] inputBytes = data.getInputBytes();
        if (inputBytes.length == PATCH_FILE_SIZE) {
//            -- Assign values
            setModulatorIntValue(getNodeByName(data, "variation"), inputBytes[0]);
            setModulatorIntValue(getNodeByName(data, "editA"), inputBytes[1]);
            setModulatorIntValue(getNodeByName(data, "editB"), inputBytes[2]);
            setModulatorIntValue(getNodeByName(data, "editC"), inputBytes[3]);
            setModulatorIntValue(getNodeByName(data, "editD"), inputBytes[4]);
            setModulatorIntValue(getNodeByName(data, "effect"), inputBytes[5]);
            setModulatorIntValue(getNodeByName(data, "eqLow"), inputBytes[6]);
            setModulatorIntValue(getNodeByName(data, "eqHigh"), inputBytes[7]);
            setModulatorIntValue(getNodeByName(data, "mix"), inputBytes[8]);
            setModulatorIntValue(getNodeByName(data, "inOut"), inputBytes[9]);

            cleanTree(data);
            cloneResourcesToExport(data);

            saveFile(data, data.input().getName().replace(SYX, PANEL));
        } else {
            throw new IllegalArgumentException("Invalid Behringer Modulizer 1200 buffer");
        }
    }

}
