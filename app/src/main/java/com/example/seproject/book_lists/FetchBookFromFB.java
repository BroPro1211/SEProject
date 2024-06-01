package com.example.seproject.book_lists;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.FBref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public class FetchBookFromFB implements OnCompleteListener<DataSnapshot> {


    public static interface OnGetBook{
        abstract void onSuccess(Book book);
        abstract void onFailure();
    }

    public static class FailedToFetchBookException extends RuntimeException{}

    private OnGetBook parent;

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
