package com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo;

import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.DefaultConfiguration;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.face.Face;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Photo {
    private Bitmap bitmap;

    public Photo(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static Photo fromBytes(byte[] data) {
        Bitmap bitmap = DefaultConfiguration.mock ? null :
            BitmapFactory.decodeByteArray(data, 0, data.length);
        return new Photo(bitmap);
    }

    public byte[] toBytes() {
        if (DefaultConfiguration.mock) {
            return new byte[0];
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)) {
                return stream.toByteArray();
            } else {
                Log.e("BitmapUtils", "Bitmap compression failed.");
                return null;
            }
        } catch (Exception e) {
            Log.e("BitmapUtils", "Error during Bitmap compression: " + e.getMessage());
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e("BitmapUtils", "Error closing ByteArrayOutputStream: " + e.getMessage());
            }
        }
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void highlightFace(Face face) {
        if (face == null) {
            return;
        }
        // 1. Create a mutable copy if the original is immutable
        Bitmap mutableBitmap;
        if (bitmap.isMutable()) {
            mutableBitmap = bitmap;
        } else {
            mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        if (mutableBitmap == null) {
            // Failed to create a mutable bitmap
            return; // Or handle error appropriately
        }

        // 2. Create a Canvas associated with the mutable Bitmap
        Canvas canvas = new Canvas(mutableBitmap);

        // 3. Create a Paint object for the rectangle
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE); // Draw only the outline
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true); // For smoother edges

        // 4. Define the rectangle's coordinates
        // Example: A rectangle taking up a portion of the bitmap
        float bitmapWidth = mutableBitmap.getWidth();
        float bitmapHeight = mutableBitmap.getHeight();

        // You can also use a RectF object
        // RectF rectangle = new RectF(left, top, right, bottom);

        // Draw the rectangle
        canvas.drawRect(face.getLeft(), face.getTop(), face.getRight(), face.getBottom(), paint);
        // Alternatively, if using RectF:
        // canvas.drawRect(rectangle, paint);
        bitmap = mutableBitmap;
    }
}
