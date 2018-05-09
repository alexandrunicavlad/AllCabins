package com.alexandrunica.allcabins.cabins;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.media.Rating;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.IOException;
import java.util.List;

public class CabinInfoFragment extends DialogFragment {

    public TextView priceView, nameView, addressView, descriptionView, facilitiesView, ratingView;
    public ImageView mainImage, phoneButton, locationButton, closeButton;
    public RatingBar rating;
    public FloatingActionsMenu add;

    public static CabinInfoFragment newInstance(Cabin cabin) {
        CabinInfoFragment f = new CabinInfoFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("cabin", cabin);
        f.setArguments(arg);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cabin_fragment_layout, container, false);
        priceView = v.findViewById(R.id.cabin_info_price);
        phoneButton = v.findViewById(R.id.cabin_info_phone);
        closeButton = v.findViewById(R.id.cabin_close);
        locationButton = v.findViewById(R.id.cabin_info_location);
        nameView = v.findViewById(R.id.cabin_info_name);
        addressView = v.findViewById(R.id.cabin_info_address);
        descriptionView = v.findViewById(R.id.cabin_info_description);
        facilitiesView = v.findViewById(R.id.cabin_info_facilities);
        rating = v.findViewById(R.id.cabin_rating_star);
        ratingView = v.findViewById(R.id.cabin_info_rating);
        add = v.findViewById(R.id.cabin_add_fav);

        Bundle args = getArguments();
        if (args != null) {
            final Cabin cabin = (Cabin) args.getSerializable("cabin");
            if (cabin != null) {
                addressView.setText(cabin.getAddress());
                nameView.setText(cabin.getName());
                //priceView.setText(cabin.getPrice() + " RON/Noapte");
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


                FloatingActionButton save = (FloatingActionButton) v.findViewById(R.id.action_a);
                FloatingActionButton writeReview = (FloatingActionButton) v.findViewById(R.id.action_b);
                FloatingActionButton uploadPhoto = (FloatingActionButton) v.findViewById(R.id.action_c);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, getActivity());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                dismiss();
            }
        });
        return v;
    }

}
