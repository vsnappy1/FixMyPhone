package com.vaaq.fixmyphone.UserActivities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaaq.fixmyphone.Adapters.ActiveOrderUserAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER_IDS;
import static com.vaaq.fixmyphone.utils.Constant.USER;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedOrderUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedOrderUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CompletedOrderUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedOrderUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedOrderUserFragment newInstance(String param1, String param2) {
        CompletedOrderUserFragment fragment = new CompletedOrderUserFragment();
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
    ActiveOrderUserAdapter adapter;
    ArrayList<ActiveOrder> list;

    DialogHelper dialogHelper;

    ArrayList<String> activeOrderIds = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String TAG = "TAKA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_completed_order_user, container, false);


        dialogHelper = new DialogHelper(getContext());

        list = new ArrayList<>();

        adapter = new ActiveOrderUserAdapter(list, getContext(), getActivity());
        adapter.setOnItemClickListener(new ActiveOrderUserAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

//                Intent intent = new Intent(CompletedOrderActivity.this, ChatActivity.class);
//                intent.putExtra("activeOrder", list.get(position));
//                intent.putExtra("from", USER);
//                startActivity(intent);

            }
        });

        recyclerView = view.findViewById(R.id.recyclerViewActiveOrderUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        getActiveOrderKeys();





        return view;
    }


    void getActiveOrderKeys() {

        activeOrderIds.clear();
        list.clear();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(USER)
                .child(uid)
                .child(COMPLETED_ORDER_IDS);

        dialogHelper.showProgressDialog("Fetching Completed Orders");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() == null){
                    Toast.makeText(getContext(), "No Completed Order",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "No Completed Order", Toast.LENGTH_SHORT).show();
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
                if (activeOrderIds.size() > 0) {
                    getActiveOrder(activeOrderIds.get(0));
                } else {
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


}