package com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketConnection extends Connection {
    private final Socket socket;

    private final InputStream inputStream;

    private final OutputStream outputStream;

    public static SocketConnection clientConnection(String serverHost, int serverPort) {
        try {
            return new SocketConnection(new Socket(serverHost, serverPort));
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }

    public SocketConnection(Socket socket) {
        try {
            this.socket = socket;
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void sendBytes(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public byte[] receiveBytes(int length) {
        try {
            return inputStream.readNBytes(length);
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }
}
