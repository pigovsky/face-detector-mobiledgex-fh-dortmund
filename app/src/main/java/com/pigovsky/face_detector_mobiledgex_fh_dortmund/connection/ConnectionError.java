package com.pigovsky.face_detector_mobiledgex_fh_dortmund.connection;

public class ConnectionError extends RuntimeException {
    public ConnectionError(Throwable throwable) {
        super(throwable);
    }
}
