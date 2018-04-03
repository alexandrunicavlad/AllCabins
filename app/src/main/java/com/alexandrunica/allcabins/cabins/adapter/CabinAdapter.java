package com.alexandrunica.allcabins.cabins.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;

import java.util.List;

/**
 * Created by Nica on 4/3/2018.
 */

public class CabinAdapter extends RecyclerView.Adapter<CabinAdapter.CabinHolder>{


    private List<String> tripList;
    private Context context;


    public CabinAdapter(Context context, List<String> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @Override
    public CabinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cabin_row, parent, false);
        return new CabinHolder(v);
    }

    @Override
    public void onBindViewHolder(final CabinHolder holder, int position) {
        final String trip = tripList.get(position);

        holder.nameView.setText(trip);
        holder.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.favoriteButton.setImageResource(R.drawable.ic_star);
                holder.favoriteButton.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.yellow_color), PorterDuff.Mode.SRC_IN);
            }
        });


    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class CabinHolder extends RecyclerView.ViewHolder {
        public TextView priceView, nameView, addressView;
        public ImageView mainImage, phoneButton, favoriteButton, heartButton, locationButton ;

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

