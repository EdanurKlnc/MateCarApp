package com.example.matecarapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.matecarapp.R;
import com.example.matecarapp.activity.driver.MapDriverActivity;
import com.example.matecarapp.activity.traveller.MapTravellerActivity;
import com.example.matecarapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main); //binging etkileştirme

        mSPref = getSharedPreferences("typUser",MODE_PRIVATE);
        SharedPreferences.Editor editor = mSPref.edit();


        binding.yolcuButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","traveller");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, FirstScreenActivity.class);
                startActivity(intent);

            }
        });

        binding.sRCButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","driver");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, FirstScreenActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user = mSPref.getString("user","");
            if (user.equals("traveller")) {
                Intent intent = new Intent(MainActivity.this, MapTravellerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }


        }
    }
}