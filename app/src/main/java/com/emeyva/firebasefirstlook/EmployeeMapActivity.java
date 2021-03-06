package com.emeyva.firebasefirstlook;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;
import java.util.ArrayList;

public class EmployeeMapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap mMap;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient mFusedLocationClient;
    Marker mCurrLocalMarker;
    private final String TAG = "MAP_LOGIN";
    public static ArrayList posArray = new ArrayList();
    public static ArrayList salArray = new ArrayList();
    public static ArrayList userDetails = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Jobs");
        currentUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String Position="";
                    String Salary="";
                    for (DataSnapshot data1 : data.getChildren()){
                        if (data1.getKey().toString().equals("details")){
                            Position=data1.child("Position").getValue().toString();
                            Salary=data1.child("Salary").getValue().toString();
                            posArray.add(Position);
                            salArray.add(Salary);

                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Trying to create Job location in:" + databaseError);

            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Person").child(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("User_name").getValue(String.class);
                userDetails.add(name);
                String age = dataSnapshot.child("age").getValue(String.class);
                userDetails.add(age);
                String city = dataSnapshot.child("city").getValue(String.class);
                userDetails.add(city);
                String country = dataSnapshot.child("country").getValue(String.class);
                userDetails.add(country);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        setContentView(R.layout.activity_employee_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        findViewById(R.id.user_account_map).setOnClickListener(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Jobs");
        currentUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Double latitude=0.0;
                    Double longitude=0.0;
                    String Position="";
                    String Salary="";
                    for (DataSnapshot data1 : data.getChildren()){
                        if (data1.getKey().toString().equals("details")){
                            Position=data1.child("Position").getValue().toString();
                            Salary=data1.child("Salary").getValue().toString();
                        }else{
                            latitude=Double.valueOf(data1.child("l").child("0").getValue().toString());
                            longitude=Double.valueOf(data1.child("l").child("1").getValue().toString());
                        }

                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.title(Position + " -> " + Salary);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_store_map));
                    mMap.addMarker(markerOptions);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Trying to create Job location in:" + databaseError);

            }
        });



        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }else{
                checkLocationPermission();
            }
        }else{
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()){
                //delete last Marker
                if (mCurrLocalMarker != null) {
                    mCurrLocalMarker.remove();
                }

                //new location
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_userrunning));
                mCurrLocalMarker=mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            }
        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Give Permission")
                        .setMessage("give permission!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EmployeeMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(EmployeeMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_account_map:
                startActivity(new Intent(EmployeeMapActivity.this,Welcome.class));
                break;


        }
    }
}
