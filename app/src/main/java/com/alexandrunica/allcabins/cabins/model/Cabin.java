package com.alexandrunica.allcabins.cabins.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nica on 4/4/2018.
 */

public class Cabin implements Serializable {

    private String id;
    private String name;
    private String address;
    public double latitude;
    public double longitude;
    public String phone;
    public String email;
    public String price;
    public float rating;
    public List<String> photoUrl;
    public String details;
    public String idAdded;

    public HashMap<String, List<String>> reviews;
    public HashMap<String, List<String>> pictures;

    public Cabin() {

    }

    public Cabin(String id, String name, String address, double latitude, double longitude, String phone, String email, String price, float rating, List<String> photoUrl, String details, String idAdded, HashMap<String, List<String>> reviews, HashMap<String, List<String>> pictures) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.email = email;
        this.price = price;
        this.rating = rating;
        this.photoUrl = photoUrl;
        this.details = details;
        this.idAdded = idAdded;
        this.reviews = reviews;
        this.pictures = pictures;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIdAdded() {
        return idAdded;
    }

    public void setIdAdded(String idAdded) {
        this.idAdded = idAdded;
    }

    public HashMap<String, List<String>> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, List<String>> reviews) {
        this.reviews = reviews;
    }

    public HashMap<String, List<String>> getPictures() {
        return pictures;
    }

    public void setPictures(HashMap<String, List<String>> pictures) {
        this.pictures = pictures;
    }
}
