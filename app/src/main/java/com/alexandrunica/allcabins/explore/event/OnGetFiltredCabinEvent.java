package com.alexandrunica.allcabins.explore.event;

import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;

import java.util.List;

public class OnGetFiltredCabinEvent {

    private List<Cabin> cabins;
    private String city;

    public OnGetFiltredCabinEvent(List<Cabin> cabins, String city) {
        this.cabins = cabins;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Cabin> getCabins() {
        return cabins;
    }

    public void setCabins(List<Cabin> cabins) {
        this.cabins = cabins;
    }
}
