package com.alexandrunica.allcabins.cabins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandrunica.allcabins.R;

/**
 * Created by Nica on 4/2/2018.
 */

public class CabinsFragment  extends Fragment {

    private Activity activity;

    public static CabinsFragment newInstance() {
        CabinsFragment cabinsFragment = new CabinsFragment();
        return cabinsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.explore_fragment, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}
