package com.vaaq.fixmyphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.vaaq.fixmyphone.UserActivities.ActiveOrderUserFragment;
import com.vaaq.fixmyphone.UserActivities.CompletedOrderUserFragment;
import com.vaaq.fixmyphone.UserActivities.DashboardActivity;
import com.vaaq.fixmyphone.UserActivities.LoginUserFragment;
import com.vaaq.fixmyphone.UserActivities.UserLoginActivity;
import com.vaaq.fixmyphone.VendorActivities.LoginVendorFragment;
import com.vaaq.fixmyphone.VendorActivities.VendorLoginActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button buttonUser;
    Button buttonVendor;
    ViewPager viewPager;
    TabLayout tabLayout;
    FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        initViews();

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(fragmentAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
            }
        });

        buttonVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VendorLoginActivity.class));
            }
        });
    }

    void initViews(){
        tabLayout = findViewById(R.id.tabLayoutMain);
        viewPager = findViewById(R.id.viewPagerMain);
        buttonUser = findViewById(R.id.buttonUser);
        buttonVendor = findViewById(R.id.buttonVendor);
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
                    return new LoginUserFragment();
                case 1:
                    return new LoginVendorFragment();
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
                    return "User";
                case 1:
                    return "Vendor";
                default:
                    return "";
            }
        }
    }
}