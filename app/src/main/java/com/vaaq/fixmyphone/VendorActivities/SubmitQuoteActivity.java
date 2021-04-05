package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.GetQuote;
import com.vaaq.fixmyphone.models.Quote;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.Date;
import java.util.Objects;

import static com.vaaq.fixmyphone.VendorActivities.DashboardVendorActivity.SHOP_NAME;
import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;
import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE_RESPONSES;

public class SubmitQuoteActivity extends AppCompatActivity {

    private static final String TAG = "TAKA submit quote";
    TextView textViewName;
    TextView textViewBrand;
    TextView textViewModel;
    TextView textViewDescription;
    EditText editTextDescription;
    EditText editTextQuote;
    Button buttonSubmit;
    GetQuote getQuote;

    DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_quote);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        initViews();

        dialogHelper = new DialogHelper(SubmitQuoteActivity.this);

        Intent intent = getIntent();
        getQuote = (GetQuote) intent.getSerializableExtra("quoteRequest");

        if (getQuote == null) {
            Toast.makeText(this, "Some Error occurred", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewName.setText(getQuote.getName());
        textViewBrand.setText(getQuote.getBrand());
        textViewModel.setText(getQuote.getModel());
        textViewDescription.setText(getQuote.getDescription());

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = editTextDescription.getText().toString().trim();
                String quote = editTextQuote.getText().toString().trim();


                if (description.length() == 0) {
                    Toast.makeText(SubmitQuoteActivity.this, "Kindly write description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quote.length() == 0) {
                    Toast.makeText(SubmitQuoteActivity.this, "Kindly write quote", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogHelper.showProgressDialog("Submitting response");
                submitQuote(description, quote);

            }
        });

    }

    private void submitQuote(String description, String quote) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Quote myQuote = new Quote(SHOP_NAME, description, quote, uid, new Date().getTime());

        FirebaseDatabase.getInstance().getReference()
                .child(GET_QUOTE)
                .child(getQuote.getUid())
                .child(getQuote.getUserRequestId())
                .child(GET_QUOTE_RESPONSES)
                .child(uid)
                .setValue(myQuote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Submit Quote Success");
                        Toast.makeText(SubmitQuoteActivity.this, "Response submitted", Toast.LENGTH_SHORT).show();
                        finish();
                        dialogHelper.hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Submit Quote Failure");
                        Toast.makeText(SubmitQuoteActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                        dialogHelper.hideProgressDialog();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "Submit Quote Complete");
                        dialogHelper.hideProgressDialog();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Submit Quote Canceled");
                        Toast.makeText(SubmitQuoteActivity.this, "Operation canceled", Toast.LENGTH_SHORT).show();
                        dialogHelper.hideProgressDialog();
                    }
                });
    }

    void initViews() {
        textViewName = findViewById(R.id.textViewSubmitQuoteUsername);
        textViewBrand = findViewById(R.id.textViewSubmitQuoteBrand);
        textViewModel = findViewById(R.id.textViewSubmitQuoteModel);
        textViewDescription = findViewById(R.id.textViewSubmitQuoteDescription);
        editTextDescription = findViewById(R.id.editTextSubmitQuoteDescription);
        editTextQuote = findViewById(R.id.editTextSubmitQuoteQuote);
        buttonSubmit = findViewById(R.id.buttonSubmitQuoteSubmit);
    }


    void headerSetup(){
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Submit Quote");
        imageView.setOnClickListener(v -> onBackPressed());
    }
}