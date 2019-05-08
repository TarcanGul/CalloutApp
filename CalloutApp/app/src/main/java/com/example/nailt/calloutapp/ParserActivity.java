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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ParserActivity extends AppCompatActivity {

    TextView dateField;
    TextView timeField;
    Spinner locationField;
    EditText titleField;


    GoogleAccountCredential credential;
    GoogleSignInOptions gso;

    GoogleSignInClient mGoogleSignInClient;
    private static final Scope CALENDAR_AUTH_TOKEN = new Scope("https://www.googleapis.com/auth/calendar.events");
    private static final int RC_SIGN_IN = 9001;
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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getApplicationContext().getResources().getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();
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

    View.OnClickListener sendButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Calendar:", "Sending started");
                //Send request to API
                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);



            }

        };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d("Request", "Request got in.");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("Google Auth", "Auth successful!");
            if(account != null)Log.d("Google Auth", account.getEmail());
            String authCode = account.getServerAuthCode();

            //Send client details to server
            Call<ResponseBody> calendarCall = Client.getClientInstance().getAPI().sendToGoogleCalendar(account.getIdToken(), authCode, dateField.getText().toString(), timeField.getText().toString(), locationField.getSelectedItem().toString());
            calendarCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("Calendar-Server", "Calendar Connection Successful");

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Calendar-Server", t.getMessage());
                }
            });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Google Authentication", "signInResult:failed code=" + e.getStatusCode());
            Log.d("Google Authentication", e.getMessage());
        }
    }

}
