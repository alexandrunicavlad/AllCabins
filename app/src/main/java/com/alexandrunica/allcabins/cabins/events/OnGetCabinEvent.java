package com.alexandrunica.allcabins.cabins.events;

import com.alexandrunica.allcabins.cabins.model.Cabin;

import java.util.List;

/**
 * Created by Nica on 4/4/2018.
 */

public class OnGetCabinEvent {

    private List<Cabin> cabins;

    public OnGetCabinEvent(List<Cabin> cabins) {
        this.cabins = cabins;
    }

    public List<Cabin> getCabins() {
        return cabins;
    }

    public void setCabins(List<Cabin> cabins) {
        this.cabins = cabins;
    }
}
