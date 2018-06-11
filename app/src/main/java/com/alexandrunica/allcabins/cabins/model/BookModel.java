package com.alexandrunica.allcabins.cabins.model;

public class BookModel {

    private String from;
    private String to;
    private String status;
    private String date;
    private String message;
    private String cabin;
    private String cabinName;
    private String id;
    private String bookName;

    public BookModel() {

    }

    public BookModel(String from, String to, String status, String date, String message, String cabin, String cabinName, String id, String bookName) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
        this.message = message;
        this.cabin = cabin;
        this.cabinName = cabinName;
        this.id = id;
        this.bookName = bookName;
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

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public String getCabinName() {
        return cabinName;
    }

    public void setCabinName(String cabinName) {
        this.cabinName = cabinName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
