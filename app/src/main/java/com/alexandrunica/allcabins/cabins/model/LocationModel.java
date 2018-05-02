package com.alexandrunica.allcabins.cabins.model;

public class LocationModel {

    private String latitude;
    private String longitude;

    public LocationModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getDoubleLatitude() {
        return Double.parseDouble(latitude);
    }

    public double getDoubleLongitude() {
        return Double.parseDouble(longitude);
    }
}
