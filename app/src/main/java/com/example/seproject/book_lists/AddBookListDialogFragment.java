package com.example.seproject.book_lists;

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

import com.example.seproject.R;
import com.example.seproject.User;

public class AddBookListDialogFragment extends DialogFragment {

    // parent fragment needs to notify the adapter
    public interface onReturnFromAddDialog{
        void listAdded();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
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
                            User.getCurrentUser().addBookList(name, description);

                            ((onReturnFromAddDialog)getParentFragment()).listAdded();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();

    }

}