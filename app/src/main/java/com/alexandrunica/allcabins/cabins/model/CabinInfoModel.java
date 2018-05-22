package com.alexandrunica.allcabins.cabins.model;

public class CabinInfoModel {

    private String guests;
    private String beds;
    private String bedrooms;
    private String bath;

    public CabinInfoModel(String guests, String beds, String bedrooms, String bath) {
        this.guests = guests;
        this.beds = beds;
        this.bedrooms = bedrooms;
        this.bath = bath;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getBath() {
        return bath;
    }

    public void setBath(String bath) {
        this.bath = bath;
    }
}
