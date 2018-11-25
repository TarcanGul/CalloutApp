package com.example.nailt.calloutapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.app.AlertDialog;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    CameraManager manager;
    Handler handler = new Handler();
    CameraDevice camera;
    TextureView textureView;
    static String pickedCameraID = "0";


    public void onSurfaceTextureAvailable(final SurfaceTexture texture, int width, int height) {

        try {
            manager.openCamera(pickedCameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull final CameraDevice camera){
                    final Surface surface = new Surface(texture);
                    OutputConfiguration config = new OutputConfiguration(surface);
                    ArrayList<OutputConfiguration> listOfConfigurations = new ArrayList<>();
                    listOfConfigurations.add(config);
                    try
                    {
                        camera.createCaptureSessionByOutputConfigurations(listOfConfigurations, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session)
                            {
                                useCamera(camera, session, surface);
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                            }
                        }, handler);
                    }
                    catch(CameraAccessException e)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("Camera configiration failed.");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Something happened to the camera");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //TODO: add a retry button to the dialog
                }

                @Override
                public void onClosed(@NonNull CameraDevice camera) {
                    camera.close();
                }

            }, null);
        }
        catch(CameraAccessException | SecurityException cae)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(cae.getMessage());
            AlertDialog dialog = builder.create();
            dialog.show();
            //TODO: add a retry button to the dialog
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
       //textureView.setSurfaceTexture(surface);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        //setContentView(textureView);
        manager = (CameraManager) MainActivity.this.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] availableCameras = manager.getCameraIdList();
            String pickedCameraID = "";
            for (int i = 0; i < availableCameras.length; i++) {

                CameraCharacteristics cameraInfo = manager.getCameraCharacteristics(availableCameras[i]);
                //Checking if the camera is the back camera
                if (cameraInfo.get(CameraCharacteristics.LENS_FACING).equals(CameraMetadata.LENS_FACING_BACK)) {
                    pickedCameraID = availableCameras[i];
                    break;
                }
            }
            if (pickedCameraID.equals("")) {
                throw new CameraAccessException(CameraAccessException.CAMERA_ERROR);
            }

            CameraCharacteristics pickedCameraInfo = manager.getCameraCharacteristics(pickedCameraID);
            StreamConfigurationMap map = pickedCameraInfo.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            final Size imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 200);
                return;
            }



        }
        catch(CameraAccessException | SecurityException e)
        {
            //Creating an error message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(e.getMessage());
            AlertDialog dialog = builder.create();
            dialog.show();
            //TODO: add a retry button to the dialog
        }

    }

    CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    };

    public void useCamera(CameraDevice camera, CameraCaptureSession session, Surface surface){
        try
        {
            CaptureRequest.Builder requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            requestBuilder.addTarget(surface);
            session.setRepeatingRequest(requestBuilder.build(), captureListener, handler);
        }
        catch(CameraAccessException e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error while in capture session");
            builder.setMessage(e.getMessage());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
