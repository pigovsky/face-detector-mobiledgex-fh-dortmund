package com.pigovsky.face_detector_mobiledgex_fh_dortmund.face;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.Photo;

public class MockFaceDetector implements FaceDetector {
    @Override
    public Face detect(Photo photo) {
        return new Face(0, 1, 2, 3);
    }
}
