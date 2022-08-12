package com.example.matecarapp.access;

import com.example.matecarapp.model.Traveller;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TravellerAccess {

    DatabaseReference mDatabase;

    public TravellerAccess() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Traveller");
    }

    public Task<Void> create(Traveller traveller) {

        Map<String, Object> map = new HashMap<>();
        map.put("ad", traveller.getAd());
        map.put("email", traveller.getEmail());

        return mDatabase.child(traveller.getId()).setValue(traveller);

    }
    public DatabaseReference getTraveller(String idTraveller){
        return mDatabase.child(idTraveller);

    }
}
