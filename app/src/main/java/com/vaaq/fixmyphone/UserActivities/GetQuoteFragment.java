package com.vaaq.fixmyphone.UserActivities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;

import java.util.Date;

import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GetQuoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GetQuoteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GetQuoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GetQuoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GetQuoteFragment newInstance(String param1, String param2) {
        GetQuoteFragment fragment = new GetQuoteFragment();
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


    private static String TAG = "Taka GetQuoteActivity";

    Spinner spinnerBrand;
    EditText editTextModel;
    EditText editTextDescription;
    Button buttonSubmit;

    DialogHelper dialogHelper;
    NetworkHelper networkHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_quote, container, false);


        initViews(view);
        dialogHelper = new DialogHelper(getContext());
        networkHelper = new NetworkHelper(getContext());

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String brand = spinnerBrand.getSelectedItem().toString().trim();
                String model = editTextModel.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                if (brand.equals("Select")) {
                    Toast.makeText(getContext(), "Kindly select brand", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.length() == 0) {
                    Toast.makeText(getContext(), "Kindly enter model", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.length() == 0) {
                    Toast.makeText(getContext(), "Kindly enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uid.length() == 0 || uid == null) {
                    Toast.makeText(getContext(), "Some error occurred kindly login again", Toast.LENGTH_SHORT).show();
                    return;
                }

                GetQuote getQuote = new GetQuote(name, brand, model, description, uid, new Date().getTime());

                new SubmitGetQuoteRequestTask().execute(getQuote);
            }
        });


        return view;
    }


    void initViews(View view) {
        spinnerBrand = view.findViewById(R.id.spinnerGetQuoteBrand);
        editTextModel = view.findViewById(R.id.editTextGetQuoteModel);
        editTextDescription = view.findViewById(R.id.editTextGetQuoteDescription);
        buttonSubmit = view.findViewById(R.id.buttonGetQuoteSubmit);
    }

    class SubmitGetQuoteRequestTask extends AsyncTask<GetQuote, Void, GetQuote> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogHelper.showProgressDialog("Submitting Request");
        }

        @Override
        protected GetQuote doInBackground(GetQuote... getQuotes) {

            if (networkHelper.isConnected()) {
                return getQuotes[0];
            }
            return null;
        }

        @Override
        protected void onPostExecute(GetQuote getQuote) {
            super.onPostExecute(getQuote);

            if (getQuote != null) {
                submitGetQuoteRequest(getQuote);
            } else {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
            }
        }
    }

    private void submitGetQuoteRequest(GetQuote getQuote) {
        FirebaseDatabase.getInstance().getReference()
                .child(GET_QUOTE)
                .child(getQuote.getUid())
                .push()
                .setValue(getQuote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        Toast.makeText(getContext(), "Request submitted", Toast.LENGTH_SHORT).show();
                        spinnerBrand.setSelection(0);
                        editTextModel.setText("");
                        editTextDescription.setText("");
                        dialogHelper.hideProgressDialog();
                        startActivity(new Intent(getContext(),QuotesActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "Signup DB Complete");

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup DB Canceled");
                    }
                });
    }

}