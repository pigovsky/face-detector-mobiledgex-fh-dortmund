package com.pigovsky.face_detector_mobiledgex_fh_dortmund.face;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.Encoding;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.Connection;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.NetworkPhotoSender;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.Photo;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.PhotoSender;

public class NetworkFaceDetector implements FaceDetector {
    private final Connection clientToServerConnection;

    public NetworkFaceDetector(Connection clientToServerConnection) {
        this.clientToServerConnection = clientToServerConnection;
    }

    @Override
    public Face detect(Photo photo) {
        PhotoSender photoSender = new NetworkPhotoSender(clientToServerConnection);
        photoSender.send(photo);
        return new Face(
            clientToServerConnection.receiveInt(),
            clientToServerConnection.receiveInt(),
            clientToServerConnection.receiveInt(),
            clientToServerConnection.receiveInt()
        );
    }
}
