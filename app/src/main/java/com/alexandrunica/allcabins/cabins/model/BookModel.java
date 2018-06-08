package com.alexandrunica.allcabins.cabins.model;

public class BookModel {

    private String from;
    private String to;
    private String status;
    private String date;
    private String message;

    public BookModel() {

    }

    public BookModel(String from, String to, String status, String date, String message) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
