package com.alexandrunica.allcabins.cabins;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.CabinAdapter;
import com.alexandrunica.allcabins.cabins.adapter.CabinsSquareAdapter;
import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.helper.PaginationScrollListener;
import com.alexandrunica.allcabins.cabins.helper.RecyclerEventOnScollListener;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.widget.Slidr;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Nica on 4/2/2018.
 */

public class CabinsFragment extends Fragment {

    @Inject
    Bus bus;

    private Activity activity;
    private boolean isDistanceSelected;
    private boolean isPriceSelected;
    private ImageView toolbarFilter;
    private LinearLayout filterLayout;
    private RecyclerView recyclerView;
    private CabinsSquareAdapter adapter;
    private CabinOperations cabinOperations;
    private List<Cabin> cabinList;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    private int TOTAL_ITEM_EACH_LOAD = 10;

    public static CabinsFragment newInstance() {
        CabinsFragment cabinsFragment = new CabinsFragment();
        return cabinsFragment;
    }

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

        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, activity);
        cabinList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 2);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
//            @Override
//            protected void loadMoreItems() {
//                isLoading = true;
//                currentPage += 1;
//                loadNextPage();
//            }
//
//            @Override
//            public int getTotalPageCount() {
//                return 0;
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return isLastPage;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//        });

        //loadFirstPage();

        setFilter(view);

//        for(int i=0;i<20;i++) {
//            LocationModel locationModel = new LocationModel("46.75817243758575", "23.581339692866802");
//
//            Cabin cabin = new Cabin();
//            cabin.setName("La Cabana" + String.valueOf(i));
//            cabin.setAddress("");
//            cabin.setLocation(new Gson().toJson(locationModel));
//            cabin.setPhone("722369851");
//            cabin.setEmail("lacabana@lacabana.com");
//            cabin.setPrice("15" +String.valueOf(i) +" Lei");
//            cabin.setRating(3);
//            cabin.setThumbPhotoUrl("");
//            cabin.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec eros porta, dapibus lectus sit amet, ultricies diam. Sed egestas vestibulum porttitor. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus at ex lacinia, sodales risus sed, vehicula nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas nec lorem scelerisque, aliquet sem eu, porttitor ligula. Quisque euismod nec lorem sit amet consectetur. Mauris non egestas mi, ut sollicitudin odio. Aliquam tristique ante eget rutrum dapibus. Donec quis volutpat neque, a iaculis metus. Morbi cursus elit ac nulla aliquet sagittis.");
//            cabin.setFacilities("dapibus lectus sit amet, ultricies diam. Sed egestas vestibulum porttitor. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus at ex lacinia, sodales risus sed, vehicula nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Maecenas nec lorem scelerisque, aliquet sem eu, porttitor ligula. Quisque euismod nec lorem sit amet consectetur. Mauris non egestas mi, ut sollicitudin odio. Aliquam tristique ante eget rutrum dapibus. Donec quis volutpa");
//            cabin.setIdAdded("nika_yo_99@yahoo.com");
//            cabinOperations.insertCabin(cabin);
//        }

        return view;
    }

    private void loadFirstPage() {
        if (cabinOperations != null) {
            cabinOperations.loadMoreData(TOTAL_ITEM_EACH_LOAD, currentPage);
        }
    }

    private void loadNextPage() {
        if (cabinOperations != null) {
            cabinOperations.loadMoreData(TOTAL_ITEM_EACH_LOAD, currentPage);
        }

        //isLoading = false;

    }

    private void loadMoreData() {
        currentPage++;
        if (cabinOperations != null) {
            cabinOperations.loadMoreData(TOTAL_ITEM_EACH_LOAD, currentPage);
        }
    }

    private void setRecyclerView(List<Cabin> list) {
        if (list != null) {
            cabinList = list;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CabinsSquareAdapter(activity, cabinList);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Subscribe
    public void onGetCabins(OnGetCabinEvent event) {
        if (event.getCabins() != null) {
            setRecyclerView(event.getCabins());
        } else {
            Toast.makeText(activity, "Unable to get cabins", Toast.LENGTH_SHORT).show();
        }
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
