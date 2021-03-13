package com.vaaq.fixmyphone.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.vaaq.fixmyphone.R;

public class OrderDashboardActivity extends AppCompatActivity {


    Button buttonChat;
    Button buttonMakePayment;
    Button buttonRateAndReviewVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_dashboard);

        initViews();

        




    }

    void initViews(){
        buttonChat = findViewById(R.id.buttonOrderDashboardChat);
        buttonMakePayment = findViewById(R.id.buttonOrderDashboardMakePayment);
        buttonRateAndReviewVendor = findViewById(R.id.buttonOrderDashboardRateAndReviewVendor);
    }
}