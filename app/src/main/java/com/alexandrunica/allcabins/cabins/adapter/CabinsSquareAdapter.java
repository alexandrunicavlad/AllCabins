package com.alexandrunica.allcabins.cabins.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.CabinInfoFragment;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;

import java.util.List;

public class CabinsSquareAdapter extends RecyclerView.Adapter<CabinsSquareAdapter.CabinSquareHolder> {


    private List<Cabin> cabinList;
    private Context context;


    public CabinsSquareAdapter(Context context, List<Cabin> cabinList) {
        this.context = context;
        this.cabinList = cabinList;
    }

    @Override
    public CabinsSquareAdapter.CabinSquareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cabin_sqare_row, parent, false);
        return new CabinsSquareAdapter.CabinSquareHolder(v);
    }

    @Override
    public void onBindViewHolder(final CabinsSquareAdapter.CabinSquareHolder holder, int position) {
        final Cabin cabin = cabinList.get(position);
        holder.nameView.setText(cabin.getName());
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                DialogFragment newFragment = CabinInfoFragment.newInstance(cabin);
                newFragment.show(ft, "dialog");
            }
        });


    }

    @Override
    public int getItemCount() {
        return cabinList.size();
    }

    public static class CabinSquareHolder extends RecyclerView.ViewHolder {
        public TextView priceView, nameView,phoneButton;
        public ImageView mainImage;
        public ToggleButton heartButton;

        public CabinSquareHolder(View view) {
            super(view);
            priceView = view.findViewById(R.id.cabin_price);
            nameView = view.findViewById(R.id.cabin_name);
            mainImage = view.findViewById(R.id.cabin_image);
            phoneButton = view.findViewById(R.id.cabin_phone);
            heartButton = view.findViewById(R.id.cabin_heart);
        }


    }
}


