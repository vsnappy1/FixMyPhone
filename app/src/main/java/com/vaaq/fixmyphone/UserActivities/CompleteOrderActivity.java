package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.models.RateAndReview;
import com.vaaq.fixmyphone.utils.Constant;
import com.vaaq.fixmyphone.utils.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER_IDS;
import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.COMPLETED_ORDER_IDS;
import static com.vaaq.fixmyphone.utils.Constant.PAYMENT_STATUS_PAID;
import static com.vaaq.fixmyphone.utils.Constant.PAYMENT_STATUS_PENDING;
import static com.vaaq.fixmyphone.utils.Constant.RAR_STATUS_RATED;
import static com.vaaq.fixmyphone.utils.Constant.REVIEW;
import static com.vaaq.fixmyphone.utils.Constant.USER;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class CompleteOrderActivity extends AppCompatActivity {


    private static final String TAG = "YOYO";
    private FirebaseFunctions mFunctions;


    Button buttonMakePayment;
    Button buttonRateReviewVendor;

    ActiveOrder activeOrder;

    DialogHelper dialogHelper;

    Dialog reviewDialog;
    String review = "";
    int starCount = 0;

    ImageView imageViewStar1;
    ImageView imageViewStar2;
    ImageView imageViewStar3;
    ImageView imageViewStar4;
    ImageView imageViewStar5;

    int filledStar, unfilledStar;

    boolean isAddedInUser = false;
    boolean isRemovedFromUser = false;
    boolean isAddedInVendor = false;
    boolean isRemovedFromVendor = false;

    private String paymentIntentClientSecret;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        mFunctions = FirebaseFunctions.getInstance();

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51IVMDOACLamgLPNi3q8dOHhoCJx6okZXU7VSs1XfRq0jFGmE7xSc9SCbxGPNFzJizE0dMjyQbzjZqxAnedVkcXiO00k2Q0A8nC"
        );
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();
        paymentIntentClientSecret = "sk_test_51IVMDOACLamgLPNiyUdkvluIRINDTcZ6MLLwICcmRgWM3jQQ4QIBcGVS6F2uUPdfgBKHajypHC0D4wfAtwyzdzdK00Q51L8cD8";

        filledStar = R.drawable.ic_star;
        unfilledStar = R.drawable.ic_star_border;

        dialogHelper = new DialogHelper(this);
        initViews();

        Intent intent = getIntent();
        activeOrder = (ActiveOrder) intent.getSerializableExtra("activeOrder");


        buttonMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change payment status
                if (activeOrder.getPaymentStatus().equals(PAYMENT_STATUS_PENDING)) {
                    showPaymentDialog();
                }
                else {
                    Toast.makeText(CompleteOrderActivity.this, "You have already made the payment", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonRateReviewVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activeOrder.getPaymentStatus().equals(PAYMENT_STATUS_PAID)) {
                    showRateAndReviewVendorDialog();
                }else {
                    Toast.makeText(CompleteOrderActivity.this, "Kindly make Payment first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initViews() {
        buttonMakePayment = findViewById(R.id.buttonMakePayment);
        buttonRateReviewVendor = findViewById(R.id.buttonRateAndReviewVendor);
    }

    void makePayment() {


        FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId()).child("paymentStatus").setValue(Constant.PAYMENT_STATUS_PAID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        activeOrder.setPaymentStatus(PAYMENT_STATUS_PAID);
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

    void updateRateReviewStatusInOrder() {

        FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId()).child("rateAndReviewStatus").setValue(Constant.RAR_STATUS_RATED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        dialogHelper.showProgressDialog("Adding review");
                        activeOrder.setRateAndReviewStatus(RAR_STATUS_RATED);
                        addReview();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();


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

    void addReview() {
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        RateAndReview rateAndReview = new RateAndReview(userName, review, starCount, new Date().getTime());

        FirebaseDatabase.getInstance().getReference().child(REVIEW).child(activeOrder.getVendorId()).push().setValue(rateAndReview)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        dialogHelper.showProgressDialog("Marking the order as complete");
                        moveFromActiveOrderToCompleteOrder();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(CompleteOrderActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();


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

    void moveFromActiveOrderToCompleteOrder() {
        final DatabaseReference from = FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId());
        final DatabaseReference to = FirebaseDatabase.getInstance().getReference().child(COMPLETED_ORDER).child(activeOrder.getOrderId());

        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                to.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            from.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deleteActiveOrderIdAndAddCompletedOrderId();
                                }
                            });
                        } else {
                            Toast.makeText(CompleteOrderActivity.this, "Network Issue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void deleteActiveOrderIdAndAddCompletedOrderId() {

        final DatabaseReference user = FirebaseDatabase.getInstance().getReference().child(USER);
        final DatabaseReference vendor = FirebaseDatabase.getInstance().getReference().child(VENDOR);

        // Add completed order ids
        user.child(activeOrder.getUserId()).child(COMPLETED_ORDER_IDS).push().setValue(activeOrder.getOrderId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isAddedInUser = true;
                checkAllOperationsDone();
            }
        });
        vendor.child(activeOrder.getVendorId()).child(COMPLETED_ORDER_IDS).push().setValue(activeOrder.getOrderId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isAddedInVendor = true;
                checkAllOperationsDone();
            }
        });

        //Delete active order ids
        user.child(activeOrder.getUserId()).child(ACTIVE_ORDER_IDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(ds.getValue()).toString().equals(activeOrder.getOrderId())) {
                        user.child(activeOrder.getUserId()).child(ACTIVE_ORDER_IDS).child(Objects.requireNonNull(ds.getKey())).removeValue();
                        isRemovedFromUser = true;
                        break;
                    }
                }
                checkAllOperationsDone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        vendor.child(activeOrder.getVendorId()).child(ACTIVE_ORDER_IDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(ds.getValue()).toString().equals(activeOrder.getOrderId())) {
                        vendor.child(activeOrder.getVendorId()).child(ACTIVE_ORDER_IDS).child(Objects.requireNonNull(ds.getKey())).removeValue();
                        isRemovedFromUser = true;
                        break;
                    }
                }
                checkAllOperationsDone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void checkAllOperationsDone() {

        if (isAddedInUser &&
                isAddedInVendor &&
                isRemovedFromUser &&
                isRemovedFromVendor) {

            dialogHelper.hideProgressDialog();
        }
    }


    void showPaymentDialog() {
        Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.layout_payment);
        paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button buttonPay = paymentDialog.findViewById(R.id.buttonPay);
        CardInputWidget cardInputWidget = paymentDialog.findViewById(R.id.cardInputWidget);
        TextView textViewAmount = paymentDialog.findViewById(R.id.textViewAmount);

        int amount = Integer.parseInt(activeOrder.getQuote());

        textViewAmount.setText("Rs. " + NumberFormat.getNumberInstance(Locale.US).format(amount));

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.showProgressDialog("Making payment");

                int amount = Integer.parseInt(activeOrder.getQuote());
                stripePayment(amount)
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                    }
                                }
                                else {
                                    Log.i("THOO server", task.getResult());
                                    try {
                                        JSONObject jsonObject = new JSONObject(task.getResult());
                                        paymentIntentClientSecret = jsonObject.getString("clientSecret");

                                        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                                        if (params != null) {
                                            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                                            final Context context = getApplicationContext();
                                            stripe = new Stripe(
                                                    context,
                                                    PaymentConfiguration.getInstance(context).getPublishableKey()
                                            );
                                            stripe.confirmPayment(CompleteOrderActivity.this, confirmParams);
                                            paymentDialog.dismiss();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(CompleteOrderActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                        dialogHelper.hideProgressDialog();
                                    }
                                }
                            }
                        });

            }
        });

        try {
            paymentDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void showRateAndReviewVendorDialog() {
        reviewDialog = new Dialog(this);
        reviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reviewDialog.setContentView(R.layout.layout_rate_and_review);
        reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageViewStar1 = reviewDialog.findViewById(R.id.imageViewStart1);
        imageViewStar2 = reviewDialog.findViewById(R.id.imageViewStart2);
        imageViewStar3 = reviewDialog.findViewById(R.id.imageViewStart3);
        imageViewStar4 = reviewDialog.findViewById(R.id.imageViewStart4);
        imageViewStar5 = reviewDialog.findViewById(R.id.imageViewStart5);
        EditText editTextReview = reviewDialog.findViewById(R.id.editTextShareExperience);
        Button buttonSubmitReview = reviewDialog.findViewById(R.id.buttonShareExperience);

        imageViewStar1.setOnClickListener(v -> {
            starCount = 1;
            updateRating();
        });
        imageViewStar2.setOnClickListener(v -> {
            starCount = 2;
            updateRating();
        });
        imageViewStar3.setOnClickListener(v -> {
            starCount = 3;
            updateRating();
        });
        imageViewStar4.setOnClickListener(v -> {
            starCount = 4;
            updateRating();
        });
        imageViewStar5.setOnClickListener(v -> {
            starCount = 5;
            updateRating();
        });

        buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (starCount == 0) {
                    Toast.makeText(CompleteOrderActivity.this, "Kindly select star rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                review = editTextReview.getText().toString().trim();
                if (review.length() == 0) {
                    Toast.makeText(CompleteOrderActivity.this, "Kindly write review", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogHelper.showProgressDialog("Updating... status");
                updateRateReviewStatusInOrder();
            }
        });

        try {
            reviewDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateRating() {
        if (starCount == 1) {
            imageViewStar1.setBackgroundResource(filledStar);
            imageViewStar2.setBackgroundResource(unfilledStar);
            imageViewStar3.setBackgroundResource(unfilledStar);
            imageViewStar4.setBackgroundResource(unfilledStar);
            imageViewStar5.setBackgroundResource(unfilledStar);
        } else if (starCount == 2) {
            imageViewStar1.setBackgroundResource(filledStar);
            imageViewStar2.setBackgroundResource(filledStar);
            imageViewStar3.setBackgroundResource(unfilledStar);
            imageViewStar4.setBackgroundResource(unfilledStar);
            imageViewStar5.setBackgroundResource(unfilledStar);
        } else if (starCount == 3) {
            imageViewStar1.setBackgroundResource(filledStar);
            imageViewStar2.setBackgroundResource(filledStar);
            imageViewStar3.setBackgroundResource(filledStar);
            imageViewStar4.setBackgroundResource(unfilledStar);
            imageViewStar5.setBackgroundResource(unfilledStar);
        } else if (starCount == 4) {
            imageViewStar1.setBackgroundResource(filledStar);
            imageViewStar2.setBackgroundResource(filledStar);
            imageViewStar3.setBackgroundResource(filledStar);
            imageViewStar4.setBackgroundResource(filledStar);
            imageViewStar5.setBackgroundResource(unfilledStar);
        } else if (starCount == 5) {
            imageViewStar1.setBackgroundResource(filledStar);
            imageViewStar2.setBackgroundResource(filledStar);
            imageViewStar3.setBackgroundResource(filledStar);
            imageViewStar4.setBackgroundResource(filledStar);
            imageViewStar5.setBackgroundResource(filledStar);
        }
    }


    void headerSetup() {
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Complete Order");
        imageView.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    // ...

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CompleteOrderActivity> activityRef;

        PaymentResultCallback(@NonNull CompleteOrderActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CompleteOrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Toast.makeText(activity, "Payment completed", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();
                makePayment();

//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent),
//                        true
//                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                Toast.makeText(activity, "Payment failed", Toast.LENGTH_SHORT).show();
                dialogHelper.hideProgressDialog();


//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
//                        false
//                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CompleteOrderActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            dialogHelper.hideProgressDialog();

//            Toast.makeText(activity, "Payment completed", Toast.LENGTH_SHORT).show();
//            dialogHelper.hideProgressDialog();
//            makePayment();

        }
    }

    private Task<String> stripePayment(int amount) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", amount*100);

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