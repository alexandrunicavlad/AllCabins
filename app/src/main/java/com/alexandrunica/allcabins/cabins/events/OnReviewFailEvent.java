package com.alexandrunica.allcabins.cabins.events;

public class OnReviewFailEvent {

    public boolean isOk;

    public OnReviewFailEvent(boolean isOk) {
        this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }
}
