package com.example.seproject.data_classes;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.seproject.MainActivity;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Class to store the information about a book's review
 */
public class Review {

    private String uid;
    private int starNum;
    private String reviewText;
    private long timeAdded;
    private String username;
    private Map<String, Boolean> userLikesMap;

    private Bitmap userProfileImage;

    /**
     * Initializes the review instance
     * @param starNum The number of stars in the review
     * @param description The description of the review
     */
    public Review(int starNum, String description){
        this.starNum = starNum;
        this.reviewText = description;

        uid = MainActivity.getCurrentUser().getUid();
        username = MainActivity.getCurrentUser().getUsername();
        timeAdded = System.currentTimeMillis();

        userLikesMap = new HashMap<>();
    }

    /**
     * Returns if this review was written by the currently logged in user
     * @return True if the currently logged in user wrote this review
     */
    @Exclude
    public boolean isCurrentUserReview(){
        return MainActivity.getCurrentUser().getUid().equals(uid);
    }

    /**
     * Returns the profile image of this review's user
     * @return The bitmap of the user's profile image
     */
    @Exclude
    public Bitmap getUserProfileImage() {
        return userProfileImage;
    }

    /**
     * Sets this review's user profile image
     * @param userProfileImage The bitmap of the user's profile image
     */
    @Exclude
    public void setUserProfileImage(Bitmap userProfileImage) {
        this.userProfileImage = userProfileImage;
    }




    // FB required constructor and getters

    /**
     * Empty constructor
     */
    public Review(){

    }

    /**
     * Returns the uid of the user who wrote this review
     * @return The user's uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the number of stars of this review
     * @return The number of stars
     */
    public int getStarNum() {
        return starNum;
    }

    /**
     * Returns the text of this review
     * @return The review text
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Returns the time this review was written
     * @return The time the review was written in milliseconds
     */
    public long getTimeAdded() {
        return timeAdded;
    }

    /**
     * Returns the username of the user who wrote this review
     * @return Returns the user's username
     */
    public String getUsername(){return username;}

    /**
     * Returns a map of the uid's of the users who liked this review
     * @return Returns the user map
     */
    @NonNull
    public Map<String, Boolean> getUserLikesMap(){
        if (userLikesMap == null)
            userLikesMap = new HashMap<>();
        return userLikesMap;
    }

    /**
     * Sets the user likes map
     * @param userLikesMap The user likes map to set
     */
    public void setUserLikesMap(Map<String, Boolean> userLikesMap) {
        this.userLikesMap = userLikesMap;
    }

}
