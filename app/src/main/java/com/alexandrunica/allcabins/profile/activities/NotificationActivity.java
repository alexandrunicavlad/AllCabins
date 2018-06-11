package com.alexandrunica.allcabins.profile.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alexandrunica.allcabins.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String id = getIntent().getStringExtra("notification");
        String a ="21";
    }
}
