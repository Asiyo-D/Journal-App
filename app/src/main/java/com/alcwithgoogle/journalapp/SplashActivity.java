package com.alcwithgoogle.journalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;


public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Context context;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        handler = new Handler();
        context = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(context, MainActivity.class));
                    finishAffinity();
                } else {
                    startActivity(new Intent(context, LoginActivity.class));
                    finishAffinity();
                }
            }
        }, 2500);

    }

    @Override
    public void onBackPressed() {
    }
}
