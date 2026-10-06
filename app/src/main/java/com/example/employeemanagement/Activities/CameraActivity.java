package com.example.employeemanagement.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.employeemanagement.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private PreviewView cameraPreview;
    private ImageView capturedImage;

    private Button captureButton;
    private Button saveButton;
    private Button retakeButton;

    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private File photoFile;

    private boolean isCameraReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        cameraPreview = findViewById(R.id.pv_cameraPreview);
        capturedImage = findViewById(R.id.iv_capturedImage);

        captureButton = findViewById(R.id.btn_captureButton);
        saveButton = findViewById(R.id.btn_saveImage);
        retakeButton = findViewById(R.id.btn_retakeImage);



        capturedImage.setVisibility(View.GONE);

        saveButton.setVisibility(View.GONE);

        retakeButton.setVisibility(View.GONE);


        captureButton.setEnabled(false);


        captureButton.setOnClickListener(v -> {
            capturePhoto();
        });


        retakeButton.setOnClickListener(v -> {
            retakePhoto();
        });



        saveButton.setOnClickListener(v -> {

            Toast.makeText(
                    CameraActivity.this,
                    "Save functionality will be implemented next",
                    Toast.LENGTH_SHORT
            ).show();

        });


        startCamera();
    }


    private void startCamera() {

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();


                Preview preview =
                        new Preview.Builder()
                                .build();

                int rotation = cameraPreview.getDisplay().getRotation();
                imageCapture =
                        new ImageCapture.Builder()
                                .setTargetRotation(rotation)
                                .build();

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_FRONT_CAMERA;


                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider()
                );

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );


                isCameraReady = true;

                captureButton.setEnabled(true);


            } catch (Exception e) {

                isCameraReady = false;

                captureButton.setEnabled(false);

                Toast.makeText(
                        CameraActivity.this,
                        "Failed to start camera",
                        Toast.LENGTH_SHORT
                ).show();

                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }


    private void capturePhoto() {



        if (!isCameraReady || imageCapture == null) {

            Toast.makeText(
                    CameraActivity.this,
                    "Camera is not ready yet",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        photoFile = new File(
                getCacheDir(),
                "captured_image.jpg"
        );



        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        photoFile
                ).build();

        runOnUiThread(() -> {

        });



        imageCapture.takePicture(
                outputFileOptions,

                ContextCompat.getMainExecutor(this),

                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults outputFileResults) {


                        Bitmap bitmap =
                                BitmapFactory.decodeFile(
                                        photoFile.getAbsolutePath()
                                );


                        if (bitmap != null) {


                            capturedImage.setImageBitmap(bitmap);

                            cameraPreview.setVisibility(View.GONE);

                            capturedImage.setVisibility(View.VISIBLE);

                            captureButton.setVisibility(View.GONE);

                            retakeButton.setVisibility(View.VISIBLE);
                            saveButton.setVisibility(View.VISIBLE);


                        } else {

                            Toast.makeText(
                                    CameraActivity.this,
                                    "Failed to load captured image",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }


                    @Override
                    public void onError(
                            @NonNull ImageCaptureException exception) {

                        Toast.makeText(
                                CameraActivity.this,
                                "Capture failed: "
                                        + exception.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        exception.printStackTrace();
                    }
                }
        );
    }

    private void retakePhoto() {


        capturedImage.setVisibility(View.GONE);

        cameraPreview.setVisibility(View.VISIBLE);

        captureButton.setVisibility(View.VISIBLE);

        saveButton.setVisibility(View.GONE);

        retakeButton.setVisibility(View.GONE);
    }
}