package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.vaaq.fixmyphone.Adapters.AskQuoteRequestAdapter;
import com.vaaq.fixmyphone.Adapters.QuoteAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.models.Quote;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.vaaq.fixmyphone.VendorActivities.DashboardVendorActivity.SHOP_NAME;
import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;

public class QuotesActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    QuoteAdapter adapter;
    ArrayList<GetQuote> list;

    DialogHelper dialogHelper;
    DatabaseReference firebaseDatabaseReference;

    ArrayList<String> keys;
    public static String requestKey;

    public static String brand;
    public static String model;
    public static String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        initViews();

        dialogHelper = new DialogHelper(QuotesActivity.this);

        list = new ArrayList<>();
        adapter = new QuoteAdapter(list, QuotesActivity.this, getParent());
        adapter.setOnItemClickListener(new QuoteAdapter.ClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Toast.makeText(QuotesActivity.this, list.get(position).getDescription(), Toast.LENGTH_SHORT).show();

//                TextView textViewName = itemView.findViewById(R.id.textViewItemAskQuoteUsername);
//                TextView textViewBrand = itemView.findViewById(R.id.textViewItemAskQuoteBrand);
//                TextView textViewModel = itemView.findViewById(R.id.textViewItemAskQuoteModel);
//                TextView textViewDescription = itemView.findViewById(R.id.textViewItemAskQuoteDescription);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getParent(),
//                        Pair.create(textViewName, ViewCompat.getTransitionName(textViewName)),
//                        Pair.create(textViewBrand, ViewCompat.getTransitionName(textViewBrand)),
//                        Pair.create(textViewModel, ViewCompat.getTransitionName(textViewModel)),
//                        Pair.create(textViewDescription, ViewCompat.getTransitionName(textViewDescription)));


                brand = list.get(position).getBrand();
                model = list.get(position).getModel();
                description = list.get(position).getDescription();

                Intent intent = new Intent(QuotesActivity.this, ResponsesActivity.class);
                requestKey = keys.get(position);
                intent.putExtra("quoteRequest", list.get(position));
                startActivity(intent);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(GET_QUOTE).child(uid);

        fetchGetQuoteRequests();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewQuotes);
    }

    void fetchGetQuoteRequests() {

        dialogHelper.showProgressDialog("Fetching...");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new QuotesActivity.ParseMapTask().execute(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogHelper.hideProgressDialog();
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }


    class ParseMapTask extends AsyncTask<DataSnapshot, Void, ArrayList<GetQuote>> {

        @Override
        protected ArrayList<GetQuote> doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot snapshot = dataSnapshots[0];

            if (snapshot.getValue() == null) {
                return new ArrayList<>();
            }
            ArrayList<GetQuote> list = new ArrayList<>();

            HashMap<String, Object> map = new HashMap<>();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                map.put(dataSnapshot.getKey(), dataSnapshot.getValue());
            }

            keys = new ArrayList<>(map.keySet());

            for (String key : keys) {
                HashMap<String, Object> getQuoteMap = (HashMap<String, Object>) map.get(key);

                String name = getQuoteMap.get("name").toString();
                String brand = getQuoteMap.get("brand").toString();
                String model = getQuoteMap.get("model").toString();
                String description = getQuoteMap.get("description").toString();
                String uid = getQuoteMap.get("uid").toString();

                long time = Long.parseLong(getQuoteMap.get("time").toString());
                ArrayList<Quote> listQuotes = new ArrayList<>();

                try {
                    HashMap<String, Object> responses = (HashMap<String, Object>) getQuoteMap.get("responses");
                    ArrayList<String> keysResponse = new ArrayList<>(responses.keySet());
                    for (String keyRes : keysResponse) {
                        HashMap<String, Object> res = (HashMap<String, Object>) responses.get(keyRes);

                        String shopName = res.get("shopName").toString();
                        String message = res.get("message").toString();
                        String quote = res.get("quote").toString();
                        String vendorId = res.get("vendorId").toString();
                        long timeQuote = Long.parseLong(getQuoteMap.get("time").toString());

                        Quote quote1 = new Quote(shopName, message, quote, vendorId, timeQuote);
                        listQuotes.add(quote1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                GetQuote getQuote = new GetQuote(name, brand, model, description, uid, time, listQuotes);
                list.add(getQuote);

            }
            if (list.size() > 0) {
                return list;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GetQuote> getQuotes) {
            super.onPostExecute(getQuotes);
            dialogHelper.hideProgressDialog();

            if (getQuotes == null) {
                Toast.makeText(QuotesActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getQuotes.size() > 0) {
                list.clear();
                list.addAll(getQuotes);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(QuotesActivity.this, "No requests", Toast.LENGTH_SHORT).show();
            }
        }
    }


    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Quotes");
        imageView.setOnClickListener(v -> onBackPressed());
    }
}