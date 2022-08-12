package com.example.matecarapp.access;

import com.example.matecarapp.model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenAccess {
    DatabaseReference mDatabase;


    public TokenAccess() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Token");


    }

    public void create(final String idUser) {
        if(idUser == null) return;
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token(s);
                mDatabase.child(idUser).setValue(token);
            }
        });
    }
    public  DatabaseReference getToken(String idUser)
    {
        return  mDatabase.child(idUser);
    }
}
