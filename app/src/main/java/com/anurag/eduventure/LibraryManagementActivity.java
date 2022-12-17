package com.anurag.eduventure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.anurag.eduventure.Adapters.ViewPagerAdapter;
import com.anurag.eduventure.Adapters.ViewPagerAdapterManagement;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class LibraryManagementActivity extends AppCompatActivity {

    PagerAdapter pagerAdapter;
    TabItem mHome, mIssued;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_managenent);

        mHome = findViewById(R.id.home);
        mIssued = findViewById(R.id.issued);
        tabLayout = findViewById(R.id.include);

        ViewPager viewPager = findViewById(R.id.fragmentContainer);

        pagerAdapter = new ViewPagerAdapterManagement(getSupportFragmentManager(), 2);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
}