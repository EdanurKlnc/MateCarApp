package com.example.matecarapp.activity.traveller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.TravellerAccess;
import com.example.matecarapp.activity.driver.MapDriverActivity;
import com.example.matecarapp.activity.driver.SignInDriverActivity;
import com.example.matecarapp.databinding.ActivitySignInBinding;
import com.example.matecarapp.model.Traveller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    Toolbar mToolbar;
    AuthAccess mAuthAccess;
    TravellerAccess mTravellerAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignInBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);


        mAuthAccess = new AuthAccess();
        mTravellerAccess = new TravellerAccess();


        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Kayıt Ol");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.kayitButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String ad = binding.kayitAd.getText().toString();
                final String email = binding.kayitemail.getText().toString();
                final String sifre = binding.kayitSifre.getText().toString();

                if (!ad.isEmpty() && !email.isEmpty() && !sifre.isEmpty()) {
                    if (sifre.length() >= 6) {
                       signIn(ad,email,sifre);

                    } else {
                        Toast.makeText(SignInActivity.this, "Şifre 6 haneliden küçük olamaz", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Yukarıdaki alanlar boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                }
            }

            void signIn(final String ad,String email, String sifre){
                mAuthAccess.signIn(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Traveller traveller = new Traveller(id,ad,email);
                            create(traveller);

                        } else {
                            Toast.makeText(SignInActivity.this, "Kayıt olunurken hata oluştu ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            void create(Traveller traveller){
                mTravellerAccess.create(traveller).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent (SignInActivity.this, MapTravellerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);                        }
                        else {
                            Toast.makeText(SignInActivity.this, "Yolcu oluşturulamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } /*
            void saveUser(String id,String ad, String email) {
                String selectedUser = mSPref.getString("user", "");
                User user = new User();
                user.setEmail(email);
                user.setAd(ad);
                if (selectedUser.equals("driver")) {
                    mDatabase.child("Users").child("Driver").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignInActivity.this, "Kayıt olma başarılı", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SignInActivity.this, "Kayıt olunurken hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else if (selectedUser.equals("traveller")) {
                    mDatabase.child("Users").child("Traveller").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignInActivity.this, "Kayıt olma başarılı", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SignInActivity.this, "Kayıt olma başarısız", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            } */
        });
    }
}