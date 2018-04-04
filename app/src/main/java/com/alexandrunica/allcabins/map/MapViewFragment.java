package com.alexandrunica.allcabins.map;

import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.alexandrunica.allcabins.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nica on 4/2/2018.
 */

public class MapViewFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.CancelableCallback {

    private FragmentActivity activity;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private Location currentLocation;
    private boolean isInfoWindowShown;

    private Marker currentMarker;
    private Marker popupMarker;
    private PopupWindow popupWindow;

    private LatLngBounds romanianBounds;

    private RelativeLayout infoLayout;
    private ImageView locateButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.map_fragment, container, false);
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
                if (infoLayout.getVisibility()==View.VISIBLE) {
                    collapse(infoLayout);
                } else {
                    expand(infoLayout);
                }
            }
        });
        initInfoBottom();
        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        initMap();
    }

    private void initInfoBottom() {
        ImageView infoBottomImage = infoLayout.findViewById(R.id.bottom_image);
        ImageView infoBottomClose = infoLayout.findViewById(R.id.bottom_close);
        TextView infoBottomName = infoLayout.findViewById(R.id.bottom_name);
        TextView infoBottomAddress = infoLayout.findViewById(R.id.bottom_address);
        TextView infoBottomPrice = infoLayout.findViewById(R.id.bottom_price);
        TextView infoBottomDistance = infoLayout.findViewById(R.id.bottom_distance);

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
        googleMap.setOnMarkerClickListener(this);
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
        }
        return true;
    }

//    private void createPopupMarker(String resortId) {
//        if (popupWindow != null) {
//            popupWindow.dismiss();
//        }
//
//        final View popup = new ResortCard(activity, (Resort) resorts.get(resortId)).getView();
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,239,getResources().getDisplayMetrics());
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ScreenSize.getWidth(activity),height);
//        popup.setLayoutParams(layoutParams);
//        popupWindow = new PopupWindow(popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                    bus.post(new OnMarkerCloseEvent());
//                    return true;
//                }
//                return false;
//            }
//        });
//        popupWindow.setAnimationStyle(R.style.Animation);
//
//    }

//    /**
//     * show selected marker's popup
//     */
//    private void showPopup() {
//        Fragment account = activity.getSupportFragmentManager().findFragmentByTag(activity.getResources().getString(R.string.account_fragment_tag));
//        if (isVisible() && getParentFragment().getUserVisibleHint() && account == null) {
//            if (popupMarker != null && popupWindow != null) {
//                int newHeight;
//
//                Point p = googleMap.getProjection().toScreenLocation(popupMarker.getPosition());
//                newHeight = (int) (p.y -  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,239,getResources().getDisplayMetrics()));
//
//                popupWindow.showAtLocation(getView(), Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, newHeight);
//                bus.post(new OnMarkerShowEvent());
//                onInfoMarkerShow();
//            }
//        }
//    }

//    private void calcDistance(Marker marker) {
//        Resort resort = (Resort) resorts.get(marker.getTitle());
//        new CalcDistanceResorts(activity, currentLocation).execute(resort);
//
//    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * disconnect from google apo
     * detach presenter
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (FragmentActivity) context;
    }

    public void setMarkers() {
    }


    public void refreshMarkers() {
        googleMap.clear();
        setMarkers();
        if (currentLocation != null) {
            setCurrentMarker();
        }
    }

    public void onInfoMarkerShow() {
        if (isInfoWindowShown) {
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            isInfoWindowShown = false;
        } else {
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
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
}