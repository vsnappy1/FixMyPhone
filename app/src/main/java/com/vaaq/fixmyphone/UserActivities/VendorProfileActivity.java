package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.Adapters.RateReviewAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.RateAndReview;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.REVIEW;
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

    RateReviewAdapter adapter;
    ArrayList<RateAndReview> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();


        initViews();

        dialogHelper = new DialogHelper(this);
        list = new ArrayList<>();
        adapter = new RateReviewAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("vendorId");
        String from = intent.getStringExtra("from");

        if(from.equals(VENDOR)){
            buttonProceed.setVisibility(View.GONE);
        }

        fetchVendorDetails(uid);
        fetchVendorReviews(uid);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorProfileActivity.this, OrderConfirmationActivity.class);
                startActivity(intent);
            }
        });

        headerSetup();

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

    void fetchVendorReviews(String uid) {


        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(REVIEW)
                .child(uid);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() == null){
                    return;
                }

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds != null){
                        HashMap<String, Object> rev = (HashMap<String, Object>) ds.getValue();

                        if (rev != null) {
                            String userName = rev.get("userName").toString();
                            String review = rev.get("review").toString();
                            long starCount = (long) rev.get("starCount");
                            long time = (long) rev.get("time");

                            RateAndReview rar = new RateAndReview(userName, review, starCount, time);
                            list.add(rar);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Profile");
        imageView.setOnClickListener(v -> onBackPressed());
    }
}