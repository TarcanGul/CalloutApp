package com.example.nailt.calloutapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ParserActivity extends AppCompatActivity {

    TextView dateField;
    TextView timeField;
    Spinner locationField;
    //Spinner titleField;
    Gson gson = new Gson();
    private class SendInputThread implements Runnable
    {
        public void run()
        {
            //TODO: Get image input from main activity, send to flask server
            Uri takenImage = getIntent().getExtras().getParcelable("imageUri");
            Log.d("Image URI:", takenImage.toString());
            if(takenImage != null)
            {
                String filePathStr = null;
                String[] path = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(takenImage, path,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(path[0]);
                filePathStr = c.getString(columnIndex);
                    File file = new File(filePathStr);
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    Call<Result> inputCall = Client.getClientInstance().getAPI().sendImageToServer(part);
                    inputCall.enqueue(new Callback<Result>() {
                        Result result;
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Log.d("Image input", "Image input successful");
                            Log.d("JSON", "Success!");
                            result = response.body();
                            dateField.setText(result.getDate());
                            timeField.setText(result.getTime());
                            Log.d("JSON", response.body().toString());
                            locationField.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    result.getLocations()));
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Log.d("Image input", t.getMessage());
                        }
                    });

            }
            else {
                Log.d("Taken image", "Taken image is null");
            }

        }
    }

    /*private class GetOutputThread implements Runnable
    {
        public void run()
        {
            Call<Result> call = Client.getClientInstance().getAPI().getResult();
            Log.d("JSON", "Request sent");
            call.enqueue(new Callback<Result>() {
                Result result;
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    Log.d("JSON", "Success!");
                    result = response.body();
                    dateField.setText(result.getDate());
                    timeField.setText(result.getTime());
                    Log.d("JSON", response.body().toString());
                    locationField.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            result.getLocations()));
                    Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_LONG);
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
                    Log.d("JSON", t.getMessage());
                }

            });
        }
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);
        dateField = (TextView) findViewById(R.id.dateField);
        timeField = (TextView)  findViewById(R.id.timeField);
        locationField = (Spinner)  findViewById(R.id.locationField);
        //titleField = (Spinner) findViewById(R.id.titleField);
        Runnable inputRunnable = new SendInputThread();
        Thread inputSendingthread = new Thread(inputRunnable);
        inputSendingthread.start();

        try {
            inputSendingthread.join();
        }
        catch(InterruptedException e)
        {
            Log.d("Input Thread interruption", e.getMessage());
        }
        /*Runnable outputRunnable = new GetOutputThread();
        Thread outputGettingThread = new Thread(outputRunnable);
        outputGettingThread.start();
        try {
            outputGettingThread.join();
        }
        catch(InterruptedException e)
        {
            Log.d("Input Thread interruption", e.getMessage());
        }*/
    }

}
