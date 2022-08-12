package com.example.matecarapp.access;

import com.example.matecarapp.model.FCMBody;
import com.example.matecarapp.model.FCMResponse;
import com.example.matecarapp.retrofit.IFCMApi;
import com.example.matecarapp.retrofit.RetrofitTraveller;

import retrofit2.Call;

public class NotificationAccess {
    private  String url ="https://fcm.google.api.com";


    public NotificationAccess() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body)
    {
        return RetrofitTraveller.getTravellerObject(url).create(IFCMApi.class).send(body);

    }
}
