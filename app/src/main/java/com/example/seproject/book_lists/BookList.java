package com.example.seproject.book_lists;

import android.util.Log;

import com.google.firebase.database.Exclude;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public class BookList{
    private String listID;
    private String name;
    private String description;
    private List<String> books;


    public BookList(String listID, String name, String description){
        if (listID == null || name == null || description == null || name.length() == 0)
            throw new IllegalArgumentException("Illegal book list arguments");

        this.listID = listID;
        this.name = name;
        this.description = description;
        this.books = new ArrayList<>();
    }

    @Exclude
    public String getShortenedName(){
        if (name.length() <= 15)
            return name;
        return name.substring(0, 15) + "...";
    }

    @Exclude
    public String getListShortInfo(){
        return getShortenedName() + "\n" + getBookCount() + " books";
    }

    @Exclude
    public int getBookCount(){
        return books.size();
    }

    // FB required constructor and getters
    public BookList(){}
    public String getListID() {
        return listID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public List<String> getBooks(){
        return books;
    }

}
