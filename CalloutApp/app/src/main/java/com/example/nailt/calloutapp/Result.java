package com.example.nailt.calloutapp;

import java.util.ArrayList;

public class Result {

    private String date;
    private String time;
    private ArrayList<String> locations;

    public Result(String date, String time, ArrayList<String> locations)
    {
        this.date = date;
        this.time = time;
        this.locations = locations;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }
}
