package com.example.seproject.data_classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {
    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static final DatabaseReference FBUsers = FBDB.getReference("Users");
    public static final DatabaseReference FBBooks = FBDB.getReference("Books");
    public static final String USER_BOOK_LISTS = "bookLists";
    public static final String USER_BOOKS = "books";
    public static final String BOOK_REVIEWS = "reviews";
    public static final String REVIEW_USER_LIKES_MAP = "userLikesMap";
    public static final FirebaseStorage FBStorage = FirebaseStorage.getInstance();
    public static final StorageReference FBBookImages = FBStorage.getReference("bookImages");
    public static final StorageReference FBUserImages = FBStorage.getReference("userImages");
    public static final String IMAGE_FILE_EXTENSION = ".jpeg";

}
