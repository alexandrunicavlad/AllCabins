package com.alexandrunica.allcabins.notification.model;

public class NotificationModel {

    private String body;
    private String id;
    private String type;

    public NotificationModel() {
    }

    public NotificationModel(String id, String body, String type) {
        this.body = body;
        this.id = id;
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
