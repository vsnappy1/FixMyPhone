package com.vaaq.fixmyphone.models;

public class RateAndReview {
    String userName;
    String review;
    long    starCount;
    long   time;

    public RateAndReview(String userName, String review, long starCount, long time) {
        this.userName = userName;
        this.review = review;
        this.starCount = starCount;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public String getReview() {
        return review;
    }

    public long getStarCount() {
        return starCount;
    }

    public long getTime() {
        return time;
    }
}
