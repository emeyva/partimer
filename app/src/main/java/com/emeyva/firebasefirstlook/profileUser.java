package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class profileUser extends AppCompatActivity
    implements View.OnClickListener {
    private final String TAG = "FB_FIRSTLOOK";

    //TODO: token FCM


    // Firebase Remote Config settings
    private final String CONFIG_PROMO_MESSAGE_KEY = "promo_message";
    private final String CONFIG_PROMO_ENABLED_KEY = "promo_enabled";
    private long PROMO_CACHE_DURATION = 1800;
    private TextView ProName;
    private TextView ProCity;
    private TextView ProCountry;
    private TextView ProAge;

    // Firebase Analytics settings
    private final int MIN_SESSION_DURATION = 5000;

    // TODO: define analytics object

    private FirebaseAnalytics mFBAnalytics;
    private FirebaseAuth mAuth;

    // TODO: define Remote Config object
    private FirebaseRemoteConfig mFBConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user);
        //TODO: Check intent
        if (getIntent().getExtras() != null){
            String launchMsg = "";
            for (String key: getIntent().getExtras().keySet()){
                Object val = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + val + "\n");
                launchMsg += "Key: " + key + " Value: " + val + "\n";
            }
            setStatus(launchMsg);

        }
        // TODO: Text variables

        ProName = (TextView)findViewById(R.id.profileName);
        ProCity = (TextView)findViewById(R.id.profileCity);
        ProCountry = (TextView)findViewById(R.id.profileCountry);
        ProAge = (TextView)findViewById(R.id.profileAge);








        // TODO: Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        // TODO: get FCM default token


        // TODO: Retrieve an instance of the Analytics package

        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        // TODO: Get the Remote Config instance
        mFBConfig = FirebaseRemoteConfig.getInstance();

        // TODO: Wait 5 seconds before counting this as a session
        mFBAnalytics.setMinimumSessionDuration(MIN_SESSION_DURATION);

        // TODO: Add Remote Config Settings
        // Enable developer mode to perform more rapid testing.
        // Config fetches are normally limited to 5 per hour. This
        // enables many more requests to facilitate testing.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFBConfig.setConfigSettings(configSettings);

        // TODO: add Signout and Promo Button
        findViewById(R.id.profileHome).setOnClickListener(this);
        findViewById(R.id.btnPromo).setOnClickListener(this);
        findViewById(R.id.viewJobbtn).setOnClickListener(this);

        // TODO: Get the default parameter settings from the XML file
        mFBConfig.setDefaults(R.xml.firstlook_config_params);




        // TODO: DATABASE
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                ProName.setText("Name: " + name);
                String age = dataSnapshot.child("age").getValue(String.class);
                ProAge.setText("Age: " + age);
                String city = dataSnapshot.child("city").getValue(String.class);
                ProCity.setText("City: " + city);
                String country = dataSnapshot.child("country").getValue(String.class);
                ProCountry.setText("Country: " + country);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        // Read from the database


        // set up button click handlers


        //welcomeUser = (TextView)findViewById(R.id.welcomeUser);
        //welcomeUser.setText("Welcome: " + user.getEmail() + " " + user.getDisplayName() + " " );



        // Check to see if the promo button should be enabled
        checkPromoEnabled();
    }

    private void checkPromoEnabled() {
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        // TODO: Set the cache duration for developer testing
        if (mFBConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()){
            PROMO_CACHE_DURATION=0;

        }

        // TODO: fetch the values from the Remote Config service
        mFBConfig.fetch(PROMO_CACHE_DURATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i(TAG, "Promo check was successful");

                            mFBConfig.activateFetched();
                        }
                        else {
                            Log.e(TAG, "Promo check failed");
                        }
                        showPromoButton();
                    }
                });

    }

    private void showPromoButton() {
        // Determine whether the show the promo button and what
        // the promo message should be to give to the user
        boolean showBtn = false;
        String promoMsg = "";

        // TODO: get the promo setting from Remote Config
        showBtn = mFBConfig.getBoolean(CONFIG_PROMO_ENABLED_KEY);
        promoMsg = mFBConfig.getString(CONFIG_PROMO_MESSAGE_KEY);

        Button btn = (Button)findViewById(R.id.btnPromo);
        btn.setVisibility(showBtn ? View.VISIBLE : View.INVISIBLE);
        btn.setText(promoMsg);
    }

    @Override
    public void onClick(View v) {
        // Create the Bundle that will hold the data sent to
        // the Analytics package
        Bundle params = new Bundle();
        params.putInt("ButtonID", v.getId());
        String btnName;

        switch (v.getId()) {
            case R.id.btnPromo:
                btnName = "ButtonPromoClick";
                setStatus("btnPromo clicked");
                startActivity(new Intent(this, PromoScreen.class));
                break;
            case R.id.profileHome:
                btnName = "SignOutk";
                setStatus("Signed out");

                startActivity(new Intent(this, Welcome.class));
                break;
            case R.id.viewJobbtn:
                btnName = "Job View";
                setStatus("Job View");

                startActivity(new Intent(this, ViewJobUser.class));
                break;
            default:
                btnName = "OtherButton";
                break;
        }
        Log.d(TAG, "Button click logged: " + btnName);

        // TODO: Log the button press as an analytics event
        mFBAnalytics.logEvent(btnName,params);

    }

    private void setStatus(String text) {
        TextView tvStat = (TextView)findViewById(R.id.tvStatus);
        tvStat.setText(text);
    }
    private void signUserOut() {
        // TODO: sign the user out
        mAuth.signOut();


    }
}
