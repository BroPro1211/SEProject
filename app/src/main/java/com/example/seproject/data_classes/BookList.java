package com.example.seproject.data_classes;

import com.example.seproject.book_lists.EnsureFieldNotNull;
import com.example.seproject.data_classes.Book;
import com.google.firebase.database.Exclude;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that saves BookList data. The class ensures that the list of saved books is not null.
 */
public class BookList extends EnsureFieldNotNull<List<String>> {

    @Override
    public List<String> initField() {
        return new ArrayList<>();
    }

    private String listID;
    private String name;
    private String description;
    private List<Book> orderedBooks;



    public BookList(String listID, String name, String description){
        if (listID == null || name == null || description == null || listID.length() == 0 || name.length() == 0)
            throw new IllegalArgumentException("Illegal book list arguments");

        this.listID = listID;
        this.name = name;
        this.description = description;
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
        return getBooks().size();
    }

    public void addBook(Book book){

    }

    public void createOrderedBooks(){
        // TODO:FIXXX
        orderedBooks = new ArrayList<>();
        /*
        orderedBooks = getBooks().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());*/
    }
    @Exclude
    public List<Book> getOrderedBooks(){
        return orderedBooks;
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
        return getField();
    }

}
