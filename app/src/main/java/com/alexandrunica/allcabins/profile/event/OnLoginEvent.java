package com.alexandrunica.allcabins.profile.event;

/**
 * Created by Nica on 4/5/2018.
 */

public class OnLoginEvent {

    private boolean logged;
    private String email;
    public OnLoginEvent(boolean logged, String email) {
        this.email = email;
        this.logged = logged;
    }

    public boolean isLogged() {
        return logged;
    }

    public String getEmail(){
        return email;
    }
}

