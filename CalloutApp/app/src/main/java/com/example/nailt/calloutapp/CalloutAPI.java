package com.example.nailt.calloutapp;

import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CalloutAPI
{

    @GET("output")
    Call<Result> getResult();

    @Multipart
    @POST("input")
    Call<ResponseBody> sendImageToServer(@Part MultipartBody.Part image);
}
