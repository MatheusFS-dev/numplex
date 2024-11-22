package com.app.numplex.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.app.numplex.R;
import com.app.numplex.home.HomeActivity;
import com.app.numplex.utils.Functions;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {
    // Timer da splash screen
    private static final int SPLASH_TIME_OUT = 1200;

    // Preload the HomeActivity intent in a member variable
    private Intent homeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/


        // Preload the HomeActivity intent
        homeIntent = new Intent(this, HomeActivity.class);

        new Handler().postDelayed(() -> {
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}