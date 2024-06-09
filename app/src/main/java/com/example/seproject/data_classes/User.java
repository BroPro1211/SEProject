package com.example.seproject.data_classes;


import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.seproject.MainActivity;
import com.google.firebase.database.Exclude;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Class to store the information about a user
 */
public class User {
    private String uid;
    private String username;
    private Map<String, BookList> bookLists;

    private Bitmap profileImage;

    /**
     * Initializes the user instance
     * @param uid The user's uid
     * @param username The user's username
     */
    public User(String uid, String username){
        if (uid == null || username == null || uid.length() == 0 || username.length() == 0)
            throw new IllegalArgumentException("Illegal user arguments " + uid + ", " + username);

        this.uid = uid;
        this.username = username;
        // LinkedHashMap is a Map implementation that preserves the insertion order when adding and removing entries
        bookLists = new LinkedHashMap<>();

        // adds the default book lists
        addBookList("Reading", "Books I'm reading");
        addBookList("Read", "Books I've read");
        addBookList("Wishlist", "Books I want to read");

    }

    /**
     * Adds book list to user
     * @param name Name of new list
     * @param description Description of new list
     */
    public void addBookList(String name, String description){
        String key = FBref.FBUsers.child(uid).child(FBref.USER_BOOK_LISTS).push().getKey();

        BookList newBookList = new BookList(key, name, description);

        bookLists.put(key, newBookList);

        MainActivity.getOrderedBookLists().add(newBookList);
        Log.d("SEProject", "Added book list at position " + (MainActivity.getOrderedBookLists().size()-1));


        FBref.FBUsers.child(uid).child(FBref.USER_BOOK_LISTS).child(key).setValue(newBookList);

        Log.d("SEProject", "Added book list " + key + " to user " + uid);

    }

    /**
     * Deletes book list from user
     * @param id Id of book list to delete
     * @param position Position of book list in the ordered list
     */
    public void deleteBookList(String id, int position){
        MainActivity.getOrderedBookLists().remove(position);
        Log.d("SEProject", "Deleted book list at position " + position);

        bookLists.remove(id);

        if (bookLists.size() == 0)
            addBookList("New List", "Add books to me!");

        FBref.FBUsers.child(uid).child(FBref.USER_BOOK_LISTS).child(id).removeValue();

        Log.d("SEProject", "Deleted book list " + id + " from user " + uid);


    }

    /**
     * Returns the user's profile image
     * @return The bitmap of the profile image, and null if doesn't exist
     */
    @Exclude
    public Bitmap getProfileImage(){
        return profileImage;
    }

    /**
     * Sets the user's profile image
     * @param image Bitmap of the profile image
     */
    @Exclude
    public void setProfileImage(Bitmap image){
        profileImage = image;
    }



    // FB required constructor and getters

    /**
     * Empty constructor
     */
    public User(){}

    /**
     * Returns the user uid
     * @return User uid
     */
    public String getUid(){
        return uid;
    }

    /**
     * Returns the user's username
     * @return User's username
     */
    public String getUsername(){
        return username;
    }

    /**
     * Returns the map of user's book lists. If empty, initializes it to an empty map.
     * @return User's map of book lists
     */
    @NonNull
    public Map<String, BookList> getBookLists(){
        if (bookLists == null)
            bookLists = new LinkedHashMap<>();
        return bookLists;
    }

}
