package com.vaaq.fixmyphone.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.vaaq.fixmyphone.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.vaaq.fixmyphone.utils.Constant.USER;

public class DashboardActivity extends AppCompatActivity {

    public static String address;
    public static String userName;

    private DrawerLayout drawer;
    private NavigationView nv;

    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentAdapter fragmentAdapter;
    private FirebaseFunctions mFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Objects.requireNonNull(getSupportActionBar()).hide();
        headerSetup();
        mFunctions = FirebaseFunctions.getInstance();

        initViews();
        drawer = findViewById(R.id.dashboard_user);
        tabLayout = findViewById(R.id.tabLayout2);
        viewPager = findViewById(R.id.viewPager);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(fragmentAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);



//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView) findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.actionCompletedOrder:
                        startActivity(new Intent(getApplicationContext(), CompletedOrderActivity.class));
                        return true;
                    case R.id.actionQuotes:
                        startActivity(new Intent(getApplicationContext(), QuotesActivity.class));
                        return true;
                    case R.id.actionLogout:
                        Toast.makeText(DashboardActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    default:
                        return true;
                }
            }
        });


        fetchUserDetails();
    }


    void initViews() {

    }

    void fetchUserDetails() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(USER)
                .child(firebaseUser.getUid());


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                address = hashMap.get("address");
                userName = hashMap.get("name");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
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
                    return new ActiveOrderUserFragment();
                case 1:
                    return new GetQuoteFragment();
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
                    return "Get Quote";
                default:
                    return "";
            }
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