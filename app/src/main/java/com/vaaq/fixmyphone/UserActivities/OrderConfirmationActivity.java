package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vaaq.fixmyphone.Adapters.QuoteAdapter;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.SplashActivity;
import com.vaaq.fixmyphone.VendorActivities.DashboardVendorActivity;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.models.Quote;
import com.vaaq.fixmyphone.utils.Constant;
import com.vaaq.fixmyphone.utils.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER_IDS;
import static com.vaaq.fixmyphone.utils.Constant.GET_QUOTE;
import static com.vaaq.fixmyphone.utils.Constant.USER;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class OrderConfirmationActivity extends AppCompatActivity {

    TextView textViewShopName;
    TextView textViewQuote;
    EditText editTextAddress;
    TextView textViewPickupDate;
    TextView textViewPickupTimeFrom;
    TextView textViewPickupTimeTo;
    Button buttonConfirm;

    Calendar cal = Calendar.getInstance();
    String uid;
    private String TAG = "Firebase";

    DialogHelper dialogHelper;

    int selectedHourFrom;
    int selectedMinuteFrom;

    int year;
    int month;
    int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();

        initViews();

        if (VENDOR.equals(getIntent().getStringExtra("from"))) {
            TextView textViewTitle1 = findViewById(R.id.textViewTitle1);
            TextView textViewTitle2 = findViewById(R.id.textViewTitle2);

            textViewTitle1.setVisibility(View.GONE);
            textViewTitle2.setVisibility(View.GONE);
            textViewShopName.setVisibility(View.GONE);
            textViewQuote.setVisibility(View.GONE);
        }

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        selectedHourFrom = 12;
        selectedMinuteFrom = 0;
        textViewPickupTimeFrom.setText("12:00 PM");
        textViewPickupTimeTo.setText("6:00 PM");


        dialogHelper = new DialogHelper(this);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH) + 1;

        if(day == 32){
            day = 1;
            month++;
            if(month == 13){
                month = 1;
                year++;
            }
        }

        textViewShopName.setText(VendorProfileActivity.shopName);
        textViewQuote.setText(ResponsesActivity.quote);
        editTextAddress.setText(DashboardActivity.address);
        textViewPickupDate.setText(day + "-" + (month + 1) + "-" + year);

        textViewPickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderConfirmationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String pickupDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year + " " + "23:59:59";
                                if (getEpochTime(pickupDate) > new Date().getTime()) {
                                    textViewPickupDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                } else {
                                    Toast.makeText(OrderConfirmationActivity.this, "Date should be greater than today", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        textViewPickupTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(OrderConfirmationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String AM_PM;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            if (selectedHour > 12) {
                                selectedHour -= 12;
                            }
                        }

                        if (selectedMinute / 10 == 0) {
                            textViewPickupTimeFrom.setText(selectedHour + ":0" + selectedMinute + " " + AM_PM);
                        } else {
                            textViewPickupTimeFrom.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                        }
                    }
                }, hour, minute, false);//Yes 12 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        textViewPickupTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(OrderConfirmationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        if (selectedHour < selectedHourFrom) {
                            Toast.makeText(OrderConfirmationActivity.this, "Pickup to time must be greater then from time", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (selectedHour == selectedHourFrom && selectedMinute < selectedMinuteFrom) {
                            Toast.makeText(OrderConfirmationActivity.this, "Pickup to time must be greater then from time", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String AM_PM;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            if (selectedHour > 12) {
                                selectedHour -= 12;
                            }
                        }

                        if (selectedMinute / 10 == 0) {
                            textViewPickupTimeTo.setText(selectedHour + ":0" + selectedMinute + " " + AM_PM);
                        } else {
                            textViewPickupTimeTo.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                        }
                    }
                }, hour, minute, false);//Yes 12 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogHelper.showProgressDialog("Creating pickup request...");
                new PickupRequestTask().execute(new JSONObject());


            }
        });
    }


    void createNewOrder() {

        ActiveOrder activeOrder = new ActiveOrder(
                null,
                Constant.ORDER_STATUS_ACTIVE,
                Constant.PAYMENT_STATUS_PENDING,
                Constant.RAR_STATUS_NOT_RATED,
                DashboardActivity.userName,
                VendorProfileActivity.vendorName,
                uid,
                ResponsesActivity.vendorId,
                QuotesActivity.brand,
                QuotesActivity.model,
                QuotesActivity.description,
                ResponsesActivity.shopName,
                ResponsesActivity.message,
                ResponsesActivity.quote,
                Calendar.getInstance().getTime().getTime()
        );

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER);

        String key = databaseReference.push().getKey();

        databaseReference.child(key).setValue(activeOrder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "new order created");
                        updateTheActiveProjectsUser(key);
                        updateTheActiveProjectsVendor(ResponsesActivity.vendorId, key);

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    void deleteGetQuoteRequest() {
        FirebaseDatabase.getInstance().getReference()
                .child(GET_QUOTE)
                .child(uid)
                .child(QuotesActivity.requestKey)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "deleted");
                        createNewOrder();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    void updateTheActiveProjectsUser(String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(USER).child(uid).child(ACTIVE_ORDER_IDS);
        databaseReference.push().setValue(key)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "added active order id in user");
                        if (dialogHelper.isDialogBoxShowing()) {
                            dialogHelper.hideProgressDialog();
                            startNewMainActivity(OrderConfirmationActivity.this, DashboardActivity.class);
                        }

                    }
                });
    }

    void updateTheActiveProjectsVendor(String vendorId, String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(VENDOR).child(vendorId).child(ACTIVE_ORDER_IDS);
        databaseReference.push().setValue(key).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "added active order id in vendor");
                if (dialogHelper.isDialogBoxShowing()) {
                    dialogHelper.hideProgressDialog();
                    startNewMainActivity(OrderConfirmationActivity.this, DashboardActivity.class);
                }
            }
        });
    }

    static void startNewMainActivity(Activity currentActivity, Class<? extends Activity> newTopActivityClass) {
        Intent intent = new Intent(currentActivity, newTopActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivity.startActivity(intent);
    }

    void initViews() {
        textViewShopName = findViewById(R.id.textViewOrderConfirmationShopName);
        textViewQuote = findViewById(R.id.textViewOrderConfirmationQuote);
        editTextAddress = findViewById(R.id.editTextOrderConfirmationAddress);
        textViewPickupDate = findViewById(R.id.textViewOrderConfirmationPickupDate);
        textViewPickupTimeFrom = findViewById(R.id.textViewOrderConfirmationPickupTimeFrom);
        textViewPickupTimeTo = findViewById(R.id.textViewOrderConfirmationPickupTimeTo);
        buttonConfirm = findViewById(R.id.buttonOrderConfirmationConfirm);
    }

    long getEpochTime(String str) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long epoch = 0;
        if (date != null) {
            epoch = date.getTime();
            System.out.println(epoch);
        }
        return epoch;
    }


    void headerSetup() {
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);

        textView.setText("Order Confirmation");
        imageView.setOnClickListener(v -> onBackPressed());
    }


    class PickupRequestTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... items) {

            JSONObject jsonObject = items[0];
            String pickupDate = null;
            String pickupFromTime = null;
            String pickupToTime = null;
            String customerName = null;
            String customerAddress = null;
            String customerPhone = null;

            try {
                pickupDate = jsonObject.getString("pickupDate");
                pickupFromTime = jsonObject.getString("pickupFromTime");
                pickupToTime = jsonObject.getString("pickupToTime");
                customerName = jsonObject.getString("customerName");
                customerAddress = jsonObject.getString("customerAddress");
                customerPhone = jsonObject.getString("customerPhone");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType,
//                    "{\"pickupDate\":\"20180101\"," +
//                            "\"pickupFromTime\":\"09:00:00\"," +
//                            "\"pickupToTime\":\"15:00:00\"," +
//                            "\"customerNo\":\"0\"," +
//                            "\"customerName\":\"Name\"," +
//                            "\"customerAddress\":\"885 Timaz Point\"," +
//                            "\"customerPhone\":\"0\"," +
//                            "\"station\":\"X\"," +
//                            "\"area\":\"X\"," +
//                            "\"careOf\":\"0\"," +
//                            "\"expectedWeight\":\"200g\"," +
//                            "\"userName\":\"Name\"," +
//                            "\"pieces\":\"1\"}");

            RequestBody body = RequestBody.create(mediaType,
                    "{\"pickupDate\":\"" + pickupDate + "\"," +
                            "\"pickupFromTime\":\"" + pickupFromTime + "\"," +
                            "\"pickupToTime\":\"" + pickupToTime + "\"," +
                            "\"customerNo\":\"0\"," +
                            "\"customerName\":\"" + customerName + "\"," +
                            "\"customerAddress\":\"" + customerAddress + "\"," +
                            "\"customerPhone\":\"" + customerPhone + "\"," +
                            "\"station\":\"X\"," +
                            "\"area\":\"X\"," +
                            "\"careOf\":\"0\"," +
                            "\"expectedWeight\":\"200g\"," +
                            "\"userName\":\"" + customerName + "\"," +
                            "\"pieces\":\"1\"}");

            Request request = new Request.Builder()
                    .url("https://apis.tcscourier.com/production/v1/pms/pickup")
                    .post(body)
                    .addHeader("X-IBM-Client-Id", "cd989018-8037-43b1-b0f0-3aa87b10273e")
                    .addHeader("content-type", "application/json")
                    .addHeader("accept", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.message();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialogHelper.hideProgressDialog();

            Toast.makeText(OrderConfirmationActivity.this, "Done", Toast.LENGTH_SHORT).show();
            String from = getIntent().getStringExtra("from");
            if (VENDOR.equals(from)) {
                Intent intent = new Intent(getApplicationContext(), DashboardVendorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                dialogHelper.showProgressDialog("Creating an order...");
                deleteGetQuoteRequest();
            }
//            if(s.equals("Unauthorized")){
//
//            }
        }
    }


}