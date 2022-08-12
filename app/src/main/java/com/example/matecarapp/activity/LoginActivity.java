package com.example.matecarapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matecarapp.R;
import com.example.matecarapp.activity.driver.MapDriverActivity;
import com.example.matecarapp.activity.traveller.MapTravellerActivity;
import com.example.matecarapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuht;
    DatabaseReference mDatabase;
    EditText girisemail, girissifre;
    SharedPreferences mSPref;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mSPref = getSharedPreferences("typUser", MODE_PRIVATE);

        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Giriş");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuht = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        girisemail = findViewById(R.id.girisemail);
        girissifre = findViewById(R.id.girisŞifre);


        binding.girisYapButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        final String email = girisemail.getText().toString();
        final String sifre = girissifre.getText().toString();

        if (!email.isEmpty() && !sifre.isEmpty()) {
            if (sifre.length() >= 6) {

                mAuht.signInWithEmailAndPassword(email, sifre).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "E-mail veya Şifre Hatalı", Toast.LENGTH_SHORT).show();

                        } else {
                            String user = mSPref.getString("user", "");
                            if (user.equals("traveller")) {
                                Intent intent = new Intent(LoginActivity.this, MapTravellerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Şifre 6 haneden kısa olamaz", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "E-mail veya Şifre boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
    }
}