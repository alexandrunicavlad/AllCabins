package com.alexandrunica.allcabins.profile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nica on 4/5/2018.
 */

public class User implements Serializable {

    public String id;

    public String email;

    public String username;

    public String profilePhoto;

    public String phone;

    public UserAddressModel addressModel;

    public HashMap<String, String> favorites;

    public User () {

    }

    public User(String id, String email, String username, String profilePhoto, String phone, UserAddressModel addressModel, HashMap<String, String> favorites) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.addressModel = addressModel;
        this.favorites = favorites;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserAddressModel getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(UserAddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public HashMap<String, String> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, String> favorites) {
        this.favorites = favorites;
    }
}
