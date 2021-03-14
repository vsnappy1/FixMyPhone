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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.UserActivities.UserLoginActivity;
import com.vaaq.fixmyphone.models.Vendor;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;

import java.util.Objects;

public class VendorLoginActivity extends AppCompatActivity {

    private static String TAG = "VendorLoginActivity";


    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewSignup;
    Button buttonLogin;

    DialogHelper dialogHelper;
    NetworkHelper networkHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initView();

        dialogHelper = new DialogHelper(VendorLoginActivity.this);
        networkHelper = new NetworkHelper(VendorLoginActivity.this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }


                new LoginTask().execute();
            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VendorLoginActivity.this, VendorSignupActivity.class));
            }
        });
    }

    private void login(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Login Canceled");
                        dialogHelper.hideProgressDialog();


                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "Login Completed");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Login Failure");

                        dialogHelper.hideProgressDialog();

                        if(e.getMessage().equals("The password is invalid or the user does not have a password.")){
                            Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                        else if(e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                            Toast.makeText(getApplicationContext(), "No user exist with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            if(firebaseUser.getDisplayName() == null){
                                Log.i(TAG, "Login Success");
                                finish();
                                startActivity(new Intent(getApplicationContext(), DashboardVendorActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Not registered as vendor", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialogHelper.hideProgressDialog();
                    }
                });

    }


    void initView(){
        editTextEmail = findViewById(R.id.editTextVendorEmail);
        editTextPassword = findViewById(R.id.editTextVendorPassword);
        textViewSignup = findViewById(R.id.textViewVendorSignup);
        buttonLogin = findViewById(R.id.buttonVendorLogin);
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogHelper.showProgressDialog("Logging in");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return networkHelper.isConnected();
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            super.onPostExecute(isConnected);

            if (isConnected) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                login(email, password);
            } else {
                Toast.makeText(VendorLoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
            }
        }
    }
}