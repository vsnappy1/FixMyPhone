package com.vaaq.fixmyphone.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.vaaq.fixmyphone.R;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();

    }
}