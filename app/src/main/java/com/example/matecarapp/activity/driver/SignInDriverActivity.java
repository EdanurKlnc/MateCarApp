package com.example.matecarapp.activity.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.DriverAccess;
import com.example.matecarapp.access.TravellerAccess;
import com.example.matecarapp.activity.traveller.SignInActivity;
import com.example.matecarapp.databinding.ActivitySignInBinding;
import com.example.matecarapp.databinding.ActivitySignInDriverBinding;
import com.example.matecarapp.model.Driver;
import com.example.matecarapp.model.Traveller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInDriverActivity extends AppCompatActivity {

    Toolbar mToolbar;
    AuthAccess mAuthAccess;
   DriverAccess mDriverAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignInDriverBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in_driver);


        mAuthAccess = new AuthAccess();
        mDriverAccess= new DriverAccess();


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
                final String arabaModeli = binding.kayitArabaModeli.getText().toString();
                final String arabaPlakasi = binding.kayitArabaPlakasi.getText().toString();

                if (!ad.isEmpty() && !email.isEmpty() && !sifre.isEmpty() && !arabaModeli.isEmpty() && !arabaPlakasi.isEmpty()) {
                    if (sifre.length() >= 6) {
                        signIn(ad,email,sifre,arabaModeli,arabaPlakasi);

                    } else {
                        Toast.makeText(SignInDriverActivity.this, "Şifre 6 haneliden küçük olamaz", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInDriverActivity.this, "Yukarıdaki alanlar boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                }
            }

            void signIn(final String ad,String email, String sifre,String arabaModeli,String arabaPlakasi){
                mAuthAccess.signIn(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Driver driver = new Driver(id,ad,email,arabaModeli,arabaPlakasi);
                            create(driver);

                        } else {
                            Toast.makeText(SignInDriverActivity.this, "Kayıt olunurken hata oluştu ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            void create(Driver driver){
                mDriverAccess.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent (SignInDriverActivity.this,MapDriverActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                         //   Toast.makeText(SignInDriverActivity.this, "Başarılı bir şekilde oluşturuldu", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignInDriverActivity.this, "Yolcu oluşturulamadı", Toast.LENGTH_SHORT).show();
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