package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.Adapters.CompletedOrderUserAdapter;
import com.vaaq.fixmyphone.ChatActivity;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER_IDS;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class CompletedOrderVendorActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CompletedOrderUserAdapter adapter;
    ArrayList<ActiveOrder> list;

    DialogHelper dialogHelper;

    ArrayList<String> activeOrderIds = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String TAG = "TAKA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_order_vendor);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        dialogHelper = new DialogHelper(this);

        list = new ArrayList<>();

        adapter = new CompletedOrderUserAdapter(list, this, this);
        adapter.setOnItemClickListener(new CompletedOrderUserAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Intent intent = new Intent(CompletedOrderVendorActivity.this, ChatActivity.class);
//                intent.putExtra("activeOrder", list.get(position));
//                intent.putExtra("from", VENDOR);
//                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewActiveOrderVendor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        getActiveOrderKeys();
    }


    void getActiveOrderKeys() {

        activeOrderIds.clear();
        list.clear();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(VENDOR)
                .child(uid)
                .child(COMPLETED_ORDER_IDS);

        dialogHelper.showProgressDialog("Fetching Completed Orders");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() == null){
                    Toast.makeText(CompletedOrderVendorActivity.this, "No Active Orders", Toast.LENGTH_SHORT).show();
                    dialogHelper.hideProgressDialog();
                    return;
                }
                Log.i(TAG, snapshot.getValue().toString());
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                ArrayList<String> keys = new ArrayList<>(hashMap.keySet());
                for (String key : keys) {
                    String orderId = hashMap.get(key);
                    activeOrderIds.add(orderId);
                }

                if (activeOrderIds.size() > 0) {
                    getActiveOrder(activeOrderIds.get(0));
                } else {
                    Toast.makeText(CompletedOrderVendorActivity.this, "No Active Order", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogHelper.hideProgressDialog();
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    void getActiveOrder(String orderId) {



        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(COMPLETED_ORDER)
                .child(orderId);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, snapshot.getValue().toString());
                HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();

                String orderStatus = hashMap.get("orderStatus").toString();
                String paymentStatus = hashMap.get("paymentStatus").toString();
                String rateAndReviewStatus = hashMap.get("rateAndReviewStatus").toString();
                String userName = hashMap.get("userName").toString();
                String vendorName = hashMap.get("vendorName").toString();
                String userId = hashMap.get("userId").toString();
                String vendorId = hashMap.get("vendorId").toString();
                String brand = hashMap.get("brand").toString();
                String model = hashMap.get("model").toString();
                String description = hashMap.get("description").toString();
                String shopName = hashMap.get("shopName").toString();
                String message = hashMap.get("message").toString();
                String quote = hashMap.get("quote").toString();
                long time = Long.parseLong(hashMap.get("time").toString());

                ActiveOrder activeOrder = new ActiveOrder(
                        orderId,
                        orderStatus,
                        paymentStatus,
                        rateAndReviewStatus,
                        userName,
                        vendorName,
                        userId,
                        vendorId,
                        brand,
                        model,
                        description,
                        shopName,
                        message,
                        quote,
                        time
                );

                list.add(activeOrder);
                activeOrderIds.remove(0);
                if(activeOrderIds.size() > 0){
                    getActiveOrder(activeOrderIds.get(0));
                }
                else {
                    adapter.notifyDataSetChanged();
                    dialogHelper.hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogHelper.hideProgressDialog();
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Completed Orders");
        imageView.setOnClickListener(v -> onBackPressed());
    }

}