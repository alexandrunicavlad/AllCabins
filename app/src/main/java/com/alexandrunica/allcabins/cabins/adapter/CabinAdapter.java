package com.alexandrunica.allcabins.cabins.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import com.alexandrunica.allcabins.cabins.CabinInfoFragment;
import com.alexandrunica.allcabins.cabins.activities.CabinInfoAcitivty;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

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
        CurrencyConverter currencyConverter = new CurrencyConverter(context);
        holder.priceView.setText(currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency()+ "/" + context.getResources().getString(R.string.night));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
//                DialogFragment newFragment = CabinInfoFragment.newInstance(cabin);
//                newFragment.show(ft, "dialog");
                Intent intent = new Intent(context, CabinInfoAcitivty.class);
                intent.putExtra("cabin",cabin.getId());
                context.startActivity(intent);
            }
        });

        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationModel locationModel = new Gson().fromJson(cabin.getLocation(), new TypeToken<LocationModel>() {
                }.getType());
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(locationModel.getLatitude()), Double.parseDouble(locationModel.getLongitude()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cabinList.size();
    }

    public static class CabinHolder extends RecyclerView.ViewHolder {
        public TextView priceView, nameView, addressView;
        public ImageView mainImage, phoneButton, locationButton, deleteButton;
        public ToggleButton heartButton;

        public CabinHolder(View view) {
            super(view);
            priceView = view.findViewById(R.id.cabin_price);
            nameView = view.findViewById(R.id.cabin_name);
            addressView = view.findViewById(R.id.cabin_address);
            mainImage = view.findViewById(R.id.cabin_image);
            phoneButton = view.findViewById(R.id.cabin_phone);
            heartButton = view.findViewById(R.id.cabin_heart);
            locationButton = view.findViewById(R.id.cabin_location);
            deleteButton = view.findViewById(R.id.cabin_delete);
        }


    }

    public void removeAt(int position) {
        final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String id = preferences.getString("uid", "");
        profileOperations.removeFavorite(id, cabinList.get(position).getId());
        cabinList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cabinList.size());
    }
}

