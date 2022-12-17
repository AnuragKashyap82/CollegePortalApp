package com.anurag.eduventure.Adapters;

import com.anurag.eduventure.Fragments.HomeFragment;
import com.anurag.eduventure.Fragments.IssuedFragment;
import com.anurag.eduventure.Fragments.ReturnedFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return  new HomeFragment();
            case 1:
                return  new IssuedFragment();
            case 2:
                return  new ReturnedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
