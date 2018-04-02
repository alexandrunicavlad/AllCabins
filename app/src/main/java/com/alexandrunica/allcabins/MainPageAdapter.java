package com.alexandrunica.allcabins;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alexandrunica.allcabins.cabins.CabinsFragment;
import com.alexandrunica.allcabins.explore.ExploreFragment;
import com.alexandrunica.allcabins.favorite.FavoriteFragment;
import com.alexandrunica.allcabins.map.MapFragment;
import com.alexandrunica.allcabins.profile.ProfileFragment;

import java.util.Map;

/**
 * Created by Nica on 4/2/2018.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private final int num_fragment = 5;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ExploreFragment.newInstance();
            case 1:
                return CabinsFragment.newInstance();
            case 2:
                return MapFragment.newInstance();
            case 3:
                return FavoriteFragment.newInstance();
            case 4:
                return ProfileFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num_fragment;
    }
}
