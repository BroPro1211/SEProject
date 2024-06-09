package com.example.seproject.book_lists.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.Toast;

import com.example.seproject.MainActivity;
import com.example.seproject.book_lists.BookListOverviewFragment;
import com.example.seproject.data_classes.Book;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Dialog fragment to delete a book
 */
public class DeleteBookDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String ARG_LIST_ID = "listID";

    private String listID;
    private int bookToDeletePos;

    /**
     * Creates a new instance of DeleteBookDialogFragment, passing the listID to delete from
     * @param listID The list to delete from
     * @return The new instance of DeleteBookDialogFragment
     */
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

        String[] booksDescriptors = MainActivity.getCurrentlyViewedListOfBooks().stream()
                .map(Book::getBookShortInfo)
                .toArray(String[]::new);

        bookToDeletePos = -1;
        builder.setTitle("Delete Book")
                .setSingleChoiceItems(booksDescriptors, -1, this)
                .setPositiveButton("Delete", this)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == DialogInterface.BUTTON_POSITIVE){
            if (bookToDeletePos == -1)
                Toast.makeText(getContext(), "Select book to delete", Toast.LENGTH_LONG).show();
            else {
                MainActivity.getCurrentUser().getBookLists().get(listID).deleteBook(bookToDeletePos);
                ((BookListOverviewFragment)getParentFragment()).notifyAdapterItemRemoved(bookToDeletePos);
            }
        }
        else
            bookToDeletePos = id;
    }
}