package com.alexandrunica.allcabins.profile.event;

import com.google.firebase.auth.FirebaseUser;

public class OnSocialLoginEvent {


    private boolean logged;
    private FirebaseUser firebaseUser;

    public OnSocialLoginEvent(boolean logged, FirebaseUser firebaseUser) {
        this.logged = logged;
        this.firebaseUser = firebaseUser;
    }

    public boolean isLogged() {
        return logged;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }
}

