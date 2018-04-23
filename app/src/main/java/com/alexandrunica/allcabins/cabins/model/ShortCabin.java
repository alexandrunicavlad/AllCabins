package com.alexandrunica.allcabins.cabins.model;

import java.io.Serializable;

public class ShortCabin implements Serializable {

    private String id;
    private String location;
    private String region;

    public ShortCabin(String id, String location, String region) {
        this.id = id;
        this.location = location;
        this.region = region;
    }

    public ShortCabin() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}