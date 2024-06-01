package com.example.seproject.book_lists;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seproject.R;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.FBref;
import com.example.seproject.data_classes.Review;
import com.example.seproject.data_classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BookDetailsFragment extends Fragment implements View.OnClickListener,FetchBookFromFB.OnGetBook {

    public static final String ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST = "book pos in currently viewed list";
    public static final String ARG_LIST_ID = "list id";

    private int bookPos;
    private Book book;
    private boolean alreadyInFB;

    private String listID; // if adding book, stores the list id to add the book to, else null
    private BookList bookList;



    private ProgressBar progressBarReviews;
    private ConstraintLayout reviewsLayout;
    private ProgressBar progressBarFAB;
    private FloatingActionButton bookDetailsFAB;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookPos = getArguments().getInt(ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST);
            book = User.getCurrentlyViewedListOfBooks().get(bookPos);
            listID = getArguments().getString(ARG_LIST_ID);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        if (book == null)
            throw new RuntimeException("Attempted to open book details dialog without book");

        fillInBookData(view);
        alreadyInFB = false;

        String bookID = book.getBookID();

        if (addingToList()) {
            bookList = User.getCurrentUser().getBookLists().get(listID);

            // checks if the book already exists in the list
            if (bookList.getBooks().containsKey(listID)){
                Log.d("SEProject", "List " + listID + " already contains book " + bookID);
                Toast.makeText(getContext(), "This list already contains this book", Toast.LENGTH_LONG).show();

                // setting listID to null, meaning the book won't be added
                listID = null;
                bookList = null;
            }
        }

        bookDetailsFAB = view.findViewById(R.id.bookDetailsFAB);
        progressBarFAB = view.findViewById(R.id.progressBarFAB);
        if (addingToList()) {
            bookDetailsFAB.setImageResource(R.drawable.baseline_white_add_24);
            initFAB();
        }
        else
            bookDetailsFAB.setImageResource(R.drawable.baseline_rate_review_24);

        progressBarReviews = view.findViewById(R.id.progressBarReviews);
        reviewsLayout = view.findViewById(R.id.reviewsLayout);


        // Check if book exists in FB, and if so, get its reviews. Since reviews are public, need
        // to check every time if they changed.

        FetchBookFromFB fetchBook = new FetchBookFromFB(this);
        fetchBook.getBookFromFB(bookID);

        return view;
    }
    private void initFAB(){
        bookDetailsFAB.setVisibility(View.VISIBLE);
        bookDetailsFAB.setOnClickListener(this);

        progressBarFAB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSuccess(Book book) {
        if (book == null) {
            Log.d("SEProject", "Book " + this.book.getBookID() + " isn't in FB");
            this.book.setReviews(null);
            this.book.setReviewSum(0);
        }
        else {
            Log.d("SEProject", "Book " + this.book.getBookID() + " is in FB");
            alreadyInFB = true;

            this.book = book;
        }

        setReviews();
    }

    @Override
    public void onFailure() {
        Toast.makeText(getContext(), "Failed to retrieve book information", Toast.LENGTH_LONG).show();

        getParentFragmentManager().popBackStack();
    }



    private void setReviews(){
        progressBarReviews.setVisibility(View.INVISIBLE);
        reviewsLayout.setVisibility(View.VISIBLE);

        RatingBar ratingBar = reviewsLayout.findViewById(R.id.totalRatingBar);
        TextView reviewsTV = reviewsLayout.findViewById(R.id.reviewsTV);


        // TODO: add listener
        if (book.getReviews() == null) {
            reviewsTV.setText("No reviews found");
            ratingBar.setVisibility(View.GONE);
        }


        // TODO: add reviews


        if (!addingToList())
            initFAB();

    }

    @Override
    public void onClick(View view) {
        if (addingToList()){
            progressBarFAB.setVisibility(View.VISIBLE);
            bookDetailsFAB.setVisibility(View.INVISIBLE);

            bookList.setBookToAdd(book);
            bookList.addBook(this, alreadyInFB);

        }
        else{
            //TODO: write review
        }


    }


    private boolean addingToList(){
        return listID != null;
    }

    private void fillInBookData(View view){
        Log.d("SEProject", "Filling in book data");

        TextView titleTV = view.findViewById(R.id.titleTV);
        titleTV.setText(book.getDisplayTitle());

        TextView authorTV = view.findViewById(R.id.authorTV);
        authorTV.setText(book.getDisplayAuthor());

        TextView descriptionTV = view.findViewById(R.id.descriptionTV);
        descriptionTV.setText(book.getDisplayDescription());

        TextView genreTV = view.findViewById(R.id.genreTV);
        genreTV.setText(book.getDisplayGenre());

        TextView pageCountTV = view.findViewById(R.id.pageCountTV);
        pageCountTV.setText(book.getDisplayPageCount());

        TextView datePublishedTV = view.findViewById(R.id.datePublishedTV);
        datePublishedTV.setText(book.getDisplayDatePublished());

        book.setDetailsImageView(view.findViewById(R.id.bookImageView));
        book.setDetailsProgressBar(view.findViewById(R.id.bookProgressBar));

        book.setDetailsImage();

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // removes the details fields from the book when they are destroyed
        book.setDetailsImageView(null);
        book.setDetailsProgressBar(null);
    }
}