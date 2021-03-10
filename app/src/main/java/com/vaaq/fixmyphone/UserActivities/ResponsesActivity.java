package com.vaaq.fixmyphone.UserActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vaaq.fixmyphone.Adapters.QuoteAdapter;
import com.vaaq.fixmyphone.Adapters.ResponseAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.models.Quote;

import java.util.ArrayList;

public class ResponsesActivity extends AppCompatActivity {

    ArrayList<Quote> list;
    GetQuote getQuote;
    RecyclerView recyclerView;
    ResponseAdapter adapter;

    public static String shopName;
    public static String message;
    public static String quote;
    public static String vendorId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);


        Intent intent = getIntent();
        getQuote = (GetQuote) intent.getSerializableExtra("quoteRequest");
        list = getQuote.getList();

        recyclerView = findViewById(R.id.recyclerViewResponses);
        adapter = new ResponseAdapter(list,this,getParent());
        adapter.setOnItemClickListener(new ResponseAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent1 = new Intent(ResponsesActivity.this, VendorProfileActivity.class);
                shopName = list.get(position).getShopName();
                message = list.get(position).getMessage();
                quote = list.get(position).getQuote();
                vendorId = list.get(position).getVendorId();

                intent1.putExtra("vendorId",list.get(position).getVendorId());
                startActivity(intent1);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }


}