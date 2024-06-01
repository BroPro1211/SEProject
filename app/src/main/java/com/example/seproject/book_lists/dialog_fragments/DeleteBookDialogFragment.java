package com.example.seproject.book_lists.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.seproject.R;
import com.example.seproject.book_lists.BookListOverviewFragment;
import com.example.seproject.book_lists.ListFragmentAddDelete;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;


public class DeleteBookDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ARG_LIST_ID = "listID";

    private String listID;
    private int deletedBook;

    public static DeleteBookDialogFragment newInstance(String listID) {
        DeleteBookDialogFragment fragment = new DeleteBookDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ID, listID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            listID = getArguments().getString(ARG_LIST_ID);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        String[] booksDescriptors = User.getCurrentlyViewedListOfBooks().stream()
                .map(Book::getBookShortInfo)
                .toArray(String[]::new);

        deletedBook = -1;
        builder.setTitle("Delete Book")
                .setSingleChoiceItems(booksDescriptors, -1, this)
                .setPositiveButton("Delete", this)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == DialogInterface.BUTTON_POSITIVE){
            if (deletedBook == -1)
                Toast.makeText(getContext(), "Select book to delete", Toast.LENGTH_LONG).show();
            else {
                User.getCurrentUser().getBookLists().get(listID).deleteBook(deletedBook);
                ((BookListOverviewFragment)getParentFragment()).notifyAdapterItemRemoved(deletedBook);
            }
        }
        else
            deletedBook = id;
    }
}