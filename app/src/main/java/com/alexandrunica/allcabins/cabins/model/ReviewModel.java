package com.alexandrunica.allcabins.cabins.model;

public class ReviewModel {

    private String rating;
    private String details;
    private String from;
    private String cabin;
    private String date;

    public ReviewModel(String rating, String details, String from, String cabin, String date) {
        this.rating = rating;
        this.details = details;
        this.from = from;
        this.cabin = cabin;
        this.date = date;
    }

    public ReviewModel() {
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
