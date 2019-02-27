package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity
    implements View.OnClickListener {
    private final String TAG = "FB_FIRSTLOOK";
    private TextView ProName;
    private TextView ProCity;
    private TextView ProCountry;
    private TextView ProAge;
    ArrayList userDetails = new ArrayList();
    //TODO: token FCM
    String name;

    // Firebase Remote Config settings
    private final String CONFIG_PROMO_MESSAGE_KEY = "promo_message";
    private final String CONFIG_PROMO_ENABLED_KEY = "promo_enabled";
    private long PROMO_CACHE_DURATION = 1800;

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
        setContentView(R.layout.welcome);
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
        ArrayList userDet = EmployeeMapActivity.userDetails;
        //CREATE FIELDS
        ProName = (TextView)findViewById(R.id.etName);
        ProAge = (TextView)findViewById(R.id.etAge);
        ProCity = (TextView)findViewById(R.id.etCity);
        ProCountry = (TextView)findViewById(R.id.etCountry);
        ProName.setText((String) userDet.get(0));
        ProAge.setText((String) userDet.get(1));
        ProCity.setText((String) userDet.get(2));
        ProCountry.setText((String) userDet.get(3));





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
        findViewById(R.id.signOutbtn).setOnClickListener(this);
        findViewById(R.id.backMapbtn).setOnClickListener(this);
        findViewById(R.id.viewJobbtn).setOnClickListener(this);


        // TODO: Get the default parameter settings from the XML file
        mFBConfig.setDefaults(R.xml.firstlook_config_params);




        // TODO: DATABASE
        String userId = mAuth.getCurrentUser().getUid();

        System.out.println(name);
        // Read from the database


        // set up button click handlers


        //welcomeUser = (TextView)findViewById(R.id.welcomeUser);
        //welcomeUser.setText("Welcome: " + user.getEmail() + " " + user.getDisplayName() + " " );



        // Check to see if the promo button should be enabled
        //checkPromoEnabled();
    }

    /*private void checkPromoEnabled() {
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

    }*/

    /*private void showPromoButton() {
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
    }*/

    @Override
    public void onClick(View v) {
        // Create the Bundle that will hold the data sent to
        // the Analytics package
        Bundle params = new Bundle();
        params.putInt("ButtonID", v.getId());
        String btnName;

        switch (v.getId()) {
            case R.id.signOutbtn:
                btnName = "SignOutk";
                setStatus("Signed out");
                signUserOut();
                startActivity(new Intent(this, LogInActivity.class));
                break;
            case R.id.backMapbtn:
                btnName = "goProfile";
                setStatus("Profile loading...");
                startActivity(new Intent(this, EmployeeMapActivity.class));
                break;
            case R.id.viewJobbtn:
                btnName = "Jobview";
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
        Log.d(TAG, "Set status: " + text);
        Toast.makeText(Welcome.this,
                "Status: " + text, Toast.LENGTH_SHORT).show();
    }
    private void signUserOut() {
        // TODO: sign the user out
        mAuth.signOut();


    }
}
