package com.alexandrunica.allcabins.favorite.event;

import com.alexandrunica.allcabins.cabins.model.Cabin;

public class OnFavDone {

    private Cabin cabin;
    public OnFavDone(Cabin cabin) {
        this.cabin = cabin;
    }

    public Cabin getCabin() {
        return cabin;
    }

    public void setCabin(Cabin cabin) {
        this.cabin = cabin;
    }
}
