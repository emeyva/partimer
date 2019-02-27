package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class showPartimer extends AppCompatActivity {
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

    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partimer_show);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(showPartimer.this, LogInActivity.class));
            }

        }, 1000);


        //TODO: Check intent


        // TODO: Get the default parameter settings from the XML file


        // set up button click handlers


        // Check to see if the promo button should be enabled

    }

}
