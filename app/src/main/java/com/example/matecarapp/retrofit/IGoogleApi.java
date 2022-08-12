package com.example.matecarapp.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApi {
    @GET
    default Call<String> getDirections(@Url String url){

        return null;
    }
}
