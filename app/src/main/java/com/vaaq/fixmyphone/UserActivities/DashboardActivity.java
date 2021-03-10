package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import static com.vaaq.fixmyphone.utils.Constant.USER;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class DashboardActivity extends AppCompatActivity {

    ImageView imageViewEdit;
    TextView  textViewUsername;
    TextView  textViewUserAddress;
    Button    buttonGetQuote;
    Button    buttonActiveOrder;
    Button    buttonCompletedOrder;
    Button    buttonQuotes;

    public static String address;
    public static String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
            }
        });

        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GetQuoteActivity.class ));
            }
        });

        buttonActiveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActiveOrderActivity.class ));
            }
        });

        buttonCompletedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CompletedOrderActivity.class));
            }
        });

        buttonQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QuotesActivity.class ));
            }
        });
        fetchUserDetails();
    }

    void initViews(){
        imageViewEdit = findViewById(R.id.imageViewDashboardEdit);
        textViewUsername = findViewById(R.id.textViewDashboardName);
        textViewUserAddress = findViewById(R.id.textViewDashboardAddress);
        buttonGetQuote = findViewById(R.id.buttonDashboardGetQuote);
        buttonActiveOrder = findViewById(R.id.buttonDashboardActiveOrder);
        buttonCompletedOrder = findViewById(R.id.buttonDashboardCompletedOrder);
        buttonQuotes = findViewById(R.id.buttonDashboardQuotes);
    }

    void fetchUserDetails(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(USER)
                .child(firebaseUser.getUid());


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                address = hashMap.get("address");
                userName = hashMap.get("name");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
}