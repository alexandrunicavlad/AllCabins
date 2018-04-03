package com.alexandrunica.allcabins.cabins;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.CabinAdapter;
import com.alexandrunica.allcabins.widget.Slidr;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nica on 4/2/2018.
 */

public class CabinsFragment extends Fragment {

    private Activity activity;
    private boolean isDistanceSelected;
    private boolean isPriceSelected;
    private ImageView toolbarFilter;
    private LinearLayout filterLayout;
    private RecyclerView recyclerView;


    public static CabinsFragment newInstance() {
        CabinsFragment cabinsFragment = new CabinsFragment();
        return cabinsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.cabins_layout, container, false);

        filterLayout = view.findViewById(R.id.filter_layout);
        toolbarFilter = activity.findViewById(R.id.toolbar_filter);
        toolbarFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterLayout.getVisibility() == View.VISIBLE) {
                    collapse(filterLayout);
                } else {
                    expand(filterLayout);
                }
            }
        });
        List<String> list = new ArrayList<>();
        list.add("Cabana Viscol mare peste noi s-a abatut");
        list.add("Cabana Muierii");
        list.add("Cabana Acolo");
        list.add("Cabana Ce faci");

        recyclerView = view.findViewById(R.id.recycler);
        CabinAdapter adapter = new CabinAdapter(activity, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        setFilter(view);


        return view;
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void setFilter(View view) {
        final Slidr slidrDistance = view.findViewById(R.id.slidr_distance);
        slidrDistance.setMax(200);
        slidrDistance.setKm(true);
        slidrDistance.setCurrentValue(50);

        final Slidr slidrPrice = view.findViewById(R.id.slidr_price);
        slidrPrice.setMax(200);
        slidrPrice.setKm(false);
        slidrPrice.setCurrentValue(50);

        final ToggleButton toggleButtonDistance = view.findViewById(R.id.filter_distance_check);
        toggleButtonDistance.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                isDistanceSelected = on;
            }
        });

        final ToggleButton toggleButtonPrice = view.findViewById(R.id.filter_price_check);
        toggleButtonPrice.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                isPriceSelected = on;
            }
        });

        ImageView closeFilter = view.findViewById(R.id.filter_close);
        ImageView doneFilter = view.findViewById(R.id.filter_apply);

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(filterLayout);
            }
        });

        doneFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(filterLayout);
                //filter with
                float price = slidrPrice.getCurrentValue();
                float distance = slidrDistance.getCurrentValue();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}
