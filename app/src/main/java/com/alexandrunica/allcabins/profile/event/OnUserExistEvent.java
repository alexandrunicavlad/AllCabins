package com.alexandrunica.allcabins.profile.event;

public class OnUserExistEvent {

    private boolean exist;

    public OnUserExistEvent(boolean exist) {
        this.exist = exist;
    }

    public boolean isExist() {
        return exist;
    }
}

