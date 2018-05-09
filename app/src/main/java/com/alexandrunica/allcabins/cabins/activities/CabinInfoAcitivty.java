package com.alexandrunica.allcabins.cabins.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.ImageAdapter;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CabinInfoAcitivty extends AppCompatActivity {

    public TextView priceView, nameView, addressView, descriptionView, facilitiesView, ratingView;
    public ImageView mainImage, phoneButton, locationButton, closeButton;
    public RatingBar rating;
    public FloatingActionsMenu add;
    public RelativeLayout imageLayout;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabin_fragment_layout);
        priceView = findViewById(R.id.cabin_info_price);
        mainImage = findViewById(R.id.cabin_no_image);
        phoneButton = findViewById(R.id.cabin_info_phone);
        closeButton = findViewById(R.id.cabin_close);
        locationButton = findViewById(R.id.cabin_info_location);
        nameView = findViewById(R.id.cabin_info_name);
        addressView = findViewById(R.id.cabin_info_address);
        descriptionView = findViewById(R.id.cabin_info_description);
        facilitiesView = findViewById(R.id.cabin_info_facilities);
        rating = findViewById(R.id.cabin_rating_star);
        ratingView = findViewById(R.id.cabin_info_rating);
        add = findViewById(R.id.cabin_add_fav);
        imageLayout = findViewById(R.id.image_layout);
        viewPager = findViewById(R.id.image_pager);


        Bundle args = getIntent().getExtras();
        if (args != null) {
            final Cabin cabin = (Cabin) args.getSerializable("cabin");
            if (cabin != null) {
                addressView.setText(cabin.getAddress());
                nameView.setText(cabin.getName());
                CurrencyConverter currencyConverter = new CurrencyConverter(this);
                priceView.setText(currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency()+ "/" + getResources().getString(R.string.night));
                descriptionView.setText(cabin.getDescription());
                facilitiesView.setText(cabin.getFacilities());
                rating.setRating(cabin.getRating());
                ratingView.setText(String.valueOf(cabin.getRating()) + "/5");

                phoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                if (cabin.getPictures() != null) {
                    List<String> listOfImage = new ArrayList<>();
                    Iterator myVeryOwnIterator = cabin.getPictures().keySet().iterator();
                    while (myVeryOwnIterator.hasNext()) {
                        String key = (String) myVeryOwnIterator.next();
                        String value = (String) cabin.getPictures().get(key);
                        listOfImage.add(value);
                    }
                    ImageAdapter adapter = new ImageAdapter(this, listOfImage);
                    viewPager.setAdapter(adapter);
                    viewPager.setOffscreenPageLimit(3);
                    mainImage.setVisibility(View.GONE);
                } else {
                    mainImage.setVisibility(View.VISIBLE);
                }


                FloatingActionButton save = (FloatingActionButton) findViewById(R.id.action_a);
                FloatingActionButton writeReview = (FloatingActionButton) findViewById(R.id.action_b);
                FloatingActionButton uploadPhoto = (FloatingActionButton) findViewById(R.id.action_c);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, CabinInfoAcitivty.this);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
                        final String id = preferences.getString("uid", "");
                        profileOperations.addFavorite(id, cabin.getId());
                        add.collapse();
                    }
                });

                writeReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add.collapse();
                    }
                });

                uploadPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add.collapse();
                    }
                });

            }
        }
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
