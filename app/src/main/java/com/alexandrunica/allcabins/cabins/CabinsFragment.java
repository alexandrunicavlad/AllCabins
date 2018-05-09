package com.alexandrunica.allcabins.cabins;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.CabinsSquareAdapter;
import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.events.OnMoreEvent;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.explore.event.OnGetFiltredCabinEvent;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.widget.Slidr;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.alexandrunica.allcabins.cabins.adapter.CabinsSquareAdapter.FOOTER_VIEW;

/**
 * Created by Nica on 4/2/2018.
 */

public class CabinsFragment extends Fragment {

    @Inject
    Bus bus;

    private Activity activity;
    private boolean isDistanceSelected;
    private boolean isPriceSelected;
    private int price;
    private int distance;
    private ImageView toolbarFilter;
    private TextView toolbarTitle;
    private LinearLayout filterLayout;
    private RecyclerView recyclerView;
    private CabinsSquareAdapter adapter;
    private CabinOperations cabinOperations;
    private List<Cabin> cabinList;
    private RelativeLayout sorryLayout;
    private Toolbar toolbar;
    private boolean isFiltred = false;

    private static final int PAGE_START = 1;
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
        toolbar = activity.findViewById(R.id.toolbar);
        toolbarFilter = activity.findViewById(R.id.toolbar_filter);
        toolbarTitle = activity.findViewById(R.id.toolbar_title);
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
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case FOOTER_VIEW:
                        return 2;
                    default:
                        return 1;
                }

            }
        });
        sorryLayout = view.findViewById(R.id.sorry_main_layout);
        TextView startExplore = view.findViewById(R.id.explore_submit);
        startExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).changeViewpager(0);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        loadFirstPage();

        setFilter(view);

//        for(int i=0;i<32;i++) {
//            LocationModel locationModel = new LocationModel("46." + String.valueOf(i) + "17243758575", "23." + String.valueOf(i) + "339692866802");
//
//            Cabin cabin = new Cabin();
//            cabin.setName("La Cabana" + String.valueOf(i));
//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(activity, Locale.getDefault());
//
//            try {
//                addresses = geocoder.getFromLocation(Double.parseDouble(locationModel.getLatitude()), Double.parseDouble(locationModel.getLongitude()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                cabin.setAddress(addresses.get(0).getAddressLine(0));
//                cabin.setCity(addresses.get(0).getLocality());
//                cabin.setState(addresses.get(0).getAdminArea());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            cabin.setLocation(new Gson().toJson(locationModel));
//            cabin.setPhone("722369851");
//            cabin.setEmail("lacabana@lacabana.com");
//            cabin.setPrice("1" +String.valueOf(i));
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

    private void loadMoreData() {
        currentPage++;
        if (cabinOperations != null) {
            cabinOperations.loadMoreData(TOTAL_ITEM_EACH_LOAD, currentPage);
        }
    }

    private void filterByPrice() {
        List<Cabin> filtredList = new ArrayList<>();
        if (cabinList != null) {
            for (Cabin cabin : cabinList) {
                CurrencyConverter currencyConverter = new CurrencyConverter(activity);
                if (Integer.parseInt(currencyConverter.convertCurrency(cabin.getPrice())) <= price) {
                    filtredList.add(cabin);
                }
            }
            if (filtredList.size() != 0) {
                cabinList = filtredList;
            } else {
                Toast.makeText(activity, getResources().getString(R.string.err_message), Toast.LENGTH_SHORT).show();
            }

            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        sorryLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (adapter != null) {
            adapter.updateReceiptsList(cabinList);
            adapter.setFooter(isFiltred);
        } else {
            OnMoreEvent listener = new OnMoreEvent() {
                @Override
                public void onLoadMore() {
                    loadMoreData();
                }
            };
            adapter = new CabinsSquareAdapter(activity, cabinList, listener);
            recyclerView.setAdapter(adapter);
        }

    }

    private void updateUI(String city) {
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarFilter.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        if (isFiltred) {
            toolbarTitle.setText(getResources().getString(R.string.near) + city.toUpperCase());
            toolbarFilter.setVisibility(View.GONE);
        } else {
            toolbarTitle.setText(getResources().getString(R.string.recomended));
            toolbarFilter.setVisibility(View.VISIBLE);
        }
    }

    private void updateToolbar() {
        toolbarTitle.setVisibility(View.GONE);
        toolbarFilter.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
    }

    @Subscribe
    public void onGetCabinsFiltred(OnGetFiltredCabinEvent event) {
        if (event.getCabins() != null && event.getCabins().size() != 0) {
            isFiltred = true;
            cabinList = event.getCabins();
            setRecyclerView();
            updateUI(event.getCity());
        } else {
            updateToolbar();
            recyclerView.setVisibility(View.GONE);
            sorryLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onGetCabins(OnGetCabinEvent event) {
        if (event.getCabins() != null && event.getCabins().size() != 0) {
            isFiltred = false;
            cabinList = event.getCabins();
            if (isPriceSelected) {
                filterByPrice();
            } else {
                setRecyclerView();
            }

            updateUI("");
        } else {
            updateToolbar();
            recyclerView.setVisibility(View.GONE);
            sorryLayout.setVisibility(View.VISIBLE);
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
                price = (int) slidrPrice.getCurrentValue();
                distance = (int) slidrDistance.getCurrentValue();
                if (isPriceSelected) {
                    filterByPrice();
                }
                if (isDistanceSelected) {

                }

                if (!isPriceSelected && !isDistanceSelected) {
                    currentPage = 1;
                    loadFirstPage();
                }
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
