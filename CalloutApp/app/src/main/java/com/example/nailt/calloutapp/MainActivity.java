package com.example.nailt.calloutapp;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.OutputConfiguration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    public static final int THUMBNAIL_SIZE = 200;
    Bitmap current_image;
    Uri imageUri;
    //PythonInterpreter interpreter = new PythonInterpreter();
    ImageView i_view;
    Button takePhotoAgainButton;
    Button parseAndSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Setting the screen as the main activity XML
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //First we will be needing permission from the user for the camera and writing to external storage (to store photos)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,

            }, 200);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,

            }, 200);
            return;
        }

        i_view = (ImageView) findViewById(R.id.imageView);
        takePhotoAgainButton = (Button) findViewById(R.id.photoAgainButton);
        parseAndSendButton = (Button) findViewById(R.id.parseAndSendButton);

        takePhotoAgainButton.setOnClickListener(takePhotoAgainListener);
        parseAndSendButton.setOnClickListener(parseAndSendListener);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CAMERA_REQUEST) && (resultCode == Activity.RESULT_OK)) {
            // Check if the result includes a thumbnail Bitmap
                // TODO It is only a thumbnail, turn into higher quality.
                // in outputFileUri. Perhaps copying it to the app folder
                try {
                    current_image = (Bitmap) data.getExtras().get("data");
                    i_view.setImageBitmap(current_image);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

        }
    }

    View.OnClickListener takePhotoAgainListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    };

    View.OnClickListener parseAndSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


}
