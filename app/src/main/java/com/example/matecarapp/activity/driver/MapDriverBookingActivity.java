package com.example.matecarapp.activity.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.GeofireAccess;
import com.example.matecarapp.access.TokenAccess;
import com.example.matecarapp.access.TravellerAccess;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.TokenProvider;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthAccess mAuthAccess;
    private TravellerAccess mTravellerAccess;
    private GeofireAccess mGeofireAccess;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTING_REQUEST_CODE = 2;

    private Marker mMarker;
    private TokenAccess mTokenAccess;

    private LatLng mLatLng;
    private TextView mTextVievTravellerBooking;
    private TextView mTextEmailTravellerBooking;


    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private String mExtraTravellerId;


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mMarker != null) {
                        mMarker.remove();
                    }

                    mMarker = mMap.addMarker(new MarkerOptions().position(
                                            new LatLng(location.getLatitude(), location.getLongitude())
                                    )
                                    .title("Konum")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_car)) //Surucunun konumunu gösteren arac ikonu


                    );
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    updateLocation();
                }
            }
        }
    };

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);
        mAuthAccess = new AuthAccess();
        mGeofireAccess = new GeofireAccess("driver_working");
        mTokenAccess = new TokenAccess();
        mTravellerAccess = new TravellerAccess();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mTextVievTravellerBooking = findViewById(R.id.travellerBooking);
        mTextEmailTravellerBooking = findViewById(R.id.travellerEmailBooking);

        mExtraTravellerId = getIntent().getStringExtra("idTraveller");

        getTravellerBooking();
    }
private void getTravellerBooking(){
        mTravellerAccess.getTraveller(mExtraTravellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String email= snapshot.child("email").getValue().toString();
                    String ad= snapshot.child("ad").getValue().toString();
                    mTextVievTravellerBooking.setText(ad);
                    mTextEmailTravellerBooking.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}
    private void updateLocation() {

        if (mAuthAccess.existSession() && mLatLng != null) {

            mGeofireAccess.saveLocation(mAuthAccess.getId(), mLatLng);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true); // Haritadaki yakınlaştırma

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gps()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    } else {
                        AlertDialogGps();

                    }
                } else {
                    checkLocationPermissions();

                }
            } else {
                checkLocationPermissions();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE && gps()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        } else {
            AlertDialogGps();
        }
    }

    private void AlertDialogGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Devam edebilmek için konumu etkinleştiriniz")
                .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTING_REQUEST_CODE);
                    }
                })
                .create().show();

    }

    private boolean gps() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;

        }
        return isActive;
    }

    private void disconnect() {

        if (mFusedLocation != null) {

            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if (mAuthAccess.existSession()) {
                mGeofireAccess.removeLocation(mAuthAccess.getId());
            }
        } else {
            Toast.makeText(this, "Konum paylaşmayı durdur", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                if (gps()) {


                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                } else {
                    AlertDialogGps();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gps()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                AlertDialogGps();
            }
        }
    }

    private void checkLocationPermissions() { //konum izinleri kontrolü
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("İzin Ver")
                        .setMessage("Uygulama için konum izinleri gerekmektedir")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }

        }
    }

}