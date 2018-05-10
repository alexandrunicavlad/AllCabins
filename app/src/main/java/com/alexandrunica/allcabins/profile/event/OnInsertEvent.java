package com.alexandrunica.allcabins.profile.event;

import com.alexandrunica.allcabins.cabins.model.Cabin;

public class OnInsertEvent {

    private boolean succes;
    private Cabin cabin;

    public OnInsertEvent(boolean succes, Cabin cabin) {
        this.succes = succes;
        this.cabin = cabin;
    }


    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public Cabin getCabin() {
        return cabin;
    }

    public void setCabin(Cabin cabin) {
        this.cabin = cabin;
    }
}
