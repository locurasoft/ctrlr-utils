package com.locurasoft.aupresetor.drivers;

import com.locurasoft.aupresetor.AbstractPanelDriver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class BehringerModulizer1200DSP extends AbstractPanelDriver {
    private static final int PATCH_FILE_SIZE = 10;

    public BehringerModulizer1200DSP() throws ParserConfigurationException, XPathExpressionException {
        super();
    }

    protected void generateFxp(Data data) throws Exception {
        byte[] inputBytes = data.getInputBytes();
        if (inputBytes.length == PATCH_FILE_SIZE) {
//            -- Assign values
            setModIntValue(nodeByName(data, "variation"), inputBytes[0]);
            setModIntValue(nodeByName(data, "editA"), inputBytes[1]);
            setModIntValue(nodeByName(data, "editB"), inputBytes[2]);
            setModIntValue(nodeByName(data, "editC"), inputBytes[3]);
            setModIntValue(nodeByName(data, "editD"), inputBytes[4]);
            setModIntValue(nodeByName(data, "effect"), inputBytes[5]);
            setModIntValue(nodeByName(data, "eqLow"), inputBytes[6]);
            setModIntValue(nodeByName(data, "eqHigh"), inputBytes[7]);
            setModIntValue(nodeByName(data, "mix"), inputBytes[8]);
            setModIntValue(nodeByName(data, "inOut"), inputBytes[9]);

            cleanTree(data);
            cloneResourcesToExport(data);

            saveFile(data, data.input().getName().replace(SYX, PANEL));
        } else {
            throw new IllegalArgumentException("Invalid Behringer Modulizer 1200 buffer");
        }
    }

}
