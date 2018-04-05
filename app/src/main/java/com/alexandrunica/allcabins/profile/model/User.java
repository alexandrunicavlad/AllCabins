package com.alexandrunica.allcabins.profile.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nica on 4/5/2018.
 */

public class User {

    public String id;

    public String email;

    public String username;

    public String profilePhoto;

    public HashMap<String, List<String>> favoriteList;

    public User () {

    }

    public User (String id, String email, String username, String profilePhoto, HashMap<String, List<String>>  favoriteList) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.favoriteList = favoriteList;
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

    public HashMap<String, List<String>>  getFavoriteList () {
        return favoriteList;
    }

    public void setFavoriteList (HashMap<String, List<String>>  favoriteList) {
        this.favoriteList = favoriteList;
    }
}
