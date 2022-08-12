package com.example.matecarapp.activity.traveller;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.matecarapp.R;
import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.GeofireAccess;
import com.example.matecarapp.access.TokenAccess;
import com.example.matecarapp.activity.MainActivity;
import com.example.matecarapp.databinding.ActivityMapTravellerBinding;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapTravellerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthAccess mAuthAccess;

    private GeofireAccess mGeofireAccess;
    private Marker mMarker;

    private PlacesClient mPlaces;

    private AutocompleteSupportFragment mAutocomplete;
    private AutocompleteSupportFragment mAutocompleteDestination;

    private List<Marker> mDriverMarker = new ArrayList<>();
    private boolean mFirstTime = true;

    private LatLng mCurrentLating;

    private String mSource;
    private LatLng mSourceLating;

    private String mDestination;
    private LatLng mDestinationLating;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private TokenAccess mTokenAccess;

    Toolbar mToolbar;


    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLating = new LatLng(location.getLatitude(), location.getLongitude());

                    /* if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                                            new LatLng(location.getLatitude(), location.getLongitude())
                                    )
                                    .title("Konum")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location)) //Surucunun konumunu gösteren arac ikonu


                    ); */

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                    if (mFirstTime) {
                        mFirstTime = false;
                        getActiveDriver();
                        limitSearch();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapTravellerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_map_traveller);

        mTokenAccess= new TokenAccess();
        mAuthAccess = new AuthAccess();
        mGeofireAccess = new GeofireAccess("active_driver");
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Geri dön");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        }
        mPlaces = Places.createClient(this);
        instanceAutocompleteSource();
        instanceAutocompleteDestination();
        onCameraMove();
        generateToken();

        binding.ButonOtostop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDriver();
            }


        });

    }

    private void requestDriver() {

        if (mSourceLating != null /*&& mDestinationLating != null*/) {

            Intent intent = new Intent(MapTravellerActivity.this, RequestDriverActivity.class);
            intent.putExtra("origin_lat", mSourceLating.latitude);
            intent.putExtra("origin_lng", mSourceLating.longitude);
            // intent.putExtra("destination_lat", mDestinationLating.latitude);
            //intent.putExtra("destination_lng", mDestinationLating.longitude);
            intent.putExtra("origin", mSource);
            //intent.putExtra("destination",mDestination);

            startActivity(intent);

        } else {
            Toast.makeText(this, "Varış yerini seçmelisiniz", Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void limitSearch() {
        LatLng northSide = SphericalUtil.computeOffset(mCurrentLating, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLating, 5000, 180);
        mAutocomplete.setCountry("");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        mAutocompleteDestination.setCountry("");
        mAutocompleteDestination.setLocationBias(RectangularBounds.newInstance(southSide, northSide));


    }

    private void onCameraMove() {
        //Yolcu Konumu arama kısmında , mevcut konumun adını gösterir.
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(MapTravellerActivity.this);
                    mSourceLating = mMap.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mSourceLating.latitude, mSourceLating.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    String address = addressList.get(0).getAddressLine(0);
                    mSource = address + " " + city;
                    mAutocomplete.setText(address + " " + city);


                } catch (Exception e) {
                    Log.d("Eror :", "Hata Mesajı " + e.getMessage());
                }

            }
        };
    }


    private void instanceAutocompleteSource() {
        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autoCompleteSource);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocomplete.setHint("Varış Yeri");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mSource = place.getName();
                mSourceLating = place.getLatLng();
                Log.d("KONUM", "İsim:" + mSource);
                Log.d("KONUM", "Enlem:" + mSourceLating.latitude);
                Log.d("KONUM", "Boylam:" + mSourceLating.longitude);

            }
        });

    }

    private void instanceAutocompleteDestination() {
        mAutocompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autoCompleteDestination);
        mAutocompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocompleteDestination.setHint("Yolcu Konumu");
        mAutocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestination = place.getName();
                mDestinationLating = place.getLatLng();
                Log.d("KONUM", "İsim:" + mDestination);
                Log.d("KONUM", "Enlem:" + mDestinationLating.latitude);
                Log.d("KONUM", "Boylam:" + mDestinationLating.longitude);


            }
        });

    }


    // Yolcu kısmında sürücünün konumunun haritada gözükmesi
    private void getActiveDriver() {
        mGeofireAccess.getActiveDriver(mCurrentLating, 10).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for (Marker marker : mDriverMarker) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }

                    }
                }
                LatLng driverLating = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLating).title("Mevcut Sürücü").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_car)));
                marker.setTag(key);
                mDriverMarker.add(marker);

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker : mDriverMarker) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            mDriverMarker.remove(marker);
                            return;
                        }

                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker marker : mDriverMarker) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.setPosition(new LatLng(location.latitude, location.longitude));

                        }

                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true); // Haritadaki yakınlaştırma
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    //Erişim izinleri
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
        if (requestCode == SETTINGS_REQUEST_CODE && gps()) {
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
                .setCancelable(false)
                .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                })
                .setNegativeButton("İptal", null)
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
                                ActivityCompat.requestPermissions(MapTravellerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapTravellerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

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
        mAuthAccess.exit();
        Intent intent = new Intent(MapTravellerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
 void generateToken(){
mTokenAccess.create(mAuthAccess.getId());
 }


}