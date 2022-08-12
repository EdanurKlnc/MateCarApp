package com.example.matecarapp.access;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthAccess {

    FirebaseAuth mAuth;

    public AuthAccess(){
        mAuth =FirebaseAuth.getInstance();

    }
    public Task<AuthResult> signIn(String email, String sifre){

        return mAuth.createUserWithEmailAndPassword(email,sifre);

    }
    public Task<AuthResult> login(String email, String sifre){

        return mAuth.signInWithEmailAndPassword(email,sifre);

    }
    public void exit(){
        mAuth.signOut();
    }

    public String getId(){
        return  mAuth.getCurrentUser().getUid();
    }
    public boolean existSession(){
        boolean exist = false;
        if(mAuth.getCurrentUser() != null){
            exist = true;
        }
        return exist;
    }

}
