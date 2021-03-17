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
import com.vaaq.fixmyphone.utils.DialogHelper;
import com.vaaq.fixmyphone.utils.NetworkHelper;
import com.vaaq.fixmyphone.utils.utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginUserFragment newInstance(String param1, String param2) {
        LoginUserFragment fragment = new LoginUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static String TAG = "TAKA UserLoginActivity";

    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewSignup;
    Button buttonLogin;

    DialogHelper dialogHelper;
    NetworkHelper networkHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_user, container, false);

        initView(view);

        dialogHelper = new DialogHelper(getContext());
        networkHelper = new NetworkHelper(getContext());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!utils.validateEmail(email)){
                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                new LoginTask().execute();
            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserSignupActivity.class));
            }
        });
        
        
        
        
        return view;
    }

    private void login(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.i(TAG, "Login Canceled");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                        else if(e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                            Toast.makeText(getContext(), "No user exist with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        dialogHelper.hideProgressDialog();

                        FirebaseUser user = authResult.getUser();

                        if (user.getDisplayName() != null) {
                            Log.i(TAG, "Login Success");
                            startActivity(new Intent(getContext(), DashboardActivity.class));
                        }
                    }
                });
    }

    void initView(View view) {
        editTextEmail    = view.findViewById(R.id.editTextUserEmail);
        editTextPassword = view.findViewById(R.id.editTextUserPassword);
        textViewSignup   = view.findViewById(R.id.textViewUserSignup);
        buttonLogin      = view.findViewById(R.id.buttonUserLogin);
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
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
            }
        }
    }
}