package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.vaaq.fixmyphone.models.User;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;

import java.util.Date;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;
import static com.vaaq.fixmyphone.utils.Constant.USER;

public class GetQuoteActivity extends AppCompatActivity {

    private static String TAG = "Taka GetQuoteActivity";


    Spinner spinnerBrand;
    EditText editTextModel;
    EditText editTextDescription;
    Button buttonSubmit;

    DialogHelper dialogHelper;
    NetworkHelper networkHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_quote);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        initViews();
        dialogHelper = new DialogHelper(GetQuoteActivity.this);
        networkHelper = new NetworkHelper(GetQuoteActivity.this);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String brand = spinnerBrand.getSelectedItem().toString().trim();
                String model = editTextModel.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                if(brand.equals("Select")){
                    Toast.makeText(GetQuoteActivity.this, "Kindly select brand", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(model.length() == 0){
                    Toast.makeText(GetQuoteActivity.this, "Kindly enter model", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(description.length() == 0){
                    Toast.makeText(GetQuoteActivity.this, "Kindly enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(uid.length() == 0 || uid == null){
                    Toast.makeText(GetQuoteActivity.this, "Some error occurred kindly login again", Toast.LENGTH_SHORT).show();
                    return;
                }

                GetQuote getQuote = new GetQuote(name, brand, model, description, uid, new Date().getTime());

                new SubmitGetQuoteRequestTask().execute(getQuote);
            }
        });
    }

    void initViews() {
        spinnerBrand = findViewById(R.id.spinnerGetQuoteBrand);
        editTextModel = findViewById(R.id.editTextGetQuoteModel);
        editTextDescription = findViewById(R.id.editTextGetQuoteDescription);
        buttonSubmit = findViewById(R.id.buttonGetQuoteSubmit);
    }

    class SubmitGetQuoteRequestTask extends AsyncTask<GetQuote, Void, GetQuote> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogHelper.showProgressDialog("Submitting Request");
        }

        @Override
        protected GetQuote doInBackground(GetQuote... getQuotes) {

            if(networkHelper.isConnected()){
                return getQuotes[0];
            }
            return null;
        }

        @Override
        protected void onPostExecute(GetQuote getQuote) {
            super.onPostExecute(getQuote);

            if(getQuote != null){
                submitGetQuoteRequest(getQuote);
            }else {
                Toast.makeText(GetQuoteActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GetQuoteActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(GetQuoteActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();

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


    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Get Quote");
        imageView.setOnClickListener(v -> onBackPressed());
    }
}