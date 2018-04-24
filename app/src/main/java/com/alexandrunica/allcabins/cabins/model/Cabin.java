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
    private String location;
    private String state;
    private String city;
    private String country;
    public String phone;
    public String email;
    public String price;
    public float rating;
    public String thumbPhotoUrl;
    public String description;
    public String facilities;
    public String idAdded;

    public HashMap<String, String> uploadedPictures;
    public HashMap<String, String> reviews;
    public HashMap<String, String> pictures;

    public Cabin() {
    }

    public Cabin(String id, String name, String address, String location, String state, String city, String country, String phone, String email, String price, float rating, String thumbPhotoUrl, String description, String facilities, String idAdded, HashMap<String, String> uploadedPictures, HashMap<String, String> reviews, HashMap<String, String> pictures) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
        this.state = state;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.email = email;
        this.price = price;
        this.rating = rating;
        this.thumbPhotoUrl = thumbPhotoUrl;
        this.description = description;
        this.facilities = facilities;
        this.idAdded = idAdded;
        this.uploadedPictures = uploadedPictures;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getThumbPhotoUrl() {
        return thumbPhotoUrl;
    }

    public void setThumbPhotoUrl(String thumbPhotoUrl) {
        this.thumbPhotoUrl = thumbPhotoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getIdAdded() {
        return idAdded;
    }

    public void setIdAdded(String idAdded) {
        this.idAdded = idAdded;
    }

    public HashMap<String, String> getUploadedPictures() {
        return uploadedPictures;
    }

    public void setUploadedPictures(HashMap<String, String> uploadedPictures) {
        this.uploadedPictures = uploadedPictures;
    }

    public HashMap<String, String> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, String> reviews) {
        this.reviews = reviews;
    }

    public HashMap<String, String> getPictures() {
        return pictures;
    }

    public void setPictures(HashMap<String, String> pictures) {
        this.pictures = pictures;
    }
}