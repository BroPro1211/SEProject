package com.example.seproject.book_lists.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.Toast;

import com.example.seproject.book_lists.ListFragmentAddDelete;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;

import java.util.List;


public class DeleteBookListDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private int deletedList;
    private List<BookList> bookLists;

    // parent fragment needs to notify the adapter
    public interface onReturnFromDeleteDialog{
        void listDeleted(int position);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        bookLists = User.getOrderedBookLists();
        String[] listDescriptors = bookLists.stream()
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
                User.getCurrentUser().deleteBookList(bookLists.get(deletedList).getListID(), deletedList);
                ((ListFragmentAddDelete)getParentFragment()).notifyAdapterItemRemoved(deletedList);
            }
        }
        else
            deletedList = id;
    }
}