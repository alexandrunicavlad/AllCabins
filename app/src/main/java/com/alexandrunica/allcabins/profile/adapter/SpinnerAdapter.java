package com.alexandrunica.allcabins.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;

public class SpinnerAdapter extends BaseAdapter {
    LayoutInflater inflator;
    String[] mCounting;

    public SpinnerAdapter(Context context, String[] counting) {
        inflator = LayoutInflater.from(context);
        mCounting = counting;
    }

    @Override
    public int getCount() {
        return mCounting.length;
    }

    @Override
    public Object getItem(int position) {
        return mCounting[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflator.inflate(R.layout.spinner_item, null);
        TextView tv = convertView.findViewById(R.id.text);
        ImageView image = convertView.findViewById(R.id.image);
        Context context = image.getContext();
        int id = context.getResources().getIdentifier(mCounting[position].toLowerCase(), "drawable", context.getPackageName());
        if (id != 0 && id != -1) {
            image.setImageResource(id);
        }
        tv.setText(mCounting[position]);
        return convertView;
    }
}
