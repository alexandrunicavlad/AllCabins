package com.alexandrunica.allcabins.cabins.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.ImageAdapter;
import com.alexandrunica.allcabins.cabins.events.OnReviewAddEvent;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.ReviewModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.alexandrunica.allcabins.service.firebase.ReviewOperations;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class CabinInfoAcitivty extends AppCompatActivity {

    public TextView priceView, nameView, addressView, descriptionView, facilitiesView, ratingView;
    public ImageView mainImage, phoneButton, locationButton, closeButton;
    public RatingBar rating;
    public FloatingActionsMenu add;
    public RelativeLayout imageLayout, mainLayout;
    public ProgressBar progress;

    public ViewPager viewPager;
    public Cabin cabin;
    private CabinOperations cabinOperations;
    private ReviewOperations reviewOperations;

    @Inject
    DatabaseService databaseService;

    @Inject
    Bus bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabin_fragment_layout);

        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);

        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, this);
        reviewOperations = (ReviewOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_REVIEWS_TABLE, this);

        mainLayout = findViewById(R.id.main_layout_cabin);
        progress = findViewById(R.id.cabin_progress);
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

        String id = getIntent().getStringExtra("cabin");
        cabinOperations.getCabin(id);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void updateUI() {
        if (cabin != null) {
            addressView.setText(cabin.getAddress());
            nameView.setText(cabin.getName());
            CurrencyConverter currencyConverter = new CurrencyConverter(this);
            priceView.setText(currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency() + "/" + getResources().getString(R.string.night));
            descriptionView.setText(cabin.getDescription());
            facilitiesView.setText(cabin.getFacilities());
            rating.setRating(cabin.getRating());
            ratingView.setText(String.valueOf(cabin.getRating()) + "/5");

            if (cabin.getPhone() != null && !cabin.getPhone().equals("")) {

            }

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

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
            final String id = preferences.getString("uid", "");

            if (id != null && !id.equals("")) {
                save.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.GONE);
            }
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, CabinInfoAcitivty.this);
                    profileOperations.addFavorite(id, cabin.getId());
                    add.collapse();
                }
            });

            writeReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
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

        progress.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetCabin(OnGetCabinByIdEvent event) {
        if (event.getCabin() != null) {
            cabin = event.getCabin();
            updateUI();
        } else {

        }
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rating_row);

        final RatingBar ratingBar = dialog.findViewById(R.id.rating);
        final AutoCompleteTextView ratingEdit = dialog.findViewById(R.id.rating_edit);


        TextView dialogButton = dialog.findViewById(R.id.submit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewModel ratingModel = new ReviewModel();
                ratingModel.setCabin(cabin.getId());
                ratingModel.setDetails(ratingEdit.getText().toString());
                ratingModel.setFrom("");
                ratingModel.setRating(String.valueOf(ratingBar.getRating()));
                ratingModel.setDate(Calendar.getInstance().getTime().toString());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
                final String id = preferences.getString("uid", "");
                if (id != null && !id.equals("")) {
                    ratingModel.setFrom(id);
                } else {
                    ratingModel.setFrom(getResources().getString(R.string.anonim));
                }
                reviewOperations.insertReview(ratingModel);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Subscribe
    public void onReviewAdded(OnReviewAddEvent event) {
        if (event.getReviewModel() != null) {
            HashMap<String, String> reviews = cabin.getReviews();
            if (reviews == null) {
                reviews = new HashMap<>();
            }
            reviews.put(event.getId(), event.getReviewModel().getRating());
            cabin.setReviews(reviews);
            cabinOperations.insertCabin(cabin);
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
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

}
