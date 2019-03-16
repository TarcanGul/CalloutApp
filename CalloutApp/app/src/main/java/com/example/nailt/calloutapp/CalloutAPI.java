package com.example.nailt.calloutapp;

import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CalloutAPI
{
    @Multipart
    @POST("model")
    Call<Result> sendImageToServer(@Part MultipartBody.Part image);
}
