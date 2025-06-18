package com.pigovsky.face_detector_mobiledgex_fh_dortmund.client;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.DefaultConfiguration;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.Connection;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection.ConnectionError;
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

    private long requestInterval = DefaultConfiguration.requestInterval;

    private String errorMessage;

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setRequestInterval(long requestInterval) {
        this.requestInterval = requestInterval;
    }

    public Face getFace() {
        return face;
    }

    public void detectFace(Photo photo) {
        if (System.currentTimeMillis() - lastRequestTime < requestInterval) {
            return ;
        }
        lastRequestTime = System.currentTimeMillis();
        new Thread(() -> {
            try {
                Connection clientToServerConnection = SocketConnection.clientConnection(serverHost, serverPort);
                FaceDetector faceDetector = new NetworkFaceDetector(clientToServerConnection);
                face = faceDetector.detect(photo);
                clientToServerConnection.close();
                System.out.println(face);
                errorMessage = null;
            } catch (ConnectionError e) {
                errorMessage = e.getMessage();
            }
        }).start();
    }
}
