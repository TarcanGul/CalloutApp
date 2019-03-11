package com.example.nailt.calloutapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CalloutAPI
{
    String BASE_URL = "http://10.192.49.68:5000/";

    @GET("output")
    Call<Result> getResult();
}
