package com.example.nailt.calloutapp;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.app.AlertDialog;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * To do list
 * TODO: Set up the "Take photo" button
 * The button should create the signal for taking a JPEG output. We can use that to put into the Python Script
 *
 * TODO: Create a new activity where Python and Java connects
 * We should somehow extract the JPEG output and present to this activity as a input
 * Use ProcessBuilder or Jython to use the Python script.
 *
 * TODO: What happens if user declines the permissions
 * The app should close itself
 *
 * TODO: The layouts are not stable (Constraint Layout)
 * Might look different in different devices
 *
 *
 */

/**
 * The goal of this activity is to setup the camera and have a button that can record a picture,
 * which will be used with the Python algorithm that extracts information from a callout picture.
 */

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener{

    //CameraManager allows us to reach any camera within the system, these systems can also be externally connected.
    CameraManager manager;

    //Handler is used for communication between the camera thread and the main thread (arg of openCamera())
    Handler handler = new Handler();

    //TextureView is the object that holds the camera stream in the main screen
    TextureView textureView;
    Button takePhotoButton;
    PythonInterpreter interpreter = new PythonInterpreter();

    //Every camera in the device(front, rear or external) has a unique string ID. Thus we will be needing this ID for locating the rear camera.
    static String pickedCameraID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Setting the screen as the main activity XML
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //First we will be needing permission from the user for the camera and writing to external storage (to store photos)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 200);
            return;
        }

        //Initializing the textureView object
        textureView = (TextureView) findViewById(R.id.textureView);
        //This class also implements surface texture listener, and that is the interface we use to open the camera.
        textureView.setSurfaceTextureListener(this);

        //Setting up the CameraManager object
        manager = (CameraManager) MainActivity.this.getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] availableCameras = manager.getCameraIdList(); //Getting all the cameras in the device
            for (int i = 0; i < availableCameras.length; i++) {
                //Getting camera info
                CameraCharacteristics cameraInfo = manager.getCameraCharacteristics(availableCameras[i]);
                //Checking if the camera is the back camera
                if (cameraInfo.get(CameraCharacteristics.LENS_FACING).equals(CameraMetadata.LENS_FACING_BACK)) {
                    pickedCameraID = availableCameras[i]; //Pick the camera
                    break;
                }
            }

            //Now the pickedCameraID will be used in textureView, with the camera thread.
        }
        catch(CameraAccessException | SecurityException e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(e.getMessage());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(this);
    }

    //Capture Callback interface is for receiving real time updates of our capture requests.
    CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    };

    //

    /**
     * This is the most important method in opening the camera. This method runs on a separate thread
     * and doesn't start until pickedCameraID is received.
     * @param texture will be used in initializing a surface class,
     *                which will let us to create a display of what the rear camera sees
     * @param width //Width of a image. Isn't in use right now, might be in future.
     * @param height //Height of a image. Isn't in use right now, might be in future.
     *
     * It is useful to know that with width and height we can create Size objects and we can specify
     * the resolution of the image we want (620 * 480 can be a good default choice).
     */
    public void onSurfaceTextureAvailable(final SurfaceTexture texture, int width, int height) {

        try {
            while(pickedCameraID == null)
            {
                //Polling until we receive the cameraID from the onCreate method
                //POINT OF DISCUSSION(by Tarcan): Is setting ID to "0" at the start always give the rear camera?
                //If it does, we can start the app even faster
            }
            manager.openCamera(pickedCameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull final CameraDevice camera){
                    //Creating the surface object with the SurfaceTexture object. We set it to final because we will use it in the inner class
                    final Surface surface = new Surface(texture);
                    //OutputConfiguration is useful for describing camera output.
                    OutputConfiguration config = new OutputConfiguration(surface);
                    //List is needed for createCaptureSessionByOutputConfigurations method
                    ArrayList<OutputConfiguration> listOfConfigurations = new ArrayList<>();
                    listOfConfigurations.add(config);
                    try
                    {
                        //This is the method where we initialize the stream.
                        camera.createCaptureSessionByOutputConfigurations(listOfConfigurations, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session)
                            {
                                //Now we successfully  have opened the camera. We can use it.
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


    /**
     * This is the method we use after opening the camera successfully, so creating a stream of capture requests.
     * @param camera The camera device
     * @param session //The capture session of the camera (while camera is open)
     * @param surface //The pixel information of where the output images will be on the screen
     */
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


    //Other interface methods which allows us to control what we will do with the camera.
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    //For the take photo button.
    @Override
    public void onClick(View v) {
            //Use Python Interpreter


    }
}
