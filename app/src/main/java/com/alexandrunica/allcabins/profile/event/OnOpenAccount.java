package com.alexandrunica.allcabins.profile.event;

import android.support.v4.app.Fragment;

/**
 * Created by Nica on 4/5/2018.
 */

public class OnOpenAccount {

    private Fragment fragment;

    public OnOpenAccount(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
