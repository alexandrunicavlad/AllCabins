package com.alexandrunica.allcabins.cabins.helper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerEventOnScollListener extends RecyclerView.OnScrollListener {
    public static String TAG = RecyclerEventOnScollListener.class.getSimpleName();

    private int current_page = 1;
    private boolean isLoading = true;

    private GridLayoutManager mLinearLayoutManager;

    public RecyclerEventOnScollListener(GridLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = mLinearLayoutManager.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

        //if (!isLoading) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= 10) {
                onLoadMore(2);
            }
     //   }
    }


    public abstract void onLoadMore(int current_page);
}