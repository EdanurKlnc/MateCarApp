package com.example.matecarapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.matecarapp.R;
import com.example.matecarapp.activity.driver.SignInDriverActivity;
import com.example.matecarapp.activity.traveller.SignInActivity;
import com.example.matecarapp.databinding.ActivityFirstScreenBinding;

public class FirstScreenActivity extends AppCompatActivity {
    Toolbar mToolbar;
    SharedPreferences mSPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFirstScreenBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_first_screen);

        mToolbar= findViewById(R.id.toolBar);
        mSPref = getSharedPreferences("typUser", MODE_PRIVATE);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Seçim sayfasına dönünüz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.girisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstScreenActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typUser = mSPref.getString("user","");
                Intent intent;
                if (typUser.equals("driver"))
                {
                    intent = new Intent(FirstScreenActivity.this, SignInDriverActivity.class);
                }else{
                    intent = new Intent(FirstScreenActivity.this, SignInActivity.class);

                }
                startActivity(intent);

            }
        });
    }
}