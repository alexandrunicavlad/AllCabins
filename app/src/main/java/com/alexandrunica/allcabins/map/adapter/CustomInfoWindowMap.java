package com.alexandrunica.allcabins.map.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowMap(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_window, null);
        Cabin cabin = (Cabin) marker.getTag();

        TextView name = view.findViewById(R.id.marker_name);
        TextView price = view.findViewById(R.id.marker_price);

        name.setText(cabin.getName());
        //price.setText(cabin.getPrice());
        return view;
    }
}
