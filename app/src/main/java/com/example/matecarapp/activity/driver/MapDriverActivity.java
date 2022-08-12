package com.example.matecarapp.activity.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.GeofireAccess;
import com.example.matecarapp.access.TokenAccess;
import com.example.matecarapp.activity.MainActivity;
import com.example.matecarapp.databinding.ActivityMapDriverBinding;
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

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthAccess mAuthAccess;
    Toolbar mToolbar;
    private GeofireAccess mGeofireAccess;
    Button butonBaglan;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTING_REQUEST_CODE = 2;

    private Marker mMarker;
    private boolean mConnect = false;
    private TokenAccess mTokenAccess;
    private ValueEventListener mListener;
    private LatLng mLatLng;


    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

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
        ActivityMapDriverBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_map_driver);

        mAuthAccess = new AuthAccess();
        mGeofireAccess = new GeofireAccess("active_driver");
        mTokenAccess = new TokenAccess();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        butonBaglan = findViewById(R.id.butonBaglan);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Geri dönün");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.butonBaglan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mConnect) {

                    disconnect();


                } else {
                    startLocation();
                }
            }
        });
        generateToken();
        isDriverWorking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mGeofireAccess.isDriverWorking(mAuthAccess.getId()).removeEventListener(mListener);

        }
    }

    public void isDriverWorking() {
       mListener = mGeofireAccess.isDriverWorking(mAuthAccess.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    disconnect();
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
            butonBaglan.setText("Mevcut konumu paylaş");
            mConnect = false;
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
                    butonBaglan.setText("Oturumu kapat");
                    mConnect = true;

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
                                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }

    void exit() {
        disconnect();
        mAuthAccess.exit();
        Intent intent = new Intent(MapDriverActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void generateToken() {
        mTokenAccess.create(mAuthAccess.getId());
    }

}