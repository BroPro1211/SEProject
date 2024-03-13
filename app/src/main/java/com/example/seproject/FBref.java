package com.example.seproject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {
    public static final String USERS_FOLDER_PATH = "userImages/";

    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static final DatabaseReference refMessages = FBDB.getReference("Messages");
    public static final DatabaseReference refUsers = FBDB.getReference("Users");
    public static final FirebaseStorage FBStorage = FirebaseStorage.getInstance();
    public static final StorageReference refStorage = FBStorage.getReference();
    public static final FirebaseAuth FBAuth = FirebaseAuth.getInstance();
}
