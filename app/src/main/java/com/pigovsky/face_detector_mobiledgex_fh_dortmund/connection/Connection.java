package com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.Encoding;

public abstract class Connection {
    public abstract void sendBytes(byte[] data);

    public abstract byte[] receiveBytes(int length);

    public abstract void close();

    public int receiveInt() {
        return Encoding.decodeInt(receiveBytes(Encoding.SIZE_OF_INT));
    }
}
