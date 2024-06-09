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
import com.example.seproject.book_lists.BookListsFragment;
import com.example.seproject.data_classes.BookList;


/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Dialog fragment to delete a book list
 */
public class DeleteBookListDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private int deletedList;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        String[] listDescriptors = MainActivity.getOrderedBookLists().stream()
                .map(BookList::getListShortInfo)
                .toArray(String[]::new);

        deletedList = -1;
        builder.setTitle("Delete Book List")
                .setSingleChoiceItems(listDescriptors, -1, this)
                .setPositiveButton("Delete", this)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == DialogInterface.BUTTON_POSITIVE){
            if (deletedList == -1)
                Toast.makeText(getContext(), "Select list to delete", Toast.LENGTH_LONG).show();
            else {
                MainActivity.getCurrentUser().deleteBookList(MainActivity.getOrderedBookLists().get(deletedList).getListID(), deletedList);
                ((BookListsFragment)getParentFragment()).notifyAdapterItemRemoved(deletedList);
            }
        }
        else
            deletedList = id;
    }
}