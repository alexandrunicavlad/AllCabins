package com.alexandrunica.allcabins.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.alexandrunica.allcabins.cabins.CabinInfoFragment;
import com.alexandrunica.allcabins.cabins.activities.CabinInfoAcitivty;
import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.cabins.model.ShortCabin;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.adapter.CustomInfoWindowMap;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.map.event.OnGetShortCabinEvent;
import com.alexandrunica.allcabins.map.helper.LocationHelper;
import com.alexandrunica.allcabins.map.model.Cluster;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
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
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationModel locationModel;

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
                if (locationModel != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationModel.getDoubleLatitude(),locationModel.getDoubleLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(locationModel.getDoubleLatitude(),locationModel.getDoubleLongitude()))
                            .zoom(9)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
        cabins = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocation();
        }

        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {

                }
                return;
            }
        }
    }

    public void setMarkers() {
        for (ShortCabin cabin : cabins) {
            if (cabin.getLocation() != null && !cabin.getLocation().isEmpty()) {
                LocationModel locationModel = new Gson().fromJson(cabin.getLocation(), new TypeToken<LocationModel>() {
                }.getType());
                Cluster offsetItem = new Cluster(Double.parseDouble(locationModel.getLatitude()), Double.parseDouble(locationModel.getLongitude()), cabin.getId(), "");
                mClusterManager.addItem(offsetItem);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    locationModel = new LocationModel(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("last_location", new Gson().toJson(locationModel));
                    editor.apply();
                }
            }
        });
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
        cabinOperations.getCabin(clusterItem.getTitle());
        return true;
    }

    @Override
    public boolean onClusterClick(com.google.maps.android.clustering.Cluster<Cluster> cluster) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                cluster.getPosition(), (float) Math.floor(googleMap
                        .getCameraPosition().zoom + 1)), 300,
                null);
        return true;
    }

    private void initInfoBottom(final Cabin cabin) {
        ImageView infoBottomImage = infoLayout.findViewById(R.id.bottom_image);
        ImageView infoBottomClose = infoLayout.findViewById(R.id.bottom_close);
        TextView infoBottomName = infoLayout.findViewById(R.id.bottom_name);
        TextView infoBottomAddress = infoLayout.findViewById(R.id.bottom_address);
        TextView infoBottomPrice = infoLayout.findViewById(R.id.bottom_price);
        TextView infoBottomDistance = infoLayout.findViewById(R.id.bottom_distance);

        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//                DialogFragment newFragment = CabinInfoFragment.newInstance(cabin);
//                newFragment.show(ft, "dialog");
                collapse(infoLayout);
                Intent intent = new Intent(activity, CabinInfoAcitivty.class);
                intent.putExtra("cabin",(Serializable)cabin);
                activity.startActivity(intent);
            }
        });

        if (cabin.getThumbPhotoUrl() != null && !cabin.getThumbPhotoUrl().isEmpty()) {
            Picasso.with(activity).load(cabin.getThumbPhotoUrl()).fit().into(infoBottomImage);
        } else {
            Picasso.with(activity).load(R.drawable.main_image);
        }

        String distanceFinal = "";
        if (cabin.getLocation() != null && !cabin.getLocation().equals("")) {
            LocationModel cabinLocation = new Gson().fromJson(cabin.getLocation(), LocationModel.class);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String lastLocation = preferences.getString("last_location", "");
            if (lastLocation != null && !lastLocation.equals("")) {
                LocationModel currentLocation = new Gson().fromJson(lastLocation, LocationModel.class);
                if (currentLocation != null && cabinLocation != null) {
                    LocationHelper locationHelper = new LocationHelper(activity);
                    //float distance = locationHelper.calcRoute(currentLocation, cabinLocation) / 1000;
                    double distanceD = locationHelper.calculationByDistance(currentLocation, cabinLocation);
                    distanceFinal = String.valueOf((int) distanceD);
                }
            }
        }
        if (!distanceFinal.equals("")) {
            infoBottomDistance.setText(distanceFinal + " KM");
            infoBottomDistance.setVisibility(View.VISIBLE);
        } else {
            infoBottomDistance.setVisibility(View.GONE);
        }

        infoBottomName.setText(cabin.getName());
        infoBottomAddress.setText(cabin.getAddress());
        CurrencyConverter currencyConverter = new CurrencyConverter(activity);
        infoBottomPrice.setText(currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency()+ "/" + getResources().getString(R.string.night));

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

    }

    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTitle() != null && (currentLocation == null || !marker.getTitle().equals("Current marker"))) {
            popupMarker = marker;
            initInfoBottom((Cabin) marker.getTag());
            expand(infoLayout);
        }
        return true;
    }

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
    public void onGetCabin(OnGetCabinByIdEvent event) {
        if (event.getCabin() != null) {
            initInfoBottom(event.getCabin());
            expand(infoLayout);
        } else {

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
            Toast.makeText(activity, getResources().getString(R.string.err_map) , Toast.LENGTH_SHORT).show();
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