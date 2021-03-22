package com.vaaq.fixmyphone.VendorActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vaaq.fixmyphone.MainActivity;
import com.vaaq.fixmyphone.R;
import com.vaaq.fixmyphone.Services.MyFirebaseMessagingService;
import com.vaaq.fixmyphone.UserActivities.ActiveOrderUserFragment;
import com.vaaq.fixmyphone.UserActivities.CompletedOrderActivity;
import com.vaaq.fixmyphone.UserActivities.DashboardActivity;
import com.vaaq.fixmyphone.UserActivities.GetQuoteFragment;
import com.vaaq.fixmyphone.UserActivities.QuotesActivity;
import com.vaaq.fixmyphone.UserActivities.ResponsesActivity;
import com.vaaq.fixmyphone.UserActivities.VendorProfileActivity;

import java.util.HashMap;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.VENDOR;

public class DashboardVendorActivity extends AppCompatActivity {

    public static String SHOP_NAME = "ABC";

    private DrawerLayout drawer;
    private NavigationView nv;

    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentAdapter fragmentAdapter;

    int backPressCount = 0;

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_vendor);
        Objects.requireNonNull(getSupportActionBar()).hide();

        headerSetup();

        drawer = findViewById(R.id.dashboard_user);
        tabLayout = findViewById(R.id.tabLayout2);
        viewPager = findViewById(R.id.viewPager);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(fragmentAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);


        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.actionCompletedOrder:
                        startActivity(new Intent(getApplicationContext(), CompletedOrderVendorActivity.class));
                        return true;
                    case R.id.actionLogout:
//                        Toast.makeText(DashboardVendorActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        drawer.closeDrawer(GravityCompat.START);
                        Intent loginscreen = new Intent(DashboardVendorActivity.this, MainActivity.class);
                        finish();
                        loginscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginscreen);
                        return true;
                    default:
                        return true;
                }
            }
        });


        fetchVendorDetails();
        fetchFCMToken();
    }


    void fetchVendorDetails() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(VENDOR)
                .child(firebaseUser.getUid());


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                SHOP_NAME = hashMap.get("shopName");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class FragmentAdapter extends FragmentStatePagerAdapter {

        int pageCount;

        public FragmentAdapter(FragmentManager fm, int pageCount) {
            super(fm);
            this.pageCount = pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ActiveOrderVendorFragment();
                case 1:
                    return new AskQuoteRequestFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Active Orders";
                case 1:
                    return "Ask Quote Requests";
                default:
                    return "";
            }
        }
    }

    void headerSetup() {
        TextView textView = findViewById(R.id.textViewHeaderTitle);
        ImageView imageView = findViewById(R.id.imageViewBack);
        imageView.setImageResource(R.drawable.ic_menu);
        textView.setText("Dashboard");
        imageView.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                // close
                drawer.closeDrawers();
            } else {
                // open
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    public void fetchFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAGG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        MyFirebaseMessagingService.updateToken(token);

                        // Log and toast
                        Log.d("TAGG", token);
//                        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {

        if(backPressCount == 0){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressCount++;
        }else {
            super.onBackPressed();
        }
    }
}