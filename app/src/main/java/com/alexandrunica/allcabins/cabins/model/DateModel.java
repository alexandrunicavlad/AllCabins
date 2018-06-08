package com.alexandrunica.allcabins.cabins.model;

public class DateModel {

    private String start;
    private String end;

    public DateModel(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public DateModel() {

    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
