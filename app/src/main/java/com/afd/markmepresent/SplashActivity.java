package com.afd.markmepresent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                try {
                    Thread.sleep(1500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();

            }
        }).start();

    }
}