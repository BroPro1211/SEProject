package com.example.seproject;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String uid;
    public List<BookList> booklists;

    public User(String uid){
        this.uid = uid;
        this.booklists = new ArrayList<>();
    }
}
