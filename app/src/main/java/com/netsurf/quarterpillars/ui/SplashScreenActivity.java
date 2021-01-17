package com.netsurf.quarterpillars.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.netsurf.quarterpillars.R;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int SPLASH_DISPLAY_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startLogin();
    }

    private void startLogin() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
            finish();
        }, SPLASH_DISPLAY_DURATION);
    }
}