package com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.Encoding;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.Connection;

public class NetworkPhotoSender implements PhotoSender {
    private final Connection connection;

    public NetworkPhotoSender(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void send(Photo photo) {
        byte[] bytes = photo.toBytes();
        byte[] encodedFileSize = Encoding.encodeInt(bytes.length);
        connection.sendBytes(encodedFileSize);
        connection.sendBytes(bytes);
    }
}
