package com.example.matecarapp.access;

import com.example.matecarapp.model.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverAccess {
    DatabaseReference mDatabase;

    public DriverAccess() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver");
    }

    public Task<Void> create(Driver driver){
        return mDatabase.child(driver.getId()).setValue(driver);

    }
}
