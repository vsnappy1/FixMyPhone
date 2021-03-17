package com.vaaq.fixmyphone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vaaq.fixmyphone.Adapters.ChatAdapter;
import com.vaaq.fixmyphone.UserActivities.ActiveOrderActivity;
import com.vaaq.fixmyphone.UserActivities.CompleteOrderActivity;
import com.vaaq.fixmyphone.UserActivities.OrderConfirmationActivity;
import com.vaaq.fixmyphone.VendorActivities.DashboardVendorActivity;
import com.vaaq.fixmyphone.models.ActiveOrder;
import com.vaaq.fixmyphone.models.Message;
import com.vaaq.fixmyphone.utils.Constant;
import com.vaaq.fixmyphone.utils.DialogHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.ACTIVE_ORDER;
import static com.vaaq.fixmyphone.utils.Constant.CONVERSATION;
import static com.vaaq.fixmyphone.utils.Constant.ORDER_STATUS_ACTIVE;
import static com.vaaq.fixmyphone.utils.Constant.ORDER_STATUS_COMPLETE;
import static com.vaaq.fixmyphone.utils.Constant.PAYMENT_STATUS_PAID;
import static com.vaaq.fixmyphone.utils.Constant.PAYMENT_STATUS_PENDING;
import static com.vaaq.fixmyphone.utils.Constant.RAR_STATUS_NOT_RATED;
import static com.vaaq.fixmyphone.utils.Constant.RAR_STATUS_RATED;
import static com.vaaq.fixmyphone.utils.Constant.USER;
import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "YOYO";
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE = 97;

    // Path Of selected image
    private Uri filePath = null;

    ImageView buttonGallery;
    ImageView buttonSend;
    ProgressBar progressBar;
    EditText editTextMessage;
    RecyclerView recyclerView;
    TextView textViewHeaderTitle;

    String senderName, senderId;

    DatabaseReference mDatabase;
    ValueEventListener valueEventListener;
    StorageReference storageReference;

    String orderId;
    ActiveOrder activeOrder;

    ArrayList<Message> list;
    LinearLayoutManager layoutManager;
    ChatAdapter adapter;

    DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();

        dialogHelper = new DialogHelper(this);

        Intent intent = getIntent();
        activeOrder = (ActiveOrder)intent.getSerializableExtra("activeOrder");

        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        ImageView imageViewDone = findViewById(R.id.imageViewDone);
        imageViewBack.setOnClickListener(v -> onBackPressed());

        intiViews();
        setupHeaderTitle();

        if(intent.getStringExtra("from").equals(VENDOR)){
            imageViewDone.setVisibility(View.GONE);
            if(activeOrder.getPaymentStatus().equals(PAYMENT_STATUS_PAID)){
                imageViewDone.setVisibility(View.VISIBLE);
                imageViewDone.setImageResource(R.drawable.ic_delivery_dining);
                imageViewDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(ChatActivity.this, OrderConfirmationActivity.class);
                        intent1.putExtra("from", VENDOR);
                        startActivity(intent1);
                    }
                });
            }

        }
        else {
            imageViewDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(activeOrder.getOrderStatus().equals(ORDER_STATUS_ACTIVE)){
                        new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("Completed Order")
                                .setMessage("Are you sure you want to mark this order as complete?")

                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        MarkOrderAsCompleted();
                                    }
                                })

                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    }
                    else {
                        Intent intent = new Intent(ChatActivity.this, CompleteOrderActivity.class);
                        intent.putExtra("activeOrder", activeOrder);
                        startActivity(intent);
                    }


                }
            });
        }
        orderId = activeOrder.getOrderId();



        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        senderId = firebaseUser.getUid();
        senderName = "";

        storageReference = FirebaseStorage.getInstance().getReference().child("Conversation").child(orderId);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CONVERSATION).child(orderId).child("Message");


        list = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);

        adapter = new ChatAdapter(this, list, senderId);
        adapter.setOnItemLongClickListener(new ChatAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }

            @Override
            public void onLongClick(int position, View v) {

            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initiateMessageListener();

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    filePath = null;
                    selectImage();
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editTextMessage.length() > 0) {
                    sendMessage();
                } else {
                    Toast.makeText(ChatActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void setupHeaderTitle(){
        if(activeOrder.getOrderStatus().equals(ORDER_STATUS_ACTIVE)){
            textViewHeaderTitle.setText("Order status: Active");
        }
        else if(activeOrder.getOrderStatus().equals(ORDER_STATUS_COMPLETE)){
            textViewHeaderTitle.setText("Order status: Completed");
            if(activeOrder.getPaymentStatus().equals(PAYMENT_STATUS_PENDING)){
                textViewHeaderTitle.setText("Payment status: Pending");
            }
            else if(activeOrder.getPaymentStatus().equals(PAYMENT_STATUS_PAID)){
                textViewHeaderTitle.setText("Payment status: Paid");
                if(activeOrder.getRateAndReviewStatus().equals(RAR_STATUS_NOT_RATED)){
                    textViewHeaderTitle.setText("Rating status: Not rated yet");
                }
                else if(activeOrder.getRateAndReviewStatus().equals(RAR_STATUS_RATED)){
                    textViewHeaderTitle.setText("Rating status: Rated");
                }
            }
        }
    }
    private void intiViews() {
        buttonGallery = findViewById(R.id.buttonChatPickImage);
        buttonSend = findViewById(R.id.buttonSend);
        progressBar = findViewById(R.id.progressBarChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        recyclerView = findViewById(R.id.recyclerViewChat);
        textViewHeaderTitle = findViewById(R.id.textViewHeaderTitle);
    }


    void MarkOrderAsCompleted(){

        dialogHelper.showProgressDialog("Marking order as completed");

        FirebaseDatabase.getInstance().getReference().child(ACTIVE_ORDER).child(activeOrder.getOrderId()).child("orderStatus").setValue(Constant.ORDER_STATUS_COMPLETE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Signup DB Success");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(ChatActivity.this, "Order marked completed", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(ChatActivity.this, CompleteOrderActivity.class);
                        intent.putExtra("activeOrder", activeOrder);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signup DB Failure");
                        dialogHelper.hideProgressDialog();
                        Toast.makeText(ChatActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();


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

    void initiateMessageListener() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                if (dataSnapshot.getValue() == null) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, Object> msg = (HashMap<String, Object>) ds.getValue();

                    String senderName = msg.get("senderName").toString();
                    String senderId = msg.get("senderId").toString();
                    String message = null;
                    String url = null;

                    if (msg.containsKey("message")) {
                        message = msg.get("message").toString();
                    } else if (msg.containsKey("url")) {
                        url = msg.get("url").toString();
                    }
                    long time = (long) msg.get("time");
                    boolean isImage = (boolean) msg.get("image");

                    Message m = new Message(senderName, senderId, message, url, time, isImage);
                    list.add(m);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                layoutManager.scrollToPosition(list.size() - 1);

//                try {
//                    list.clear();
//                    Log.i("TAKA", dataSnapshot.toString());
//                    HashMap<String, Object> messages = (HashMap<String, Object>) dataSnapshot.getValue();
//                    ArrayList<String> msgKeys = new ArrayList<>(messages.keySet());
//                    for(String key: msgKeys){
//                        HashMap<String,Object> msg = (HashMap<String, Object>) messages.get(key);
//
//                        String  senderName = msg.get("senderName").toString();
//                        String  senderId = msg.get("senderId").toString();
//                        String  message = null;
//                        String  url = null;
//
//                        if(msg.containsKey("message")){
//                            message = msg.get("message").toString();
//                        }else if(msg.containsKey("url")){
//                            url = msg.get("url").toString();
//                        }
//                        long    time = (long) msg.get("time");
//                        boolean isImage = (boolean) msg.get("image");
//
//                        Message m = new Message(senderName, senderId, message, url, time, isImage);
//                        list.add(m);
//
//                    }
//
//                    adapter.notifyDataSetChanged();
//                    progressBar.setVisibility(View.GONE);
//                    layoutManager.scrollToPosition(list.size() - 1);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(valueEventListener);
    }

    private void sendMessage() {

        String messageString = editTextMessage.getText().toString();
        long time = Calendar.getInstance().getTime().getTime();

        Message message = new Message(senderName, senderId, messageString, null, time, false);

        DatabaseReference messageRef = mDatabase;
        messageRef.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    editTextMessage.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Could not send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendImageToTheServer() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Progress...");
            progressDialog.show();

            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    sendImage(url);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void sendImage(String url) {
        long time = Calendar.getInstance().getTime().getTime();

        Message message = new Message(senderName, senderId, null, url, time, true);

        DatabaseReference messageRef = mDatabase;
        messageRef.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    editTextMessage.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Could not send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    void selectImage() {
        Intent imagePicker = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(imagePicker, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            filePath = data.getData();
            sendImageToTheServer();

            Cursor cursor = null;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
            }
            if (cursor != null) {
                cursor.moveToFirst();
                cursor.close();
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            selectImage();
            Log.d("TAG", "GRANTED");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(valueEventListener);
    }
}