package com.alexandrunica.allcabins.profile.model;

import java.io.Serializable;

public class UserAddressModel implements Serializable {

    public String street;

    public String city;

    public String state;

    public UserAddressModel() {
    }

    public UserAddressModel(String street, String city, String state) {
        this.street = street;
        this.city = city;
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}