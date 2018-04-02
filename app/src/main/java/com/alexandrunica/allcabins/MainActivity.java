package com.alexandrunica.allcabins;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private RelativeLayout toolbarLayout;
    private Toolbar toolbar;
    private MainPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.toolbar_background)));
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.toolbar_color)));
        toolbar.setTitle(getResources().getString(R.string.empty_string));
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.main_screen_pager);
        tabLayout = findViewById(R.id.main_screen_tabs);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new MainPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
        setupTabIcons();
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
                break;
            case 1:
                toolbarTitle.setText("List");
                break;
            case 2:
                toolbarTitle.setText("Map");
                break;
            case 3:
                toolbarTitle.setText("Favorite");
                break;
            case 4:
                toolbarTitle.setText("Profile");
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
}
