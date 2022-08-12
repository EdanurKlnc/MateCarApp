package com.example.matecarapp.access;

import com.example.matecarapp.model.TravellerBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TravellerBookingAccess {
    private DatabaseReference mDatabase;

    public TravellerBookingAccess(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TravellerBooking");

    }

 public Task<Void> create(TravellerBooking travellerBooking){
        return  mDatabase.child(travellerBooking.getIdTraveller()).setValue(travellerBooking);

 }
 public Task<Void> updateStatus(String idTravellerBooking, String status){

     Map<String, Object> map = new HashMap<>();
     map.put("status",status);
        return mDatabase.child(idTravellerBooking).updateChildren(map);
 }

public DatabaseReference getStatus(String  idTravellerBooking){
        return mDatabase.child(idTravellerBooking).child("status");


}
}
