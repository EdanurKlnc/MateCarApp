package com.example.matecarapp.activity.traveller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.matecarapp.R;
import com.example.matecarapp.access.GoogleApiAccess;
import com.example.matecarapp.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsRequestTravellerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    Toolbar mToolbar;

    private double mExtraSourceLat;
    private double mExtraSourceLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;

    private String mExtraSource;
    private String mExtraDestination;

    private TextView mTextViewOrigin, mTextViewDestination, mTextViewTime, mTextViewDistance;
    private Button mbutonOtostop;

    private LatLng mSourseLating;
    private LatLng mDestinationLating;

    private GoogleApiAccess mGoogleApiAccess;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_request_traveller);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Geri dönünüz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mExtraSourceLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraSourceLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mExtraSource = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");

        mTextViewOrigin.setText(mExtraSource);
        mTextViewDestination.setText(mExtraDestination);

        mbutonOtostop=findViewById(R.id.btnRequestNow);


        mTextViewOrigin = findViewById(R.id.textViewOrigin);
        mTextViewDestination = findViewById(R.id.textViewDestination);
        mTextViewDistance = findViewById(R.id.textViewDistance);
        mTextViewTime = findViewById(R.id.textViewTime);


        mSourseLating = new LatLng(mExtraSourceLat, mExtraSourceLng);
        mDestinationLating = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGoogleApiAccess = new GoogleApiAccess(DetailsRequestTravellerActivity.this);

        mbutonOtostop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRequestDriver();
            }
        });
    }
    private void goToRequestDriver(){
        Intent intent = new Intent(DetailsRequestTravellerActivity.this,RequestDriverActivity.class);
        intent.putExtra("origin_lat",mSourseLating.latitude);
        intent.putExtra("origin_lng",mSourseLating.longitude);
        intent.putExtra("origin",mExtraSource);
        intent.putExtra("destination",mExtraDestination);
        intent.putExtra("destination_lng",mDestinationLating.longitude);
        intent.putExtra("destination_lat",mDestinationLating.latitude);


        startActivity(intent);
        finish();

    }

    private void drawRoute() {
        mGoogleApiAccess.getDirections(mSourseLating, mDestinationLating).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = new JSONObject().getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(12f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    //json verilerini internettekilerle eşleştiriyoruz

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    mTextViewTime.setText(durationText);
                    mTextViewDistance.setText(distanceText);



                } catch (Exception e) {
                    Log.d("Error", "Hata!!!" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true); // Haritadaki yakınlaştırma

        mMap.addMarker(new MarkerOptions().position(mSourseLating).title("Source").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLating).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mSourseLating)
                        .zoom(14f)
                        .build()

        ));
        drawRoute();
    }
}