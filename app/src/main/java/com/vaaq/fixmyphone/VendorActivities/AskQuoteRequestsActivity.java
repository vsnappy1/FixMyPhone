package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.Adapters.AskQuoteRequestAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;

public class AskQuoteRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AskQuoteRequestAdapter adapter;
    ArrayList<GetQuote> list;

    DialogHelper dialogHelper;
    DatabaseReference firebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_quote_requests_screen);

        initViews();

        dialogHelper = new DialogHelper(AskQuoteRequestsActivity.this);

        list = new ArrayList<>();
        adapter = new AskQuoteRequestAdapter(list, AskQuoteRequestsActivity.this, getParent());
        adapter.setOnItemClickListener(new AskQuoteRequestAdapter.ClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Toast.makeText(AskQuoteRequestsActivity.this, list.get(position).getDescription(), Toast.LENGTH_SHORT).show();

//                TextView textViewName = itemView.findViewById(R.id.textViewItemAskQuoteUsername);
//                TextView textViewBrand = itemView.findViewById(R.id.textViewItemAskQuoteBrand);
//                TextView textViewModel = itemView.findViewById(R.id.textViewItemAskQuoteModel);
//                TextView textViewDescription = itemView.findViewById(R.id.textViewItemAskQuoteDescription);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getParent(),
//                        Pair.create(textViewName, ViewCompat.getTransitionName(textViewName)),
//                        Pair.create(textViewBrand, ViewCompat.getTransitionName(textViewBrand)),
//                        Pair.create(textViewModel, ViewCompat.getTransitionName(textViewModel)),
//                        Pair.create(textViewDescription, ViewCompat.getTransitionName(textViewDescription)));

                Intent intent = new Intent(AskQuoteRequestsActivity.this, SubmitQuoteActivity.class);
                intent.putExtra("quoteRequest", list.get(position));
                startActivity(intent);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(GET_QUOTE);

        fetchGetQuoteRequests();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewAskQuoteRequests);
    }

    void fetchGetQuoteRequests(){

        dialogHelper.showProgressDialog("Fetching...");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                new ParseMapTask().execute(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogHelper.hideProgressDialog();
            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }



    class ParseMapTask extends AsyncTask<DataSnapshot, Void, ArrayList<GetQuote>>{

        @Override
        protected ArrayList<GetQuote> doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot snapshot = dataSnapshots[0];

            if(snapshot.getValue() == null){
                return null;
            }
            ArrayList<GetQuote> list = new ArrayList<>();

            HashMap<String, Object> map = new HashMap<>();
            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                map.put(dataSnapshot.getKey(), dataSnapshot.getValue());
            }
            ArrayList<String> keys = new ArrayList<>(map.keySet());


            for(String key : keys){
                HashMap<String, Object> internalMap  = (HashMap<String, Object>) map.get(key);
                ArrayList<String> internalMapKeys = new ArrayList<>(internalMap.keySet());
                for(String internalKey: internalMapKeys){
                    HashMap<String, Object> getQuoteMap = (HashMap<String, Object>) internalMap.get(internalKey);

                    String name = getQuoteMap.get("name").toString();
                    String brand = getQuoteMap.get("brand").toString();
                    String model = getQuoteMap.get("model").toString();
                    String description = getQuoteMap.get("description").toString();
                    String uid = key;
                    String userRequestId = internalKey;
                    long time = Long.parseLong(getQuoteMap.get("time").toString());

                    GetQuote getQuote = new GetQuote(name, brand, model, description, uid, userRequestId, time);
                    list.add(getQuote);
                }
            }
            if(list.size() > 0){
                return list;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GetQuote> getQuotes) {
            super.onPostExecute(getQuotes);
            dialogHelper.hideProgressDialog();

            if(getQuotes == null){
                Toast.makeText(AskQuoteRequestsActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if(getQuotes.size() > 0){
                list.clear();
                list.addAll(getQuotes);
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(AskQuoteRequestsActivity.this, "No requests", Toast.LENGTH_SHORT).show();
            }

        }
    }
}