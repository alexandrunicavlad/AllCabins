package com.alexandrunica.allcabins.favorite.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.alexandrunica.allcabins.cabins.adapter.CabinAdapter;
import com.alexandrunica.allcabins.cabins.model.Cabin;

import java.util.List;

/**
 * Created by Nica on 4/3/2018.
 */

public class FavoriteAdapter extends CabinAdapter {

    public FavoriteAdapter(Context context, List<Cabin> cabinList) {
        super(context, cabinList);
    }

    @Override
    public void onBindViewHolder(CabinHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.heartButton.setVisibility(View.GONE);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position);

            }
        });
    }
}
