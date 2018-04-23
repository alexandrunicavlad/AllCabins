package com.alexandrunica.allcabins.map.event;

import com.alexandrunica.allcabins.cabins.model.ShortCabin;

import java.util.List;

public class OnGetShortCabinEvent {

    private List<ShortCabin> cabins;

    public OnGetShortCabinEvent(List<ShortCabin> cabins) {
        this.cabins = cabins;
    }

    public List<ShortCabin> getCabins() {
        return cabins;
    }

    public void setCabins(List<ShortCabin> cabins) {
        this.cabins = cabins;
    }
}

