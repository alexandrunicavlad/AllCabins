package com.alexandrunica.allcabins.favorite;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.CabinAdapter;
import com.alexandrunica.allcabins.favorite.adapter.FavoriteAdapter;
import com.alexandrunica.allcabins.map.MapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nica on 4/2/2018.
 */

public class FavoriteFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;

    public static FavoriteFragment newInstance() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        return favoriteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.favorite_layout, container, false);
        List<String> list = new ArrayList<>();
        list.add("Cabana Viscol mare peste noi s-a abatut");
        list.add("Cabana Muierii");
        list.add("Cabana Acolo");
        list.add("Cabana Ce faci");

        recyclerView = view.findViewById(R.id.recycler_favorite);
        FavoriteAdapter adapter = new FavoriteAdapter(activity, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}
