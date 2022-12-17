package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterIssuedBooks;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Adapters.ViewPagerAdapter;
import com.anurag.eduventure.Models.ModelIssuedBooks;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityMyIssuedBookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyIssuedBookActivity extends AppCompatActivity {

    PagerAdapter pagerAdapter;
    TabItem mHome, mIssued, mReturned;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_issued_book);

        mHome = findViewById(R.id.home);
        mIssued = findViewById(R.id.issued);
        mReturned = findViewById(R.id.returned);
        tabLayout = findViewById(R.id.include);

        ViewPager viewPager  =findViewById(R.id.fragmentContainer);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2){
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