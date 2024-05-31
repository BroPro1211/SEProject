package com.example.seproject.data_classes;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User {
    private static User loggedInUser;
    private String uid;
    private String username;
    private Map<String, BookList> bookLists;
    private static List<BookList> orderedBookLists;
    // an ordering of the signed in user's book lists for the recycler view and delete list dialog

    private static List<Book> currentlyViewedListOfBooks;
    // an ordering of the current list of books that is being viewed, for the book list overview page
    // and book search page

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

    public void addBookList(String name, String description){
        String key = FBref.FBUsers.child(uid).child("bookLists").push().getKey();

        BookList newBookList = new BookList(key, name, description);

        bookLists.put(key, newBookList);

        orderedBookLists.add(newBookList);
        Log.d("SEProject", "Added list at position " + (orderedBookLists.size()-1));

        FBref.FBUsers.child(uid).child("bookLists").child(key).setValue(newBookList);

        Log.d("SEProject", "Added book list " + key + " to user " + uid);

    }

    public void deleteBookList(String id, int position){
        orderedBookLists.remove(position);
        Log.d("SEProject", "Deleted list at position " + position);

        bookLists.remove(id);

        if (bookLists.size() == 0)
            addBookList("New List", "Add books to me!");

        FBref.FBUsers.child(uid).child("bookLists").child(id).removeValue();

        Log.d("SEProject", "Deleted book list " + id + " from user " + uid);


    }

    @Exclude
    public int getBookListCount(){
        return bookLists.size();
    }

    public static void setCurrentUser(User user){
        loggedInUser = user;
    }
    public static User getCurrentUser(){
        return loggedInUser;
    }
    public static void checkCurrentUser(){
        if (loggedInUser == null)
            throw new RuntimeException("No logged in user found");
    }
    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
        setCurrentUser(null);
    }

    public static void setCurrentlyViewedListOfBooks(List<Book> listOfBooks){
        currentlyViewedListOfBooks = listOfBooks;
    }
    public static List<Book> getCurrentlyViewedListOfBooks(){
        return currentlyViewedListOfBooks;
    }


    public static void createOrderedBookLists(){
        orderedBookLists = User.getCurrentUser().bookLists.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public static List<BookList> getOrderedBookLists(){
        return orderedBookLists;
    }


    // FB required constructor and getters
    public User(){}
    public String getUid(){
        return uid;
    }
    public String getUsername(){
        return username;
    }
    public Map<String, BookList> getBookLists(){
        return bookLists;
    }

}