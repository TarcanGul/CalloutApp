package com.example.nailt.calloutapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);
        dateField = (TextView) findViewById(R.id.dateField);
        timeField = (TextView)  findViewById(R.id.timeField);
        locationField = (Spinner)  findViewById(R.id.locationField);
        //titleField = (Spinner) findViewById(R.id.titleField);
        //TODO: Get image input from main activity, send to flask server

        Retrofit retrofit = new Retrofit.Builder().baseUrl(CalloutAPI.BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        CalloutAPI api = retrofit.create(CalloutAPI.class);
        Call<Result> call = api.getResult();
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
}
