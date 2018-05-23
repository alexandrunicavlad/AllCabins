package com.alexandrunica.allcabins.cabins.events;

import com.alexandrunica.allcabins.cabins.model.Cabin;

public interface OnCallClickEvent {
    void onCall(Cabin cabin);
}
