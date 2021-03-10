package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.HashMap;

import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class VendorProfileActivity extends AppCompatActivity {

    DialogHelper dialogHelper;

    TextView textViewVendorName;
    TextView textViewShopName;
    TextView textViewShopAddress;
    Button buttonProceed;
    RecyclerView recyclerView;

    public static String vendorName;
    public static String shopName;
    public static String shopAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        initViews();

        dialogHelper = new DialogHelper(this);

        fetchVendorDetails(getIntent().getStringExtra("vendorId"));

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorProfileActivity.this, OrderConfirmationActivity.class);
                startActivity(intent);

            }
        });

    }

    void initViews() {
        textViewVendorName = findViewById(R.id.textViewVendorProfileName);
        textViewShopName = findViewById(R.id.textViewVendorProfileShopName);
        textViewShopAddress = findViewById(R.id.textViewVendorProfileAddress);
        buttonProceed = findViewById(R.id.buttonVendorProfileProceed);
        recyclerView = findViewById(R.id.recyclerViewVendorProfile);
    }


    void fetchVendorDetails(String uid) {

        dialogHelper.showProgressDialog("Loading...");

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(VENDOR)
                .child(uid);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();

                vendorName = hashMap.get("name");
                shopName = hashMap.get("shopName");
                shopAddress = hashMap.get("shopAddress");


                textViewVendorName.setText(vendorName);
                textViewShopName.setText(shopName);
                textViewShopAddress.setText(shopAddress);

                dialogHelper.hideProgressDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogHelper.hideProgressDialog();
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
}