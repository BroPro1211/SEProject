package com.example.seproject.data_classes;

import com.google.firebase.database.Exclude;

public class Review {

    private String uid;
    private int starNum;
    private String reviewText;
    private long timeAdded;
    private String username;


    public Review(int starNum, String description){
        this.starNum = starNum;
        this.reviewText = description;

        uid = User.getCurrentUser().getUid();
        username = User.getCurrentUser().getUsername();
        timeAdded = System.currentTimeMillis();
    }

    @Exclude
    public boolean isCurrentUserReview(){
        return User.getCurrentUser().getUid().equals(uid);
    }


    public Review(){

    }
    public String getUid() {
        return uid;
    }

    public int getStarNum() {
        return starNum;
    }

    public String getReviewText() {
        return reviewText;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public String getUsername(){return username;}
}
