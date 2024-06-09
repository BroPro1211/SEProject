package com.example.seproject.data_classes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Class to store the firebase RTDB, storage and authentication of this application
 */
public class FBref {
    /**
     * The firebase authentication instance
     */
    public static final FirebaseAuth FBAuth = FirebaseAuth.getInstance();
    /**
     * The firebase realtime database instance
     */
    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    /**
     * The firebase database users sub tree
     */
    public static final DatabaseReference FBUsers = FBDB.getReference("Users");
    /**
     * The firebase database books sub tree
     */
    public static final DatabaseReference FBBooks = FBDB.getReference("Books");
    /**
     * The firebase database name of a user's book lists
     */
    public static final String USER_BOOK_LISTS = "bookLists";
    /**
     * The firebase database name of a book list's books
     */
    public static final String USER_BOOKS = "books";
    /**
     * The firebase database name of a book's review
     */
    public static final String BOOK_REVIEWS = "reviews";
    /**
     * The firebase database name of a review's map of users who liked the review
     */
    public static final String REVIEW_USER_LIKES_MAP = "userLikesMap";
    /**
     * The firebase storage instance
     */
    public static final FirebaseStorage FBStorage = FirebaseStorage.getInstance();
    /**
     * The firebase storage book images folder
     */
    public static final StorageReference FBBookImages = FBStorage.getReference("bookImages");
    /**
     * The firebase storage user images folder
     */
    public static final StorageReference FBUserImages = FBStorage.getReference("userImages");
    /**
     * The firebase storage image extension
     */
    public static final String IMAGE_FILE_EXTENSION = ".jpeg";
    /**
     * Maximum size of a picture that can be downloaded from FB storage
     */
    public static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;

}
