package com.example.seproject.book_lists.dialog_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.seproject.R;
import com.example.seproject.book_lists.BookListsFragment;
import com.example.seproject.data_classes.FBref;
import com.example.seproject.data_classes.Review;
import com.example.seproject.data_classes.User;


public class AddReviewDialogFragment extends DialogFragment {

    public static final String ARG_BOOK_ID = "bookID";

    private String bookID;

    public AddReviewDialogFragment() {
        // Required empty public constructor
    }


    public static AddReviewDialogFragment newInstance(String bookID) {
        AddReviewDialogFragment fragment = new AddReviewDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookID = getArguments().getString(ARG_BOOK_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_review_dialog, null);

        builder.setView(view)
                .setTitle("Add Review")
                .setPositiveButton("Add review", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RatingBar reviewRatingBar = view.findViewById(R.id.reviewRatingBar);
                        EditText reviewDescriptionET = view.findViewById(R.id.reviewDescriptionET);

                        float rating = reviewRatingBar.getRating();
                        if (rating == 0)
                            Toast.makeText(getContext(), "Choose rating to proceed", Toast.LENGTH_LONG).show();
                        else {
                            String description = reviewDescriptionET.getText().toString();
                            Review review = new Review((int) rating, description);

                            FBref.FBBooks.child(bookID).child(FBref.BOOK_REVIEWS)
                                    .child(User.getCurrentUser().getUid()).setValue(review);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();

    }
}