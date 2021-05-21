package com.example.easypass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView imageView;
    private static final int DELAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.icon);
        imageView.animate().rotationBy(360).setDuration(DELAY_TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent i = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
        }, 3000);
    }
}