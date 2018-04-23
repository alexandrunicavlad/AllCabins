package com.alexandrunica.allcabins.map.event;

import com.alexandrunica.allcabins.cabins.model.Cabin;

import java.util.List;

public class OnGetCabinByIdEvent {

    private Cabin cabin;

    public OnGetCabinByIdEvent(Cabin cabin) {
        this.cabin = cabin;
    }

    public Cabin getCabin() {
        return cabin;
    }

    public void setCabin(Cabin cabin) {
        this.cabin = cabin;
    }
}
