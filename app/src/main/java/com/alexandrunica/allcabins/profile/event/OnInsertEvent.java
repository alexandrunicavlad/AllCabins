package com.alexandrunica.allcabins.profile.event;

public class OnInsertEvent {

    private boolean succes;

    public OnInsertEvent(boolean succes) {
        this.succes = succes;
    }


    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }
}
