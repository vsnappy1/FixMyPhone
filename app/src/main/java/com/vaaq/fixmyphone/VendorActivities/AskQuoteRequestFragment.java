package com.vaaq.fixmyphone.VendorActivities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AskQuoteRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AskQuoteRequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AskQuoteRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AskQuoteRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AskQuoteRequestFragment newInstance(String param1, String param2) {
        AskQuoteRequestFragment fragment = new AskQuoteRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView recyclerView;
    AskQuoteRequestAdapter adapter;
    ArrayList<GetQuote> list;

    DialogHelper dialogHelper;
    DatabaseReference firebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ask_quote_request, container, false);

        initViews(view);

        dialogHelper = new DialogHelper(getContext());

        list = new ArrayList<>();
        adapter = new AskQuoteRequestAdapter(list, getContext(), getActivity());
        adapter.setOnItemClickListener(new AskQuoteRequestAdapter.ClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Toast.makeText(getContext(), list.get(position).getDescription(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), SubmitQuoteActivity.class);
                intent.putExtra("quoteRequest", list.get(position));
                startActivity(intent);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(GET_QUOTE);

        fetchGetQuoteRequests();
    
    
        return view;
    }


    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewAskQuoteRequests);
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

    class ParseMapTask extends AsyncTask<DataSnapshot, Void, ArrayList<GetQuote>> {

        @Override
        protected ArrayList<GetQuote> doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot snapshot = dataSnapshots[0];

            if(snapshot.getValue() == null){
                return new ArrayList<>();
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
                Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if(getQuotes.size() > 0){
                list.clear();
                list.addAll(getQuotes);
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(getContext(), "No requests", Toast.LENGTH_SHORT).show();
            }

        }
    }


}