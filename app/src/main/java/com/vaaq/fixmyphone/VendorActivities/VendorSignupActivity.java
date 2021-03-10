package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.vaaq.fixmyphone.models.Vendor;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;
import com.vaaq.fixmyphone.utils.utils;

import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class VendorSignupActivity extends AppCompatActivity {

    private static String TAG = "VendorSignupActivity";


    EditText editTextName;
    EditText editTextPhone;
    EditText editTextShopName;
    EditText editTextShopAddress;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSignup;

    DialogHelper dialogHelper;
    NetworkHelper networkHelper;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_signup);

        initViews();
        dialogHelper = new DialogHelper(VendorSignupActivity.this);
        networkHelper = new NetworkHelper(VendorSignupActivity.this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String shopName = editTextShopName.getText().toString().trim();
                String shopAddress = editTextShopAddress.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (name.length() < 3) {
                    Toast.makeText(VendorSignupActivity.this, "Name should be at least 3 character long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() < 11) {
                    Toast.makeText(VendorSignupActivity.this, "Phone# should have 11 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (shopName.isEmpty()) {
                    Toast.makeText(VendorSignupActivity.this, "Please enter shop name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (shopAddress.isEmpty()) {
                    Toast.makeText(VendorSignupActivity.this, "Please enter shop address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    Toast.makeText(VendorSignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(VendorSignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!utils.validateEmail(email)) {
                    Toast.makeText(VendorSignupActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 5) {
                    Toast.makeText(VendorSignupActivity.this, "Password should contain at least 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                Vendor vendor = new Vendor(name, phone, shopName, shopAddress, email, password);
                new SignupTask().execute(vendor);

            }
        });
    }

    void initViews() {

        editTextName = findViewById(R.id.editTextSignupVendorName);
        editTextPhone = findViewById(R.id.editTextSignupVendorPhone);
        editTextShopName = findViewById(R.id.editTextSignupVendorShopName);
        editTextShopAddress = findViewById(R.id.editTextSignupVendorShopAddress);
        editTextEmail = findViewById(R.id.editTextSignupVendorEmail);
        editTextPassword = findViewById(R.id.editTextSignupVendorPassword);
        buttonSignup = findViewById(R.id.buttonSignupVendor);
    }

    void signup(Vendor vendor) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup Canceled");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "Signup Completed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup Failure");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG, "Signup Success");

                        String userId = authResult.getUser().getUid();
                        createDatabaseEntry(userId, vendor);
                    }
                });

    }

    void createDatabaseEntry(String uid, Vendor vendor) {

        FirebaseDatabase.getInstance().getReference().child(VENDOR).child(uid).setValue(vendor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        finish();
                        startActivity(new Intent(getApplicationContext(), DashboardVendorActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();


                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "Signup DB Complete");
                        dialogHelper.hideProgressDialog();

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup DB Canceled");
                        dialogHelper.hideProgressDialog();


                    }
                });
    }

    class SignupTask extends AsyncTask<Vendor, Void, Vendor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogHelper.showProgressDialog("Processing Request");
        }

        @Override
        protected Vendor doInBackground(Vendor... vendor) {

            if (networkHelper.isConnected()) {
                return vendor[0];
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Vendor vendor) {
            super.onPostExecute(vendor);

            if (vendor != null) {
                signup(vendor);
            } else {
                Toast.makeText(VendorSignupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
            }
        }
    }

}