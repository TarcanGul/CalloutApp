package com.example.nailt.calloutapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

    EditText dateField;
    EditText timeField;
    EditText timeEndField;
    Spinner locationField;
    EditText titleField;
    LinearLayout spinner;
    LinearLayout parserScreen;
    Button sendButton;
    GoogleAccountCredential credential;
    GoogleSignInOptions gso;
    public static Toast transitionToast;
    GoogleSignInClient mGoogleSignInClient;
    private static final Scope CALENDAR_AUTH_TOKEN = new Scope("https://www.googleapis.com/auth/calendar.events");
    private static final int RC_SIGN_IN = 9001;
    AlertDialog.Builder builder;

    private class SendInputTask extends AsyncTask<Void, String, Integer>
    {
        @Override
        protected void onPreExecute() {

            parserScreen.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.VISIBLE);
            transitionToast = Toast.makeText(getApplicationContext(), "Parsing...", Toast.LENGTH_LONG);
            transitionToast.show();
        }

        protected Integer doInBackground(Void... params)
        {
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
                        result = response.body();
                        if(result.isEmpty())
                        {
                            spinner.setVisibility(View.GONE);
                            transitionToast.cancel();
                            Log.d("Result", "Is empty!");
                            builder.setMessage("Sorry, but it seems like the picture you have taken cannot be parsed.");
                            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(goBackIntent);
                                }
                            });
// Create the AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else{
                            Log.d("Result", "Image input successful");
                            Log.d("JSON", "Success!");
                            if(transitionToast != null) transitionToast.cancel();
                            transitionToast = Toast.makeText(getApplicationContext(), "Parsing successful!", Toast.LENGTH_LONG);
                            transitionToast.show();
                            spinner.setVisibility(View.GONE);
                            parserScreen.setVisibility(View.VISIBLE);
                            sendButton.setEnabled(true);
                            dateField.setText(result.getDate());
                            timeField.setText(result.getTime());
                            timeEndField.setText(result.getEndTime());
                            Log.d("JSON", response.body().toString());
                            locationField.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.support_simple_spinner_dropdown_item,
                                    result.getLocations()));
                        }

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        spinner.setVisibility(View.GONE);
                        Log.d("Image input", t.getMessage());
// Add the buttons
                        builder.setMessage("Error: " + t.getMessage());
                        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(goBackIntent);
                            }
                        });
// Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });

            }
            else {
                Log.d("Taken image", "Taken image is null");
            }
            return 0;
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

        dateField = (EditText) findViewById(R.id.dateField);
        timeField = (EditText)  findViewById(R.id.timeField);
        timeEndField = (EditText) findViewById(R.id.timeEndField);
        locationField = (Spinner)  findViewById(R.id.locationField);
        titleField = (EditText) findViewById(R.id.titleField);

        spinner = (LinearLayout) findViewById(R.id.progressBar1);
        parserScreen = (LinearLayout) findViewById(R.id.parserScreen);
        Button backButton = (Button) findViewById(R.id.backButton);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(sendButtonListener);
        backButton.setOnClickListener(backButtonListener);
        builder = new AlertDialog.Builder(this);



        //Runnable inputRunnable = new SendInputThread();
        //Thread inputSendingthread = new Thread(inputRunnable);

        new SendInputTask().execute();
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
            String title = titleField.getText().toString();
            if(title == null)
                title = "Untitled event";
            //Send client details to server
            Call<ResponseBody> calendarCall = Client.getClientInstance().getAPI().sendToGoogleCalendar(account.getIdToken(), authCode, dateField.getText().toString()
                    , timeField.getText().toString(), timeEndField.getText().toString(), locationField.getSelectedItem().toString(), title);
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
