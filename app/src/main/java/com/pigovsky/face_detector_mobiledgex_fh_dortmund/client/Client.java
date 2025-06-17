package com.pigovsky.face_detector_mobiledgex_fh_dortmund.client;

import android.graphics.Bitmap;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.DefaultConfiguration;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.Connection;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.SocketConnection;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.face.Face;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.face.FaceDetector;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.face.NetworkFaceDetector;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.*;

public class Client {
    private long lastRequestTime = 0;

    private final String serverHost;

    private final int serverPort;

    private Face face;

    public Client(String serverUrl) {
        if (serverUrl != null) {
            String[] hostPort = serverUrl.split(":");
            serverHost = hostPort[0];
            serverPort = Integer.parseInt(hostPort[1]);
        } else {
            serverHost = DefaultConfiguration.serverHost;
            serverPort = DefaultConfiguration.serverPort;
        }
    }

    public Face getFace() {
        return face;
    }

    public void detectFace(Photo photo) {
        if (System.currentTimeMillis() - lastRequestTime < DefaultConfiguration.requestInterval) {
            return ;
        }
        lastRequestTime = System.currentTimeMillis();
        new Thread(() -> {
            Connection clientToServerConnection = SocketConnection.clientConnection(serverHost, serverPort);
            FaceDetector faceDetector = new NetworkFaceDetector(clientToServerConnection);
            face = faceDetector.detect(photo);
            clientToServerConnection.close();
            System.out.println(face);
        }).start();
    }
}
