package com.example.matecarapp.activity.traveller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.GeofireAccess;
import com.example.matecarapp.access.GoogleApiAccess;
import com.example.matecarapp.access.NotificationAccess;
import com.example.matecarapp.access.TokenAccess;
import com.example.matecarapp.access.TravellerBookingAccess;
import com.example.matecarapp.model.FCMBody;
import com.example.matecarapp.model.FCMResponse;
import com.example.matecarapp.model.TravellerBooking;
import com.example.matecarapp.utils.DecodePoints;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextViewLookingFor;
    private Button mButtonCagriiptal;
    private GeofireAccess mGeofireAccess;
    private double mExtraSourceLat;
    private double mExtraSourceLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;

    private String mExtraSource;
    private String mExtraDestination;
    private GoogleApiAccess mGoogleApiAccess;

    private ValueEventListener mListener;

    private LatLng mSourceLating;
    private LatLng mDestinationLating;
    private double mRadius = 0.1;
    private boolean mDriverfound = false;
    private String mIdDriverfound = "";
    private NotificationAccess mNotificationAccess;
    private TokenAccess mTokenAccess;
    private TravellerBookingAccess mTravellerBookingAccess;
    private AuthAccess mAuhtAccess;

    private LatLng mDriverfoundLating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        mAnimation = findViewById(R.id.carCity);
        mTextViewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCagriiptal = findViewById(R.id.butonCagriiptal);

        mAnimation.playAnimation();
        mExtraSource = getIntent().getStringExtra("origin");

        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraSourceLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraSourceLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);


        mSourceLating = new LatLng(mExtraSourceLat, mExtraSourceLng);
        mDestinationLating = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGeofireAccess = new GeofireAccess("active_driver");
        mNotificationAccess = new NotificationAccess();
        mTokenAccess = new TokenAccess();
        mTravellerBookingAccess = new TravellerBookingAccess();
        mAuhtAccess = new AuthAccess();
        mGoogleApiAccess = new GoogleApiAccess(RequestDriverActivity.this);

        getClosestDriver();

        mButtonCagriiptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(RequestDriverActivity.this,MapTravellerActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getClosestDriver() {
        mGeofireAccess.getActiveDriver(mSourceLating, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if (!mDriverfound) {
                    mDriverfound = true;
                    mIdDriverfound = key;
                    mDriverfoundLating = new LatLng(location.latitude, location.longitude);
                    mTextViewLookingFor.setText("Yakınlarda Sürücü Bulundu \n Cevap Bekleniyor");
                    createTravellerBooking();
                    Log.d("Sürücü", "ID" + mIdDriverfound);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!mDriverfound) {
                    mRadius = mRadius + 0.1f;
                    if (mRadius > 5) {
                        mTextViewLookingFor.setText("Yakınlarda sürücü bulunamadı");
                        Toast.makeText(RequestDriverActivity.this, "Yakınlarda sürücü bulunamadı.", Toast.LENGTH_SHORT).show();
                        return;


                    } else {
                        getClosestDriver();
                    }

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void createTravellerBooking() {
        mGoogleApiAccess.getDirections(mSourceLating, mDriverfoundLating).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = new JSONObject().getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");

                    //json verilerini internettekilerle eşleştiriyoruz

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    sendNotification(durationText, distanceText);


                } catch (Exception e) {
                    Log.d("Error", "Hata!!!" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }


    private void sendNotification(final String time, final String km) {
        mTokenAccess.getToken(mIdDriverfound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String token = snapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "Otostop Talebi" + time);
                    map.put("body",
                            "Otostap Çekmek İsteyen Biri var !!!" + km);
                    map.put("idTraveller", mAuhtAccess.getId());
                    FCMBody fcmBody = new FCMBody(token, "high", map);
                    mNotificationAccess.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                            if (response.body() != null) {
                                if (response.body().getSuccess() == 1) {
                                    TravellerBooking travellerBooking = new TravellerBooking(
                                            mAuhtAccess.getId(),
                                            mIdDriverfound,
                                            mExtraDestination,
                                            mExtraSource,
                                            time,
                                            km,
                                            "create",
                                            mExtraSourceLat,
                                            mExtraSourceLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng
                                    );
                                    mTravellerBookingAccess.create(travellerBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            checkStatusTravellerBooking();
                                            //Toast.makeText(RequestDriverActivity.this, "İstek başarıyla oluşturuldu", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Toast.makeText(RequestDriverActivity.this, "Otostop çekme talebi başarıyla iletildi.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RequestDriverActivity.this, "Otostop çekme talebinde bulunulamadı.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RequestDriverActivity.this, "Otostop çekme talebinde bulunulamadı.", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {

                            Log.d("Error", "Error" + t.getMessage());

                        }
                    });

                } else {
                    Toast.makeText(RequestDriverActivity.this, "Otostop çekme talebinde bulunamadı. ", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkStatusTravellerBooking() {
        mListener = mTravellerBookingAccess.getStatus(mAuhtAccess.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue().toString();
                    if (status.equals("accept")) {
                        Intent intent = new Intent(RequestDriverActivity.this, MapTravellerBookingActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (status.equals("cancel")) {
                        Toast.makeText(RequestDriverActivity.this, "Otostop Çekme Talebi Sürücü Tarafından Kabul Edilmedi ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RequestDriverActivity.this, MapTravellerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mTravellerBookingAccess.getStatus(mAuhtAccess.getId()).removeEventListener(mListener);

        }
    }
}