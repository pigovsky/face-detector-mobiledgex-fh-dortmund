package com.pigovsky.face_detector_mobiledgex_fh_dortmund.common;

public class Encoding {
    public static final int SIZE_OF_INT = 4;

    public static int decodeInt(byte[] data){
        int result = data[data.length-1] & 0xFF;
        for(int i=data.length-2; i>=0; i--) {
            result <<= 8;
            result |= data[i] & 0xFF;
        }
        return result;
    }

    public static byte[] encodeInt(int value) {
        return encodeInt(value, Encoding.SIZE_OF_INT);
    }

    public static byte[] encodeInt(int value, int length) {
        byte[] result = new byte[length];
        return encodeInt(value, length, result, 0);
    }

    public static byte[] encodeInt(int value, int length, byte[] result, int startingIndex) {
        for(int i=startingIndex; i<startingIndex + length; i++) {
            result[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        return result;
    }
}
