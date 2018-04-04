package com.alexandrunica.allcabins.favorite.adapter;

import android.content.Context;
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
    public void onBindViewHolder(CabinHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.priceView.getLayoutParams();
        if (params != null) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.priceView.setLayoutParams(params);
        }
    }
}
