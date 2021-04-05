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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.User;
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;
import com.vaaq.fixmyphone.utils.utils;

import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.USER;

public class UserSignupActivity extends AppCompatActivity {

    private static String TAG = "UserSignupActivity";


    EditText editTextName;
    EditText editTextPhone;
    EditText editTextAddress;
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
        setContentView(R.layout.activity_user_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();


        initViews();
        dialogHelper = new DialogHelper(UserSignupActivity.this);
        networkHelper = new NetworkHelper(UserSignupActivity.this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (name.length() < 3) {
                    Toast.makeText(UserSignupActivity.this, "Name should be at least 3 character long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phone.length() < 11) {
                    Toast.makeText(UserSignupActivity.this, "Phone# should have 11 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (address.isEmpty()) {
                    Toast.makeText(UserSignupActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    Toast.makeText(UserSignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(UserSignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!utils.validateEmail(email)) {
                    Toast.makeText(UserSignupActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 5) {
                    Toast.makeText(UserSignupActivity.this, "Password should contain at least 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(name, phone, address, email, password);
                new SignupTask().execute(user);
            }
        });

    }

    void signup(User user) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Signup Canceled");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(UserSignupActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();

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
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(UserSignupActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG, "Signup Success");

                        // Sets the display name
                        FirebaseUser firebaseUser = authResult.getUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getName()).build();
                        firebaseUser.updateProfile(profileUpdates);

                        String userId = authResult.getUser().getUid();
                        createDatabaseEntry(userId, user);
                    }
                });

    }

    void createDatabaseEntry(String uid, User user) {

        FirebaseDatabase.getInstance().getReference().child(USER).child(uid).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        Toast.makeText(UserSignupActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");

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

    void initViews() {

        editTextName = findViewById(R.id.editTextSignupUserName);
        editTextPhone = findViewById(R.id.editTextSignupUserPhone);
        editTextAddress = findViewById(R.id.editTextSignupUserAddress);
        editTextEmail = findViewById(R.id.editTextSignupUserEmail);
        editTextPassword = findViewById(R.id.editTextSignupUserPassword);
        buttonSignup = findViewById(R.id.buttonSignupUser);
    }

    class SignupTask extends AsyncTask<User, Void, User> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogHelper.showProgressDialog("Processing Request");
        }

        @Override
        protected User doInBackground(User... users) {

            if (networkHelper.isConnected()) {
                return users[0];
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if (user != null) {
                signup(user);
            } else {
                Toast.makeText(UserSignupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
            }
        }
    }
}