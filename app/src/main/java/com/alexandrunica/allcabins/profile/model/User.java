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

    public HashMap<String, String> favorites;

    public User () {

    }

    public User (String id, String email, String username, String profilePhoto, HashMap<String, String>  favoriteList) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.favorites = favoriteList;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getProfilePhoto () {
        return profilePhoto;
    }

    public void setProfilePhoto (String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public HashMap<String, String>  getFavoriteList () {
        return favorites;
    }

    public void setFavoriteList (HashMap<String, String>  favoriteList) {
        this.favorites = favoriteList;
    }
}
