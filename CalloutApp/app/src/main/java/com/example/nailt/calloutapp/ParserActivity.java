package com.example.nailt.calloutapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

public class ParserActivity extends AppCompatActivity {

    TextView dateField;
    TextView timeField;
    Spinner locationField;
    Spinner titleField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateField = (TextView) findViewById(R.id.dateField);
        timeField = (TextView)  findViewById(R.id.timeField);
        locationField = (Spinner)  findViewById(R.id.locationField);
        titleField = (Spinner) findViewById(R.id.titleField);

        //TODO: Get image input from main activity
        //TODO: Communicate with Flask Server to get the relevant information
        //TODO: Parse information and send to the fields
        //TODO: Send the possible strings to Spinners

        MainActivity.transitionToast.cancel();   // Dismiss the toast
        setContentView(R.layout.activity_parser);
    }
}
