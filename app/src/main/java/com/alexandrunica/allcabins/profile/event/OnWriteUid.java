package com.alexandrunica.allcabins.profile.event;

import com.alexandrunica.allcabins.profile.model.User;

public class OnWriteUid
{
    public User user;

    public OnWriteUid(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
