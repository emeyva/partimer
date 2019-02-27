package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.emeyva.firebasefirstlook.LogInActivity;

public class ViewJobUser extends AppCompatActivity
    implements View.OnClickListener{
    private final String TAG = "FB_SIGNIN";

    // TODO: Add Auth members
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList positionArray = EmployeeMapActivity.posArray;
    ArrayList salaryArray = EmployeeMapActivity.salArray;


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
        setContentView(R.layout.activity_view_job_user);




        // Set up click handlers and view item references
        findViewById(R.id.goBackbtn).setOnClickListener(this);

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

        ListView listView =(ListView)findViewById(R.id.listView);
        CustomAdapter customAdapter=new CustomAdapter();
        listView.setAdapter(customAdapter);



    }
    class CustomAdapter extends BaseAdapter{
        int x=0;
        @Override
        public int getCount() {
            x=positionArray.size();

            return x;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.custom_job_layout,null);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageViewJob);
            TextView textViewPosition = (TextView)convertView.findViewById(R.id.textView_position);
            TextView textViewSalary = (TextView)convertView.findViewById(R.id.textView2_salary);


            imageView.setImageResource(R.mipmap.ic_store_map);
            textViewPosition.setText((String)positionArray.get(position));
            textViewSalary.setText((String)salaryArray.get(position));

            return convertView;
        }

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

            case R.id.goBackbtn:
                startActivity(new Intent(ViewJobUser.this,Welcome.class));
                break;


        }
    }


    private void updateStatus() {
        // TODO: get the current user
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            Toast.makeText(ViewJobUser.this,
                    "Sign in", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ViewJobUser.this,
                    "Sign Out", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateStatus(String stat) {
        Toast.makeText(ViewJobUser.this,
                stat, Toast.LENGTH_SHORT).show();
    }


}
