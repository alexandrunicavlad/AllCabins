package com.alexandrunica.allcabins.cabins.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;

import java.util.List;

/**
 * Created by Nica on 4/3/2018.
 */

public class CabinAdapter extends RecyclerView.Adapter<CabinAdapter.CabinHolder> {


    private List<Cabin> cabinList;
    private Context context;


    public CabinAdapter(Context context, List<Cabin> cabinList) {
        this.context = context;
        this.cabinList = cabinList;
    }

    @Override
    public CabinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cabin_row, parent, false);
        return new CabinHolder(v);
    }

    @Override
    public void onBindViewHolder(final CabinHolder holder, int position) {
        final Cabin cabin = cabinList.get(position);


        holder.nameView.setText(cabin.getName());
        holder.addressView.setText(cabin.getAddress());

        holder.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call
            }
        });

        holder.heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, context);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                final String id = preferences.getString("uid", "");
                if (holder.heartButton.isChecked()) {
                    profileOperations.addFavorite(id, cabin.getId());
                } else {
                    profileOperations.removeFavorite(id, cabin.getId());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return cabinList.size();
    }

    public static class CabinHolder extends RecyclerView.ViewHolder {
        public TextView priceView, nameView, addressView;
        public ImageView mainImage, phoneButton, favoriteButton, locationButton;
        public ToggleButton heartButton;

        public CabinHolder(View view) {
            super(view);
            priceView = view.findViewById(R.id.cabin_price);
            nameView = view.findViewById(R.id.cabin_name);
            addressView = view.findViewById(R.id.cabin_address);
            mainImage = view.findViewById(R.id.cabin_image);
            phoneButton = view.findViewById(R.id.cabin_phone);
            favoriteButton = view.findViewById(R.id.cabin_favorite);
            heartButton = view.findViewById(R.id.cabin_heart);
            locationButton = view.findViewById(R.id.cabin_location);
        }


    }
}

