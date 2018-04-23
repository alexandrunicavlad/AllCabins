package com.alexandrunica.allcabins.map;

import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.cabins.model.ShortCabin;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.adapter.CustomInfoWindowMap;
import com.alexandrunica.allcabins.map.event.OnGetShortCabinEvent;
import com.alexandrunica.allcabins.map.model.Cluster;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Nica on 4/2/2018.
 */

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener,
        GoogleMap.CancelableCallback, ClusterManager.OnClusterClickListener<Cluster> {

    @Inject
    Bus bus;

    private FragmentActivity activity;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private Location currentLocation;
    private boolean isInfoWindowShown;
    private List<ShortCabin> cabins;

    private Marker currentMarker;
    private Marker popupMarker;
    private PopupWindow popupWindow;

    private LatLngBounds romanianBounds;
    private ClusterManager<Cluster> mClusterManager;

    private RelativeLayout infoLayout;
    private ImageView locateButton;

    private CabinOperations cabinOperations;

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
        View view = (ViewGroup) inflater.inflate(R.layout.map_fragment, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        infoLayout = view.findViewById(R.id.info_bottom_layout);
        locateButton = view.findViewById(R.id.initial_location_btn);
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoLayout.getVisibility() == View.VISIBLE) {
                    collapse(infoLayout);
                } else {
                    expand(infoLayout);
                }
            }
        });
        cabins = new ArrayList<>();

        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void setMarkers() {

        for (ShortCabin cabin : cabins) {
            if (cabin.getLocation() != null && !cabin.getLocation().isEmpty()) {
                LocationModel locationModel = new Gson().fromJson(cabin.getLocation(), new TypeToken<LocationModel>() {
                }.getType());
                //LatLng cabinLoc = new LatLng(Double.parseDouble(locationModel.getLatitude()), Double.parseDouble(locationModel.getLongitude()));
//                Marker marker = googleMap.addMarker(new MarkerOptions().position(cabinLoc)
//                        .title(cabin.getId()));
//                marker.setTag(cabin);
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
                Cluster offsetItem = new Cluster(Double.parseDouble(locationModel.getLatitude()), Double.parseDouble(locationModel.getLongitude()));
                mClusterManager.addItem(offsetItem);
            }
        }
        mClusterManager.cluster();
    }

    private void setCurrentMarker() {
        if (currentLocation != null) {
            LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            if (currentMarker != null) {
                currentMarker.remove();
            }
            try {
                currentMarker = googleMap.addMarker(new MarkerOptions().position(pos)
                        .title("Current Marker")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location)));
            } catch (Exception e) {
                //Toast.makeText(activity, activity.getResources().getString(R.string.unable_to_load_location), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, activity);
        cabinOperations.getCabins();

        initMap();
    }

    private void startCluster() {
        mClusterManager = new ClusterManager<Cluster>(activity, googleMap);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        googleMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setAnimation(true);
        setMarkers();
        mClusterManager.cluster();
    }

    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        String a = "21";
        return false;
    }

    @Override
    public boolean onClusterClick(com.google.maps.android.clustering.Cluster<Cluster> cluster) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                cluster.getPosition(), (float) Math.floor(googleMap
                        .getCameraPosition().zoom + 1)), 300,
                null);
        return true;
    }

    private void initInfoBottom(Cabin cabin) {
        ImageView infoBottomImage = infoLayout.findViewById(R.id.bottom_image);
        ImageView infoBottomClose = infoLayout.findViewById(R.id.bottom_close);
        TextView infoBottomName = infoLayout.findViewById(R.id.bottom_name);
        TextView infoBottomAddress = infoLayout.findViewById(R.id.bottom_address);
        TextView infoBottomPrice = infoLayout.findViewById(R.id.bottom_price);
        TextView infoBottomDistance = infoLayout.findViewById(R.id.bottom_distance);

        if (cabin.getThumbPhotoUrl() != null && !cabin.getThumbPhotoUrl().isEmpty()) {
            Picasso.with(getContext()).load(cabin.getThumbPhotoUrl()).into(infoBottomImage);
        }

        infoBottomName.setText(cabin.getName());
        infoBottomAddress.setText(cabin.getAddress());
        infoBottomPrice.setText(cabin.getPrice() + "/Night");

        infoBottomClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(infoLayout);
            }
        });
    }

    private void initMap() {

        googleMap.clear();
        romanianBounds = new LatLngBounds(
                new LatLng(41, 18), new LatLng(51, 32));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(romanianBounds.getCenter(), 6));
        googleMap.setMinZoomPreference(6);
        googleMap.setLatLngBoundsForCameraTarget(romanianBounds);
       // googleMap.setOnMarkerClickListener(this);
        googleMap.setTrafficEnabled(false);

//        ImageView locationBtn = getView().findViewById(R.id.initial_location_btn);
//        locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(romanianBounds.getCenter(), 6));
//
//            }
//        });

    }

    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTitle() != null && (currentLocation == null || !marker.getTitle().equals("Current marker"))) {
            popupMarker = marker;
            initInfoBottom((Cabin) marker.getTag());
            expand(infoLayout);
        }
        return true;
    }


//    private void calcDistance(Marker marker) {
//        Resort resort = (Resort) resorts.get(marker.getTitle());
//        new CalcDistanceResorts(activity, currentLocation).execute(resort);
//
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

    public void refreshMarkers() {
        googleMap.clear();
        setMarkers();
        if (currentLocation != null) {
            setCurrentMarker();
        }
    }

    @Subscribe
    public void onGetShortCabins(OnGetShortCabinEvent event) {
        if (event.getCabins() != null) {
            cabins = event.getCabins();
            if (googleMap != null) {
                startCluster();
            }
        } else {
            Toast.makeText(activity, "Unable to get cabins", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }

    private void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
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

    private void collapse(final View v) {
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


    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);

        mGoogleApiClient.connect();
    }

    /**
     * disconnect from google apo
     * detach presenter
     */
    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


}