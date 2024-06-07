package com.example.seproject.data_classes;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.seproject.MainActivity;
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

    private Bitmap userProfileImage;


    public Review(int starNum, String description){
        this.starNum = starNum;
        this.reviewText = description;

        uid = MainActivity.getCurrentUser().getUid();
        username = MainActivity.getCurrentUser().getUsername();
        timeAdded = System.currentTimeMillis();

        userLikesMap = new HashMap<>();
    }

    @Exclude
    public boolean isCurrentUserReview(){
        return MainActivity.getCurrentUser().getUid().equals(uid);
    }

    @Exclude
    public Bitmap getUserProfileImage() {
        return userProfileImage;
    }
    @Exclude
    public void setUserProfileImage(Bitmap userProfileImage) {
        this.userProfileImage = userProfileImage;
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
    @NonNull
    public Map<String, Boolean> getUserLikesMap(){
        if (userLikesMap == null)
            userLikesMap = new HashMap<>();
        return userLikesMap;
    }
    public void setUserLikesMap(Map<String, Boolean> userLikesMap) {
        this.userLikesMap = userLikesMap;
    }

}
