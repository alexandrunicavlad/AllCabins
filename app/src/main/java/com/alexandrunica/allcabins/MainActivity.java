package com.alexandrunica.allcabins;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.ProfileFragment;
import com.alexandrunica.allcabins.profile.event.OnLoginEvent;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.alexandrunica.allcabins.service.firebase.auth.FirebaseAuthentication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.FirebaseApp;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private RelativeLayout toolbarLayout;
    private ImageView toolbarFilter;
    private Toolbar toolbar;
    private MainPageAdapter adapter;
    private GoogleApiClient mGoogleApiClient;

    @Inject
    FirebaseAuthentication authentication;

    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.alexandrunica.allcabins",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        FirebaseApp.initializeApp(this);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.toolbar_background)));
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.toolbar_color)));
        toolbar.setTitle("");
        toolbarFilter = findViewById(R.id.toolbar_filter);
        viewPager = findViewById(R.id.main_screen_pager);
        tabLayout = findViewById(R.id.main_screen_tabs);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new MainPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
        hideStatus();
        setupTabIcons();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        sync();
    }

    private void sync() {
        ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, this);
    }

    public void changeViewpager(int position) {
        viewPager.setCurrentItem(position);
    }

    public void hideStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void showStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }



    private void setupTabIcons() {
        TabLayout.Tab explore = tabLayout.getTabAt(0);
        TabLayout.Tab cabins = tabLayout.getTabAt(1);
        TabLayout.Tab map = tabLayout.getTabAt(2);
        TabLayout.Tab favorite = tabLayout.getTabAt(3);
        TabLayout.Tab profile = tabLayout.getTabAt(4);
        if (explore != null) {
            explore.setIcon(R.drawable.ic_explore);
            setTabColor(explore, R.color.selected_tab);
        }

        if (cabins != null) {
            cabins.setIcon(R.drawable.ic_list);
            setTabColor(cabins, R.color.unselected_tab);
        }

        if (map != null) {
            map.setIcon(R.drawable.ic_map);
            setTabColor(map, R.color.unselected_tab);
        }

        if (favorite != null) {
            favorite.setIcon(R.drawable.ic_favorite);
            setTabColor(favorite, R.color.unselected_tab);
        }

        if (profile != null) {
            profile.setIcon(R.drawable.ic_profile);
            setTabColor(profile, R.color.unselected_tab);
        }
    }

    private void setTabColor(TabLayout.Tab tab, int color) {
        Drawable icon = tab.getIcon();

        if (icon != null)
            icon.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toolbar.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                toolbar.setVisibility(View.GONE);
                hideStatus();
                break;
            case 1:
                showStatus();
                break;
            case 2:
                toolbarTitle.setText("Map");
                toolbarFilter.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                showStatus();
                break;
            case 3:
                toolbarTitle.setText("Favorite");
                toolbarFilter.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                showStatus();
                break;
            case 4:
                toolbar.setVisibility(View.GONE);
                toolbarTitle.setText("Profile");
                toolbarFilter.setVisibility(View.GONE);
                showStatus();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setTabColor(tab, R.color.selected_tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        setTabColor(tab, R.color.unselected_tab);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String a = "";
    }
}
