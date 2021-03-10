package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.R;

import java.util.HashMap;

import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class DashboardVendorActivity extends AppCompatActivity {

    public static String SHOP_NAME = "ABC";

    ImageView imageViewEdit;
    TextView textViewUsername;
    TextView  textViewUserAddress;
    Button      buttonAskQuoteRequest;
    Button    buttonActiveOrder;
    Button    buttonCompletedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_vendor);

        initViews();

        buttonAskQuoteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVendorActivity.this, AskQuoteRequestsActivity.class));
            }
        });

        fetchVendorDetails();

    }

    void initViews(){
        imageViewEdit = findViewById(R.id.imageViewDashboardVendorEdit);
        textViewUsername = findViewById(R.id.textViewDashboardVendorName);
        textViewUserAddress = findViewById(R.id.textViewDashboardVendorAddress);
        buttonAskQuoteRequest = findViewById(R.id.buttonDashboardVendorAskQuote);
        buttonActiveOrder = findViewById(R.id.buttonDashboardVendorActiveOrder);
        buttonCompletedOrder = findViewById(R.id.buttonDashboardVendorCompletedOrder);
    }

    void fetchVendorDetails(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(VENDOR)
                .child(firebaseUser.getUid());


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                SHOP_NAME = hashMap.get("shopName");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
}