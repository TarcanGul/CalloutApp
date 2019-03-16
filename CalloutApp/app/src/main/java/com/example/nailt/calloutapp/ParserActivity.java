package com.example.nailt.calloutapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.RuleBasedCollator;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson.*;

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
    EditText titleField;
    private static final JsonFactory JSON_FACTORY =  JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "./res/client_id.json";
    GoogleAccountCredential credential;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);
        dateField = (TextView) findViewById(R.id.dateField);
        timeField = (TextView)  findViewById(R.id.timeField);
        locationField = (Spinner)  findViewById(R.id.locationField);
        titleField = (EditText) findViewById(R.id.titleField);
        Button backButton = (Button) findViewById(R.id.backButton);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(sendButtonListener);
        backButton.setOnClickListener(backButtonListener);


        Runnable inputRunnable = new SendInputThread();
        Thread inputSendingthread = new Thread(inputRunnable);
        inputSendingthread.start();
        try {
            inputSendingthread.join();
            sendButton.setEnabled(true);
        }
        catch(InterruptedException e)
        {
            Log.d("Input Thread interruption", e.getMessage());
        }
    }

    View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goBackIntent);
        }
    };

    View.OnClickListener sendButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v)
        {
            Log.d("Calendar:","Sending started");
            //Send request to API
        }

    };

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = ParserActivity.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("online")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(5000).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("nailtarcan@gmail.com");
        //return flow.loadCredential("nailtarcan@gmail.com");
    }

}
