package com.vaaq.fixmyphone.UserActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vaaq.fixmyphone.Adapters.QuoteAdapter;
import com.vaaq.fixmyphone.Adapters.ResponseAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.models.Quote;

import java.util.ArrayList;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.USER;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class ResponsesActivity extends AppCompatActivity {

    ArrayList<Quote> list;
    GetQuote getQuote;
    RecyclerView recyclerView;
    ResponseAdapter adapter;

    public static String shopName;
    public static String message;
    public static String quote;
    public static String vendorId;

    TextView textViewResponseBrand;
    TextView textViewSubmitQuoteModel;
    TextView textViewSubmitQuoteDescription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        textViewResponseBrand = findViewById(R.id.textViewResponseBrand);
        textViewSubmitQuoteModel = findViewById(R.id.textViewSubmitQuoteModel);
        textViewSubmitQuoteDescription = findViewById(R.id.textViewSubmitQuoteDescription);


        Intent intent = getIntent();
        getQuote = (GetQuote) intent.getSerializableExtra("quoteRequest");
        textViewResponseBrand.setText(getQuote.getBrand());
        textViewSubmitQuoteModel.setText(getQuote.getModel());
        textViewSubmitQuoteDescription.setText(getQuote.getDescription());
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
                intent1.putExtra("from", USER);
                startActivity(intent1);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }


    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Responses");
        imageView.setOnClickListener(v -> onBackPressed());
    }


}