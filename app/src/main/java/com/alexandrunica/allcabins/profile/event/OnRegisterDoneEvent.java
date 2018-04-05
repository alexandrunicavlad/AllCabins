package com.alexandrunica.allcabins.profile.event;

/**
 * Created by Nica on 4/5/2018.
 */

public class OnRegisterDoneEvent {

    private boolean registered;
    private boolean existEmail;
    private String email;
    private String name;

    public OnRegisterDoneEvent(boolean registered, String email, String name){
        this.registered = registered;
        existEmail = false;
        this.email = email;
        this.name = name;
    }
    public OnRegisterDoneEvent(boolean registered, boolean existEmail, String email, String name){
        this.registered = registered;
        this.existEmail = existEmail;
        this.email = email;
        this.name = name;
    }

    public boolean isRegistered(){
        return registered;
    }

    public boolean isExistEmail(){
        return existEmail;
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }
}
