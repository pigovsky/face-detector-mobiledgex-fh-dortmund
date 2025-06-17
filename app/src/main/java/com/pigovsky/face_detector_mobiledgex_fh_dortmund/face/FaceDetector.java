package com.pigovsky.face_detector_mobiledgex_fh_dortmund.face;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.Photo;

public interface FaceDetector {
    Face detect(Photo photo);
}
