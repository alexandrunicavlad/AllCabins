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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.CabinInfoFragment;
import com.alexandrunica.allcabins.cabins.events.OnMoreEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;

import java.util.List;

public class CabinsSquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int FOOTER_VIEW = 1;

    private List<Cabin> cabinList;
    private Context context;
    private OnMoreEvent listener;
    private boolean isFiltred = false;


    public CabinsSquareAdapter(Context context, List<Cabin> cabinList, OnMoreEvent listener) {
        this.context = context;
        this.cabinList = cabinList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_cabins, parent, false);

            FooterViewHolder vh = new FooterViewHolder(v);

            return vh;
        }

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cabin_sqare_row, parent, false);

        CabinSquareHolder vh = new CabinSquareHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        try {
            if (vh instanceof CabinSquareHolder) {
                final CabinSquareHolder holder = (CabinSquareHolder) vh;

                final Cabin cabin = cabinList.get(position);
                holder.nameView.setText(cabin.getName());
                holder.priceView.setText(cabin.getPrice() + "/Noapte");
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
            } else if (vh instanceof FooterViewHolder) {

                FooterViewHolder holder = (FooterViewHolder) vh;
                holder.progressBar.setVisibility(View.GONE);
                if (isFiltred) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == cabinList.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return cabinList.size() + 1;
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.load_more_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    listener.onLoadMore();
                }
            });
        }
    }

    public void updateReceiptsList(List<Cabin> newlist) {
        cabinList.clear();
        cabinList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public void setFooter(boolean footerState) {
        isFiltred = footerState;
    }

    public class CabinSquareHolder extends RecyclerView.ViewHolder {
        public TextView priceView, nameView, phoneButton;
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


