package com.example.matecarapp.access;

import android.content.Context;

import com.example.matecarapp.R;
import com.example.matecarapp.retrofit.IGoogleApi;
import com.example.matecarapp.retrofit.RetrofitTraveller;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;


public class GoogleApiAccess {
    private Context context;

    public GoogleApiAccess(Context context){
        this.context= context;

    }
    public Call<String> getDirections(LatLng sourceLating, LatLng destinationLating){
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + sourceLating.latitude + "," + sourceLating.longitude + "&"
                + "destination=" + destinationLating.latitude + "," + destinationLating.longitude + "&"
                + "key=" + context.getResources().getString(R.string.google_api_key);

        return RetrofitTraveller.getTraveller(baseUrl).create(IGoogleApi.class).getDirections(baseUrl+query);

    }

}
