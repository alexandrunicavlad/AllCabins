package com.alexandrunica.allcabins.profile.event;

public class OnWriteUid
{
    public String id;

    public OnWriteUid(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
