package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.utils.Constant;
import com.vaaq.fixmyphone.utils.DialogHelper;

import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER;

public class CompleteOrderActivity extends AppCompatActivity {


    private static final String TAG = "YOYO";

    Button buttonMakePayment;
    Button buttonRateReviewVendor;

    ActiveOrder activeOrder;

    DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);

        dialogHelper = new DialogHelper(this);
        initViews();

        Intent intent = getIntent();
        activeOrder = (ActiveOrder)intent.getSerializableExtra("activeOrder");




        buttonMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change payment status
                makePayment();
            }
        });

        buttonRateReviewVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateVendor();
            }
        });
    }

    void initViews(){
        buttonMakePayment = findViewById(R.id.buttonMakePayment);
        buttonRateReviewVendor = findViewById(R.id.buttonRateAndReviewVendor);
    }

    void makePayment(){

        dialogHelper.showProgressDialog("Making payment");

        FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId()).child("paymentStatus").setValue(Constant.PAYMENT_STATUS_PAID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Payment Made", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "Signup DB Complete");
                        dialogHelper.hideProgressDialog();

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup DB Canceled");
                        dialogHelper.hideProgressDialog();


                    }
                });


    }

    void rateVendor(){

        dialogHelper.showProgressDialog("Making payment");

        FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId()).child("rateAndReviewStatus").setValue(Constant.RAR_STATUS_RATED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Payment Made", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "Signup DB Complete");
                        dialogHelper.hideProgressDialog();

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup DB Canceled");
                        dialogHelper.hideProgressDialog();


                    }
                });


    }



}