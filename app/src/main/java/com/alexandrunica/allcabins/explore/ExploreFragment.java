package com.alexandrunica.allcabins.explore;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.explore.adapter.PlaceAutocompleteAdapter;
import com.alexandrunica.allcabins.explore.event.OnExploreClickListener;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Nica on 4/2/2018.
 */

public class ExploreFragment extends Fragment {

    private Toolbar toolbar;
    private Activity activity;
    private AutoCompleteTextView mAutocompleteView;
    private ImageView cancel;
    private RelativeLayout mainLayout;
    private CabinOperations cabinOperations;

    protected GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(46.7712, 23.6236), new LatLng(46.7712, 23.6236));

    public static ExploreFragment newInstance() {
        ExploreFragment exploreFramgnet = new ExploreFragment();
        return exploreFramgnet;
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
                R.layout.explore_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, activity);

        mGeoDataClient = Places.getGeoDataClient(activity, null);
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        mAutocompleteView = view.findViewById(R.id.input);
        cancel = view.findViewById(R.id.cancel_input);
        mainLayout = view.findViewById(R.id.main_layout);
        mAutocompleteView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cancel.setVisibility(View.VISIBLE);
                    moveTop();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAutocompleteView.setText("");
                            mAutocompleteView.clearFocus();
                            cancel.setVisibility(View.GONE);
                            moveCenter();
                            hideKeyboard();
                            cabinOperations.loadMoreData(10, 1);
                        }
                    });
                } else {
                    hideKeyboard();
                    moveCenter();

                }
            }
        });
        OnExploreClickListener listener = new OnExploreClickListener() {
            @Override
            public void onItemClick(String s) {
                cabinOperations.getFiltredCabin(s);
                hideKeyboard();
                moveCenter();
                ((MainActivity) activity).changeViewpager(1);
            }
        };
        mAdapter = new PlaceAutocompleteAdapter(activity, mGeoDataClient, BOUNDS_GREATER_SYDNEY, null,listener);
        mAutocompleteView.setAdapter(mAdapter);

        return view;
    }

    private void hideKeyboard() {
        if (mAutocompleteView != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAutocompleteView.getWindowToken(), 0);
        }
    }

    private void moveTop() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = 100;
        mainLayout.setLayoutParams(params);
    }

    private void moveCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.setLayoutParams(params);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
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
}

