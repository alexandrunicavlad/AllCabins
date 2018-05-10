package com.alexandrunica.allcabins.cabins.events;

import com.alexandrunica.allcabins.cabins.model.ReviewModel;

public class OnReviewAddEvent {

    private ReviewModel reviewModel;
    private String id;


    public OnReviewAddEvent(String id, ReviewModel reviewModel) {
        this.reviewModel = reviewModel;
        this.id = id;
    }

    public ReviewModel getReviewModel() {
        return reviewModel;
    }

    public String getId() {
        return id;
    }
}
