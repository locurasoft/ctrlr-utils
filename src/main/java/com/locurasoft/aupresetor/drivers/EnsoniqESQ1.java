package com.locurasoft.aupresetor.drivers;

import com.google.common.collect.ImmutableMap;
import com.locurasoft.aupresetor.AbstractDriver;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class EnsoniqESQ1 extends AbstractDriver {
    private static final ImmutableMap<Integer, Integer> ADD_SUBT_OFFSETS = ImmutableMap.<Integer, Integer>builder()
            .put(6, 64)
            .put(8, 64)
            .put(12, 64)
            .put(14, 64)
            .put(21, 64)
            .put(23, 64)
            .put(28, 64)
            .put(30, 64)
            .put(36, 64)
            .put(38, 64)
            .put(42, 64)
            .put(44, 64)
            .put(49, 64)
            .put(51, 64)
            .put(55, 64)
            .put(70, 64)
            .put(72, 64)
            .put(74, 64)
            .put(80, 64)
            .put(82, 64)
            .put(84, 64)
            .put(90, 64)
            .put(92, 64)
            .put(94, 64)
            .put(100, 64)
            .put(102, 64)
            .put(104, 64).build();

    private static final int[][] paramSpecifications = {
            {1, 0, 127, 0, 0, 121},
            {2, 0, 127, 0, 0, 121},
            {0, 3, 248, 0, 128, 123},
            {0, 0, 255, 0, 128, 131},
            {0, 0, 15, 0, 8, 125},
            {0, 1, 254, 1, 128, 127},
            {0, 4, 240, 0, 128, 125},
            {0, 1, 254, 1, 128, 129},
            {0, 1, 126, 0, 64, 133},
            {0, 7, 128, 0, 128, 133},
            {0, 0, 15, 0, 8, 135},
            {0, 1, 254, 1, 128, 137},
            {0, 4, 240, 0, 128, 135},
            {0, 1, 254, 1, 128, 139},
            {1, 0, 127, 0, 0, 141},
            {2, 0, 127, 0, 0, 141},
            {0, 3, 248, 0, 128, 143},
            {0, 0, 255, 0, 128, 151},
            {0, 7, 128, 0, 128, 183},
            {0, 0, 15, 0, 8, 145},
            {0, 1, 254, 1, 128, 147},
            {0, 4, 240, 0, 128, 145},
            {0, 1, 254, 1, 128, 149},
            {0, 7, 128, 0, 128, 181},
            {0, 1, 126, 0, 64, 153},
            {0, 7, 128, 0, 128, 153},
            {0, 0, 15, 0, 8, 155},
            {0, 1, 254, 1, 128, 157},
            {0, 4, 240, 0, 128, 155},
            {0, 1, 254, 1, 128, 159},
            {1, 0, 127, 0, 0, 161},
            {2, 0, 127, 0, 0, 161},
            {0, 3, 248, 0, 128, 163},
            {0, 0, 255, 0, 128, 171},
            {0, 0, 15, 0, 8, 165},
            {0, 1, 254, 1, 128, 167},
            {0, 4, 240, 0, 128, 165},
            {0, 1, 254, 1, 128, 169},
            {0, 1, 126, 0, 64, 173},
            {0, 7, 128, 0, 128, 173},
            {0, 0, 15, 0, 8, 175},
            {0, 1, 254, 1, 128, 177},
            {0, 4, 240, 0, 128, 175},
            {0, 1, 254, 1, 128, 179},
            {0, 0, 127, 0, 64, 183},
            {0, 0, 31, 0, 16, 185},
            {0, 1, 126, 0, 64, 193},
            {0, 0, 15, 0, 8, 187},
            {0, 0, 127, 1, 64, 189},
            {0, 4, 240, 0, 128, 187},
            {0, 0, 127, 1, 64, 191},
            {0, 4, 240, 0, 128, 205},
            {0, 0, 127, 0, 64, 181},
            {0, 0, 15, 0, 8, 205},
            {0, 0, 127, 1, 64, 207},
            {0, 0, 63, 0, 32, 195},
            {0, 7, 128, 0, 128, 191},
            {0, 7, 128, 0, 128, 189},
            {0, 7, 128, 0, 128, 193},
            {0, 7, 128, 0, 128, 195},
            {0, 7, 128, 0, 128, 207},
            {4, 0, 127, 0, 0, 197},
            {5, 0, 128, 0, 0, 201},
            {0, 0, 127, 0, 64, 201},
            {0, 7, 128, 0, 128, 199},
            {0, 0, 127, 0, 64, 199},
            {0, 7, 128, 0, 128, 203},
            {0, 0, 127, 0, 64, 203},
            {0, 0, 63, 0, 32, 23},
            {0, 1, 254, 1, 128, 17},
            {0, 0, 63, 0, 32, 25},
            {0, 1, 254, 1, 128, 19},
            {0, 0, 63, 0, 32, 27},
            {0, 1, 254, 1, 128, 21},
            {0, 0, 63, 0, 32, 29},
            {0, 2, 252, 0, 128, 31},
            {0, 0, 63, 0, 32, 33},
            {0, 0, 63, 0, 32, 35},
            {0, 0, 63, 0, 32, 43},
            {0, 1, 254, 1, 128, 37},
            {0, 0, 63, 0, 32, 45},
            {0, 1, 254, 1, 128, 39},
            {0, 0, 63, 0, 32, 47},
            {0, 1, 254, 1, 128, 41},
            {0, 0, 63, 0, 32, 49},
            {0, 2, 252, 0, 128, 51},
            {0, 0, 63, 0, 32, 53},
            {0, 0, 63, 0, 32, 55},
            {0, 0, 63, 0, 32, 63},
            {0, 1, 254, 1, 128, 57},
            {0, 0, 63, 0, 32, 65},
            {0, 1, 254, 1, 128, 59},
            {0, 0, 63, 0, 32, 67},
            {0, 1, 254, 1, 128, 61},
            {0, 0, 63, 0, 32, 69},
            {0, 2, 252, 0, 128, 71},
            {0, 0, 63, 0, 32, 73},
            {0, 0, 63, 0, 32, 75},
            {0, 0, 63, 0, 32, 83},
            {0, 1, 254, 1, 128, 77},
            {0, 0, 63, 0, 32, 85},
            {0, 1, 254, 1, 128, 79},
            {0, 0, 63, 0, 32, 87},
            {0, 1, 254, 1, 128, 81},
            {0, 0, 63, 0, 32, 89},
            {0, 2, 252, 0, 128, 91},
            {0, 0, 63, 0, 32, 93},
            {0, 0, 63, 0, 32, 95},
            {0, 0, 63, 0, 32, 97},
            {0, 6, 192, 0, 128, 97},
            {0, 6, 64, 0, 64, 103},
            {0, 7, 128, 0, 128, 103},
            {3, 0, 192, 0, 0, 99},
            {0, 0, 63, 0, 32, 99},
            {0, 0, 63, 0, 32, 103},
            {0, 0, 63, 0, 32, 101},
            {0, 0, 63, 0, 32, 105},
            {0, 6, 192, 0, 128, 105},
            {0, 6, 64, 0, 64, 111},
            {0, 7, 128, 0, 128, 111},
            {3, 0, 192, 0, 0, 107},
            {0, 0, 63, 0, 32, 107},
            {0, 0, 63, 0, 32, 111},
            {0, 0, 63, 0, 32, 109},
            {0, 0, 63, 0, 32, 113},
            {0, 6, 192, 0, 128, 113},
            {0, 6, 64, 0, 64, 119},
            {0, 7, 128, 0, 128, 119},
            {3, 0, 192, 0, 0, 115},
            {0, 0, 63, 0, 32, 115},
            {0, 0, 63, 0, 32, 119},
            {0, 0, 63, 0, 32, 117}
    };

    private static final int BANK_FILE_SIZE = 8166;
    private static final int PATCH_FILE_SIZE = 210;
    private static final int SINGLE_DATA_SIZE = 204;
    private static final int NUM_PATCHES = 40;
    private static final int COMPLETE_HEADER_SIZE = 5;

    public EnsoniqESQ1() throws ParserConfigurationException, XPathExpressionException {
        super();
    }

    private void p2v(Data data, Patch patch) throws XPathExpressionException {
//  -- gets the voice parameter values
        for (int i = 1; i < 132; i++) {
            String name = String.format("Voice%d", i);
            Node mod = getNodeByCustName(data, name);
            if (mod != null) {
                setModulatorIntValue(mod, patch.getValue(i));
            }
        }
        setModulatorStringValue(getNodeByName(data, "Name1"), patch.getPatchName());
    }


    @Override
    protected void generateFxp(Data data) throws Exception {
        byte[] inputBytes = data.getInputBytes();
        if (inputBytes.length == PATCH_FILE_SIZE) {
            Patch patch = new Patch(inputBytes);
            p2v(data, patch);
            saveFile(data, data.input().getName().replace(SYX, PANEL));
        } else if (inputBytes.length == BANK_FILE_SIZE) {
            for (int i = 0; i < NUM_PATCHES; i++) {
                byte[] buffer = new byte[SINGLE_DATA_SIZE];
                System.arraycopy(inputBytes, COMPLETE_HEADER_SIZE + i * SINGLE_DATA_SIZE, buffer, 0, SINGLE_DATA_SIZE);
                Patch patch = new Patch(buffer);
                p2v(data, patch);
                saveFile(data, data.input().getName().replace(SYX, "") + " - " +
                        patch.getPatchName().trim() + PANEL);
            }
        } else {
            throw new IllegalArgumentException("Invalid Ensoniq ESQ-1 buffer");
        }

    }

    static class Patch {
        private byte[] data;

        public Patch(byte[] data) {
            this.data = data;
        }

        String getPatchName() {
            //  -- This method fetches the patch name from the hidden
            String patchName = "";
            for (int i = 0; i < 5; i++) {
                patchName = String.format("%s%c", patchName, data[i * 2] + data[i * 2 + 1] * 16);
            }
            return patchName;
        }

        int getValue(int index) {
            int[] spec = paramSpecifications[index];
            int type = spec[0];
            int shift = spec[1];
            int bitmask = spec[2];
            boolean signed = spec[3] == 1;
            int signmask = spec[4];
            int offset = spec[5] - 5;

            int modValue = 0;
            int j = data[offset] + data[offset + 1] * 16;

            switch (type) {
                case 0:
//                    -- The general case
                    if (signed && (j & signmask) != 0) {
//                    -- If the parameter is signed AND negative
//                        bit.arshift(bit.bor(bit.band(-1, bit.bnot(bitmask)), bit.band(j, bitmask)), shift);
                        modValue = (((-1 & ~bitmask) | (j & bitmask)) >> shift);
                    } else {
//            -- If the parameter is positive or not signed
//                        bit.rshift(bit.band(j, bitmask), shift);
                        modValue = ((j & bitmask) >>> shift);
                    }
                    break;
                case 1:
//                    -- Octave
                    modValue = (int) Math.floor(j / 12);
                    break;
                case 2:
//                    -- Semi
                    modValue = j % 12;
                    break;
                case 3:
//                    -- LFO Mod Src
                    int k = data[offset + 2] + data[offset + 3] * 16;
//                    bit.rshift(bit.band(j, bitmask), 4) + bit.rshift(bit.band(k, bitmask), 6);
                    modValue = (((j & bitmask) >>> 4) + ((k & bitmask) >>> 6));
                    break;
                case 4:
//                    -- Split Point
//                    modValue = j -- math.max(bit.band(j, bitmask), 21) - 21;
                    modValue = Math.max((j & bitmask), 21) - 21;  // Silently correct if stored value is below value range (<21)
                    break;
                case 5:
//                    -- Split Direction
                    if ((j & bitmask) == 0) {
                        modValue = 0;
                    } else if ((((byte) (data[offset - 4] + data[offset - 3] * 16)) & (byte) bitmask) == 0) {
                        modValue = 1;
                    } else {
                        modValue = 2;
                    }
                    break;
                default:
//            log:warn("Weird param type %d", type)
                    return -1;
            }

            if (ADD_SUBT_OFFSETS.containsKey(index)) {
                modValue = modValue + ADD_SUBT_OFFSETS.get(index);
            }

            return modValue;
        }
    }
}
