package com.alexandrunica.allcabins.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.BookModel;
import com.alexandrunica.allcabins.cabins.model.DateModel;
import com.alexandrunica.allcabins.notification.Activities.NotificationActivity;
import com.alexandrunica.allcabins.notification.Activities.NotificationListActivity;
import com.google.gson.Gson;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookHolder> {


    private List<BookModel> bookModelList;
    private Context context;


    public BookingAdapter(Context context, List<BookModel> bookModelList) {
        this.context = context;
        this.bookModelList = bookModelList;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_row, parent, false);


        return new BookHolder(v);
    }

    @Override
    public void onBindViewHolder(final BookHolder holder, int position) {
        final BookModel booking = bookModelList.get(position);
        DateModel dateModel = new Gson().fromJson(booking.getDate(), DateModel.class);
        holder.nameView.setText(context.getResources().getString(R.string.notification_trip, booking.getCabinName()));
        holder.dateView.setText(context.getResources().getString(R.string.book_date_time,dateModel.getStart(), dateModel.getEnd()));
        holder.statusView.setText(booking.getStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifyIntent = new Intent(context, NotificationActivity.class);
                notifyIntent.putExtra("notification", booking.getId());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                final String id = preferences.getString("uid", "");
                if (id.equals(booking.getTo())) {
                    notifyIntent.putExtra("type", "host");
                } else if(id.equals(booking.getFrom())) {
                    notifyIntent.putExtra("type", "guest");
                } else {
                    return;
                }
                context.startActivity(notifyIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookModelList.size();
    }

    public static class BookHolder extends RecyclerView.ViewHolder {
        public TextView dateView, nameView, statusView;

        public BookHolder(View view) {
            super(view);
            dateView = view.findViewById(R.id.booking_date);
            nameView = view.findViewById(R.id.booking_name);
            statusView = view.findViewById(R.id.booking_status);
        }
    }
}

