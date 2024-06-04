package com.example.seproject.data_classes;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Review {

    private String uid;
    private int starNum;
    private String reviewText;
    private long timeAdded;
    private String username;

    private Map<String, Boolean> userLikesMap;


    public Review(int starNum, String description){
        this.starNum = starNum;
        this.reviewText = description;

        uid = User.getCurrentUser().getUid();
        username = User.getCurrentUser().getUsername();
        timeAdded = System.currentTimeMillis();

        userLikesMap = new HashMap<>();
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

    public Map<String, Boolean> getUserLikesMap(){
        if (userLikesMap == null)
            userLikesMap = new HashMap<>();
        return userLikesMap;
    }
    public void setUserLikesMap(Map<String, Boolean> userLikesMap) {
        this.userLikesMap = userLikesMap;
    }

}
