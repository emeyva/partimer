package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateJobCompany extends AppCompatActivity
    implements View.OnClickListener{
    private final String TAG = "FB_SIGNIN";

    // TODO: Add Auth members
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText etPos;
    private EditText etSal;
    private EditText etLati;
    private EditText etLong;


    /**
     * Standard Activity lifecycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_company);

        // Set up click handlers and view item references
        findViewById(R.id.createJobbtn).setOnClickListener(this);
        findViewById(R.id.gobackbtn).setOnClickListener(this);
        findViewById(R.id.setmylocation).setOnClickListener(this);

        etPos = (EditText)findViewById(R.id.etPosition);
        etSal = (EditText)findViewById(R.id.etSalary);
        etLati = (EditText)findViewById(R.id.etLatitude);
        etLong = (EditText)findViewById(R.id.etLongitude);


        // TODO: Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();


        // TODO: Attach a new AuthListener to detect sign in and out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "Signed in:" + user.getUid());
                }
                else Log.d(TAG, "Currently Signed out");
            }
        };

    }

    /**
     * When the Activity starts and stops, the app needs to connect and
     * disconnect the AuthListener
     */
    @Override
    public void onStart() {
        super.onStart();
        // TODO: add the AuthListener
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: Remove the AuthListener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.createJobbtn:
                addJobLocation();
                break;

            case R.id.gobackbtn:
                startActivity(new Intent(CreateJobCompany.this,WelcomeCompany.class));
                break;

            case R.id.setmylocation:
                setmyLocation();
                break;


        }
    }
    private void setmyLocation(){
        ArrayList myLocationDet=new ArrayList();
        myLocationDet.add(0,BossMapActivity.locationDetails.get(0));
        myLocationDet.add(1,BossMapActivity.locationDetails.get(1));
        etLati.setText(String.valueOf(myLocationDet.get(0)));
        etLong.setText(String.valueOf(myLocationDet.get(1)));
    }

    private boolean checkFormFields() {
        String position, salary, latitude, longitude;

        position = etPos.getText().toString();
        salary = etSal.getText().toString();
        latitude = etLati.getText().toString();
        longitude = etLong.getText().toString();

        if (position.isEmpty()) {
            etPos.setError("Position Required");
            return false;
        }
        if (salary.isEmpty()){
            etSal.setError("Salary Required");
            return false;
        }
        if (latitude.isEmpty()){
            etLati.setError("Latitude Required");
            return false;
        }
        if (longitude.isEmpty()) {
            etLong.setError("Longitude Required");
            return false;
        }

        return true;
    }

    private void updateStatus() {
        // TODO: get the current user
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            Toast.makeText(CreateJobCompany.this,
                    "Sign in", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(CreateJobCompany.this,
                    "Sign Out", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateStatus(String stat) {
        Toast.makeText(CreateJobCompany.this,
                stat, Toast.LENGTH_SHORT).show();
    }

    private void addJobLocation(){
        if (!checkFormFields())
            return;
        String position = etPos.getText().toString();
        String salary = etSal.getText().toString();
        Double latitude = Double.valueOf(etLati.getText().toString());
        Double longitude = Double.valueOf(etLong.getText().toString());
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = mAuth.getCurrentUser().getUid();



        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Jobs").child(userId);
        GeoFire geofire = new GeoFire(currentUserDB);
        geofire.setLocation(position, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Toast.makeText(CreateJobCompany.this,
                        "Job created", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Job Created:" + error);
            }
        });

        Map newPost = new HashMap();
        newPost.put("Position", position);
        newPost.put("Salary", salary);
        currentUserDB.child("details").setValue(newPost);

        startActivity(new Intent(CreateJobCompany.this, WelcomeCompany.class));




    }

}
