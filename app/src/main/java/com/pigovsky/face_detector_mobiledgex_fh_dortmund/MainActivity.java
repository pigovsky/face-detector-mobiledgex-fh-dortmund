package com.pigovsky.face_detector_mobiledgex_fh_dortmund;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.client.Client;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.common.DefaultConfiguration;
import com.pigovsky.face_detector_mobiledgex_fh_dortmund.photo.Photo;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CameraProcessingApp";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView mPreviewView;
    private ImageView mProcessedImageView;
    private ExecutorService mCameraExecutor;

    private Client client = new Client();

    private TextView errorMessageTextView;

    private Boolean needToRotatePhoto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText serverUrlEditText = findViewById(R.id.serverUrlEditText);
        serverUrlEditText.setText(DefaultConfiguration.serverHost + ":" + DefaultConfiguration.serverPort);
        client.setServerUrl(null);
        serverUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                client.setServerUrl(editable.toString());
            }
        });
        mPreviewView = findViewById(R.id.viewFinder);
        mProcessedImageView = findViewById(R.id.processedImageView);

        mCameraExecutor = Executors.newSingleThreadExecutor();

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        SeekBar frameRatioSeekBar = findViewById(R.id.frameRatioSeekBar);
        frameRatioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // double selectedRatioValue = ratioValues.get(progress);
                    // Log.d("FrameRatio", "Selected ratio value: " + selectedRatioValue);
                    // TODO: Apply the selected frame ratio
                    client.setRequestInterval(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional
                // int finalProgress = seekBar.getProgress();
                // String finalSelectedRatioString = availableRatios.get(finalProgress);
                // Toast.makeText(FrameRatioActivity.this, "Final Ratio: " + finalSelectedRatioString, Toast.LENGTH_SHORT).show();
            }
        });

        errorMessageTextView = findViewById(R.id.errorMessageTextView);
    }

    private void startCamera() {
        // This is a singleton that can be used to bind the lifecycle of cameras to any LifecycleOwner.
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // CameraProvider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview Use Case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

                // ImageAnalysis Use Case
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // Set the analyzer for the ImageAnalysis use case
                imageAnalysis.setAnalyzer(mCameraExecutor, new ImageProcessor());

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private class ImageProcessor implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(@NonNull ImageProxy image) {
            // Convert the ImageProxy to a Bitmap
            Bitmap bitmap = imageProxyToBitmap(image);
            if (bitmap != null) {
                bitmap = needToRotatePhoto ?
                    rotatePhoto(bitmap) :
                    bitmap;

                // Process the Bitmap
                Photo photo = new Photo(bitmap);
                client.detectFace(photo);
                photo.highlightFace(client.getFace());

                // Display the processed bitmap in the ImageView
                runOnUiThread(
                    () -> {
                        mProcessedImageView.setImageBitmap(photo.getBitmap());
                        errorMessageTextView.setText(client.getErrorMessage());
                    }
                );
            }
            // VERY IMPORTANT: Close the image to let the next one be processed
            image.close();
        }

        private Bitmap rotatePhoto(Bitmap bitmap) {
            Matrix matrix = new Matrix();
            matrix.setRotate(-90); // For simple rotation
            // Or matrix.postRotate(degrees); if chaining transformations

            try {
                // Create a new bitmap with the rotation applied
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError e) {
                // Handle the error, e.g., log it and return the original or null
                Log.e("Rotating photo", "face-detector", e);
            }
            return bitmap;
        }
    }

    /**
     * Converts an ImageProxy in YUV_420_888 format to a Bitmap.
     */
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        android.graphics.YuvImage yuvImage = new android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /**
     * Converts a color Bitmap to a grayscale Bitmap.
     */
    private Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width = bmpOriginal.getWidth();
        int height = bmpOriginal.getHeight();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraExecutor.shutdown();
    }
}