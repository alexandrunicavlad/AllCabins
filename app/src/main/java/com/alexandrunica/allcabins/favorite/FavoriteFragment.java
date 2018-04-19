package com.alexandrunica.allcabins.favorite;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.favorite.adapter.FavoriteAdapter;
import com.alexandrunica.allcabins.favorite.event.OnFavDone;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Nica on 4/2/2018.
 */

public class FavoriteFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RelativeLayout mainLayout;
    private List<Cabin> list;
    private FavoriteAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static FavoriteFragment newInstance() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        return favoriteFragment;
    }

    @Inject
    Bus bus;

    @Inject
    DatabaseService databaseService;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DaggerDbApplication app = (DaggerDbApplication) activity.getApplicationContext();
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.favorite_layout, container, false);
        list= new ArrayList<>();
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recycler_favorite);
        mainLayout = view.findViewById(R.id.favorite_main_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        addUserFav();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        addUserFav();


        TextView explore = view.findViewById(R.id.explore_submit);
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).changeViewpager(0);
            }
        });
        //ecyclerView.setAdapter(adapter);
        return view;
    }

    private void addUserFav() {

        if (databaseService.getUser() != null) {
            HashMap<String, String> mapList = databaseService.getUser().getFavoriteList();
            if (mapList != null) {
                list.clear();
                for (Map.Entry<String, String> entry : mapList.entrySet()) {
                    String value = entry.getValue();
                    CabinOperations cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, activity);
                    cabinOperations.getCabinFromId(value);
                }
            } else {
                recyclerView.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void onAddFavorite(OnFavDone event) {
        if (event.getCabin() != null) {
            if (list.size() == 0) {
                list.add(event.getCabin());
                adapter = new FavoriteAdapter(activity, list);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            } else {
                list.add(event.getCabin());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setRecycler() {
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}
