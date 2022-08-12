package com.example.matecarapp.retrofit;

import com.example.matecarapp.model.FCMBody;
import com.example.matecarapp.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAAVjOokE:APA91bF_Yw0WlyR6hHQHe0LesnCBS4ESvwJBTMtYVoc5e6XIr3Mdx-iu4b4SfTlCBnsTcs_tuA0eLhXzkuJwNQZGpzWvdWOiFuKTUvccmiF40x1_z3ZUkvnvX-l7UoGXfwPbITyQJEFR "
    })

    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
