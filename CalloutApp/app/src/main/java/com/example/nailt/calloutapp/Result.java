package com.example.nailt.calloutapp;

import java.util.ArrayList;

public class Result {

    private String date;
    private String time;
    private String end_time;
    private ArrayList<String> locations;

    public Result(String date, String time, String end_time, ArrayList<String> locations)
    {
        this.date = date;
        this.time = time;
        this.end_time = end_time;
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

    public String getEndTime(){ return end_time; }

    public boolean isEmpty()
    {
        return date == null && time == null && end_time == null && locations.isEmpty();
    }

}
