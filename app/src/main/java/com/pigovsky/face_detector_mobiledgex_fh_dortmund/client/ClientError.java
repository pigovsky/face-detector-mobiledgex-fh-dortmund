package com.pigovsky.face_detector_mobiledgex_fh_dortmund.client;

public class ClientError extends RuntimeException {
    public ClientError(Throwable throwable) {
        super(throwable);
    }

    public ClientError(String message) {
        super(message);
    }
}
