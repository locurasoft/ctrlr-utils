package com.locurasoft.utils;

public final class ByteUtils {
    private ByteUtils() {

    }

    public static byte[] reverse(byte[] array) {
        if (array == null) {
            return null;
        }
        byte[] buffer = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            buffer[array.length - (i + 1)] = array[i];
        }
        return buffer;
    }

    public static byte[] trimPatchname(String patchname, int length) {
        int bufferLength = length;
        byte[] bytes = patchname.getBytes();
        if (bytes.length < length) {
            bufferLength = bytes.length;
        }
        byte[] retval = new byte[length];
        System.arraycopy(bytes, 0, retval, 0, bufferLength);
        return retval;
    }

}
