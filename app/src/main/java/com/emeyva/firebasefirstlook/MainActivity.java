package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {
    private final String TAG = "FB_FIRSTLOOK";

    //TODO: token FCM
    private String m_FCMtoken;
    //TODO: token: dqO8XI29XEc:APA91bHQ3BLg_h1UPwa3Q9U_VtwZnsUZl0BrilyN2i9KZsQRqzKhQQyiY-FbgHl2ZQ50g
    //              P7FBeDskW0VEBDuZlLD54WKhmeHLJ2pwQ4DwjiOgAOCVJnxF8HYfP1E93XVo9kd33veF05v

    // Firebase Remote Config settings
    private final String CONFIG_PROMO_MESSAGE_KEY = "promo_message";
    private final String CONFIG_PROMO_ENABLED_KEY = "promo_enabled";
    private long PROMO_CACHE_DURATION = 1800;

    // Firebase Analytics settings
    private final int MIN_SESSION_DURATION = 5000;

    // TODO: define analytics object

    private FirebaseAnalytics mFBAnalytics;

    // TODO: define Remote Config object
    private FirebaseRemoteConfig mFBConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        // TODO: get FCM default token
        m_FCMtoken = FirebaseInstanceId.getInstance().getToken();

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



        // TODO: Get the default parameter settings from the XML file
        mFBConfig.setDefaults(R.xml.firstlook_config_params);


        // set up button click handlers
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btnAuthActivity).setOnClickListener(this);
        findViewById(R.id.btnPromo).setOnClickListener(this);

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
            case R.id.btn1:
                btnName = "Button1Click";
                setStatus("Log in or create a new Account");
                Log.d(TAG, "FCM Example Token: " + m_FCMtoken);
                break;
            case R.id.btn2:
                btnName = "Button2Click";
                setStatus("");
                startActivity(new Intent(this, LogInActivity.class));
                break;
            case R.id.btnAuthActivity:
                btnName = "ButtonAuthClick";
                setStatus("");
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.btnPromo:
                btnName = "ButtonPromoClick";
                setStatus("");
                startActivity(new Intent(this, PromoScreen.class));
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
}
