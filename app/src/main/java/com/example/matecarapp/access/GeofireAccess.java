package com.example.matecarapp.access;

import android.webkit.GeolocationPermissions;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.maps.model.LatLng;

public class GeofireAccess {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeofireAccess(String reference) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(reference);
        mGeofire = new GeoFire(mDatabase);
    }

    public void saveLocation(String idDriver, LatLng lating) {

        mGeofire.setLocation(idDriver, new GeoLocation(lating.latitude, lating.longitude));

    }

    public void removeLocation(String idDriver) {
        mGeofire.removeLocation(idDriver);

    }
    public GeoQuery getActiveDriver(LatLng latLng, double radius){
        GeoQuery geoQuery= mGeofire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude),radius);
        geoQuery.removeAllListeners();
         return geoQuery;
    }
    public DatabaseReference isDriverWorking(String idDriver){
        return FirebaseDatabase.getInstance().getReference().child("driver_working").child(idDriver);

    }
}

