package com.vaaq.fixmyphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private FirebaseFunctions mFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mFunctions = FirebaseFunctions.getInstance();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        },2000);

//        stripePayment(345)
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Exception e = task.getException();
//                            if (e instanceof FirebaseFunctionsException) {
//                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//                                FirebaseFunctionsException.Code code = ffe.getCode();
//                                Object details = ffe.getDetails();
//                            }
//                        }
//                        else {
//                            Log.i("THOO server", task.getResult());
//                        }
//                    }
//                });

//        helloWorld("this is client msg")
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Exception e = task.getException();
//                            if (e instanceof FirebaseFunctionsException) {
//                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//                                FirebaseFunctionsException.Code code = ffe.getCode();
//                                Object details = ffe.getDetails();
//                            }
//                        }
//                        else {
//                            Log.i("THOO server", task.getResult());
//                            String response = task.getResult();
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });

    }

    private Task<String> helloWorld(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", "true11");

        return mFunctions
                .getHttpsCallable("helloWorld")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        Log.i("THOO", result);
                        return result;
                    }
                });
    }

    private Task<String> stripePayment(long amount) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", amount);

        return mFunctions
                .getHttpsCallable("stripePayment")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        Log.i("THOO", result);
                        return result;
                    }
                });
    }

}