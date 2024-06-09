package com.example.seproject.book_lists.dialog_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.BookListsFragment;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Dialog fragment to add a book list
 */
public class AddBookListDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_book_list_dialog, null);

        builder.setView(view)
                .setTitle("Add Book List")
                .setPositiveButton("Add list", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText listNameET = view.findViewById(R.id.listNameET);
                        EditText listDescriptionET = view.findViewById(R.id.listDescriptionET);

                        String name = listNameET.getText().toString();
                        if (name.length() == 0)
                            Toast.makeText(getContext(), "Enter list name to proceed", Toast.LENGTH_LONG).show();
                        else {
                            String description = listDescriptionET.getText().toString();
                            MainActivity.getCurrentUser().addBookList(name, description);

                            ((BookListsFragment)getParentFragment()).notifyAdapterItemInserted(-1);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();

    }

}