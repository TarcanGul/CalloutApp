package com.example.nailt.calloutapp;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*Implemented client as a singleton class,
this is for not having two different clients from one device.*/

public class Client {

    private static final String BASE_URL = "http://192.168.1.127:5000/";
    private static Client _client;
    private Retrofit retrofit;

    private Client()
    {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized Client getClientInstance()
    {
        if(_client == null)
            _client = new Client();

        return _client;
    }

    public CalloutAPI getAPI()
    {
        return retrofit.create(CalloutAPI.class);
    }
}
