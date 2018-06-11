package com.alexandrunica.allcabins.notification.event;

import com.alexandrunica.allcabins.profile.model.User;

public class OnUserGetById {

    private User user;

    public OnUserGetById(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
