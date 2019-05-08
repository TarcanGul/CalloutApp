package com.example.nailt.calloutapp;

import android.net.Uri;

import okhttp3.MultipartBody;
import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CalloutAPI
{
    @Multipart
    @POST("model")
    Call<Result> sendImageToServer(@Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("calendar")
    Call<ResponseBody> sendToGoogleCalendar(@Field("token") String idToken, @Field("authcode") String auth_code, @Field("date") String date, @Field("time") String time,
                                           @Field("location") String location);
}
