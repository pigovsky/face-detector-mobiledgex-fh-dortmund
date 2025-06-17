package com.pigovsky.face_detector_mobiledgex_fh_dortmund.face;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.Encoding;

public class Face {
    private final int left;
    private final int top;
    private final int width;
    private final int height;

    public Face(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getRight() {
        return left + width;
    }

    public int getHeight() {
        return height;
    }

    public int getBottom() {
        return top + height;
    }

    public byte[] toBytes() {
        byte[] result = new byte[Encoding.SIZE_OF_INT * 4];
        Encoding.encodeInt(left, Encoding.SIZE_OF_INT, result, 0);
        Encoding.encodeInt(top, Encoding.SIZE_OF_INT, result, Encoding.SIZE_OF_INT);
        Encoding.encodeInt(width, Encoding.SIZE_OF_INT, result, Encoding.SIZE_OF_INT * 2);
        Encoding.encodeInt(height, Encoding.SIZE_OF_INT, result, Encoding.SIZE_OF_INT * 3);
        return result;
    }

    @Override
    public String toString() {
        return "Face{" +
                "left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
