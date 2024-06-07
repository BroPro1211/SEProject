package com.example.seproject.book_lists;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.FBref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

/**
 * Class to get book information from firebase
 */
public class FetchBookFromFB implements OnCompleteListener<DataSnapshot> {

    /**
     * Interface to call when finished getting the book info
     */
    public interface OnGetBook{
        /**
         * Method to call on success
         * @param book Book from FB
         */
        void onSuccess(Book book);

        /**
         * Method to call on failure
         */
        void onFailure();
    }


    private final OnGetBook parent;

    public FetchBookFromFB(OnGetBook parent){
        this.parent = parent;

    }

    public void getBookFromFB(String bookID){
        FBref.FBBooks.child(bookID).get().addOnCompleteListener(this);
        Log.d("SEProject", "Retrieving book info from FB: id " + bookID);
    }

    @Override
    public void onComplete(@NonNull Task<DataSnapshot> task) {
        if (task.isSuccessful()) {
            Log.d("SEProject", "Successfully retrieved book info from FB");

            Book FBBook = task.getResult().getValue(Book.class);

            parent.onSuccess(FBBook);
        }
        else {
            Log.d("SEProject", "Failed to retrieve book info from FB", task.getException());

            parent.onFailure();

        }
    }


}
