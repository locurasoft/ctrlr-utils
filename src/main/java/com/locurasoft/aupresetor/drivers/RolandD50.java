package com.locurasoft.aupresetor.drivers;

import com.google.common.collect.ImmutableMap;
import com.locurasoft.aupresetor.AbstractDriver;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class RolandD50 extends AbstractDriver {

    private static final int Voice_singleSize = 448;
    private static final int PATCH_FILE_SIZE = 458;
    private static final int BANK_FILE_SIZE = 36048;
    private static final int PATCH_NAME_OFFSET = 384;
    private static final int PATCH_NAME_LENGTH = 18;

    private static final int UPPER_TONE_OFFSET = 128;
    private static final int LOWER_TONE_OFFSET = 320;
    private static final int TONE_NAME_LENGTH = 10;
    private static final int UpperPartialSelectIndex = 174;
    private static final int LowerPartialSelectIndex = 366;


    private static final ImmutableMap<Integer, Integer> SPECIAL_OFFSETS = ImmutableMap.<Integer, Integer>builder()
            .put(228, 50)
            .put(257, 50)
            .put(292, 50)
            .put(407, 24)
            .put(408, 50)
            .put(409, 50)
            .put(406, 24)
            .put(411, 12)
            .put(145, 50)
            .put(146, 50)
            .put(147, 50)
            .put(148, 50)
            .put(149, 50)
            .put(166, 12)
            .put(169, 12)
            .put(1, 50)
            .put(36, 50)
            .put(65, 50)
            .put(100, 50)
            .put(337, 50)
            .put(338, 50)
            .put(339, 50)
            .put(340, 50)
            .put(341, 50)
            .put(358, 12)
            .put(361, 12)
            .put(193, 50)
            .put(17, 7)
            .put(152, 7)
            .put(53, 7)
            .put(34, 7)
            .put(9, 7)
            .put(12, 7)
            .put(117, 7)
            .put(81, 7)
            .put(98, 7)
            .put(73, 7)
            .put(76, 7)
            .put(344, 7)
            .put(245, 7)
            .put(209, 7)
            .put(226, 7)
            .put(201, 7)
            .put(204, 7)
            .put(309, 7)
            .put(273, 7)
            .put(290, 7)
            .put(265, 7)
            .put(268, 7).build();


    public RolandD50() throws ParserConfigurationException, XPathExpressionException {
        super();
    }

    private byte[] trimSyxData(byte[] buffer) {
        int dataSize = buffer.length;
        int cleanIndex = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] cleanData = new byte[dataSize];
        int i = 0;
        while (i < dataSize) {
//            --gets the voice parameter values
            if (buffer[i] == (byte) 0xF0) {
                i += 8;
            } else if (buffer[i + 1] == (byte) 0xF7) {
                i += 2;
            } else {
                byteArrayOutputStream.write(buffer[i]);
                i += 1;
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    //    -- This method assigns patch data from a memory block
//-- to all modulators in the panel
    private void p2v(Data data, Patch patch) throws XPathExpressionException {
//  -- gets the voice parameter values
        Node nodeByName = getNodeByName(data, "toneSelector");
        nodeByName.setNodeValue(Integer.toString(0));

        for (int i = 0; i < Voice_singleSize; i++) {
            Node mod = getNodeByCustName(data, String.format("Voice%d", i));
            if (mod != null && i != 174 && i != 366) {
//                System.out.println(String.format("Voice%d", i) + " -> " + patch.getValue(i));
                setModulatorIntValue(mod, patch.getValue(i));
            }
        }

        setModulatorIntValue(getNodeByName(data, "UpperPartial1"), patch.getUpperPartial1Value());
        setModulatorIntValue(getNodeByName(data, "UpperPartial2"), patch.getUpperPartial2Value());
        setModulatorIntValue(getNodeByName(data, "LowerPartial1"), patch.getLowerPartial1Value());
        setModulatorIntValue(getNodeByName(data, "LowerPartial2"), patch.getLowerPartial2Value());

//        --Set Patch name
        setModulatorStringValue(getNodeByName(data, "Name1"), patch.getPatchName());
        setModulatorStringValue(getNodeByName(data, "VoiceName12"), patch.getUpperToneName());
        setModulatorStringValue(getNodeByName(data, "VoiceName123"), patch.getLowerToneName());

        dumpToFile(data.toString(), "1.panel");
        cleanTree(data);
        dumpToFile(data.toString(), "2.panel");
        cloneResourcesToExport(data);
        dumpToFile(data.toString(), "3.panel");
    }

    private void dumpToFile(String s, String filename) {
        try(  PrintWriter out = new PrintWriter( filename )  ){
            out.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void generateFxp(Data data) throws Exception {
        byte[] inputBytes = data.getInputBytes();
        byte[] trimSyxData = trimSyxData(inputBytes);
        if (inputBytes.length == PATCH_FILE_SIZE) {
            Patch patch = newPatch(trimSyxData, 0);
            p2v(data, patch);
            saveFile(data, data.input().getName().replace(SYX, PANEL));
        } else if (inputBytes.length == BANK_FILE_SIZE) {
            for (int i = 0; i < 64; i++) {
                Patch patch = newPatch(trimSyxData, i);
                p2v(data, patch);
                saveFile(data, data.input().getName().replace(SYX, "") + " - " +
                        patch.getPatchName().trim() + PANEL);
            }
        } else {
            throw new IllegalArgumentException("Invalid Roland D50 buffer");
        }
    }

    private Patch newPatch(byte[] buffer, int index) {
        byte[] bytes = new byte[Voice_singleSize];
        System.arraycopy(buffer, index * Voice_singleSize, bytes, 0, Voice_singleSize);
        return new Patch(bytes);
    }

    static class Patch {

        private byte[] data;

        public Patch(byte[] data) {
            this.data = data;
        }

        int getValue(int offset) {
            int value = data[offset];
            if (SPECIAL_OFFSETS.containsKey(offset)) {
                value -= SPECIAL_OFFSETS.get(offset);
            }
            return value;
        }

        int getPartial1Value(int value) {
            if (value == 1 || value == 3) {
                return 1;
            } else {
                return 0;
            }
        }

        int getPartial2Value(int value) {
            if (value > 1) {
                return 1;
            } else {
                return 0;
            }
        }

        // -- This method fetches the patch name from the hidden
        // -- char modulators and returns it as a string
        String getD50String(byte[] data, int patchNameStart, int patchNameSize) {
            String name = "";
            String[] symbols = {" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-"};
            for (int i = patchNameStart; i < (patchNameStart + patchNameSize - 1); i++) {
                byte midiParam = data[i];
                name = String.format("%s%s", name, symbols[midiParam]);
            }
            return name;
        }

        String getPatchName() {
            return getD50String(data, PATCH_NAME_OFFSET, PATCH_NAME_LENGTH);
        }

        String getUpperToneName() {
            return getD50String(data, UPPER_TONE_OFFSET, TONE_NAME_LENGTH);
        }

        String getLowerToneName() {
            return getD50String(data, LOWER_TONE_OFFSET, TONE_NAME_LENGTH);
        }

        int getUpperPartial1Value() {
            return getPartial1Value(data[UpperPartialSelectIndex]);
        }

        int getUpperPartial2Value() {
            return getPartial2Value(data[UpperPartialSelectIndex]);
        }

        int getLowerPartial1Value() {
            return getPartial1Value(data[LowerPartialSelectIndex]);
        }

        int getLowerPartial2Value() {
            return getPartial2Value(data[LowerPartialSelectIndex]);
        }
    }
}
