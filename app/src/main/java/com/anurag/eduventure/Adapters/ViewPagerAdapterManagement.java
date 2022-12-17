package com.anurag.eduventure.Adapters;

import com.anurag.eduventure.Fragments.AllIssuedFragment;
import com.anurag.eduventure.Fragments.AppliedFragment;
import com.anurag.eduventure.Fragments.HomeFragment;
import com.anurag.eduventure.Fragments.IssuedFragment;
import com.anurag.eduventure.Fragments.ReturnedFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapterManagement extends FragmentPagerAdapter {

    public ViewPagerAdapterManagement(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return  new AppliedFragment();
            case 1:
                return  new AllIssuedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
