package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seproject.R;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class BookDetailsFragment extends Fragment {

    public static final String ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST = "book pos in currently viewed list";
    public static final String ARG_LIST_ID = "list id";

    private Book book;
    private String listID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int bookPos = getArguments().getInt(ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST);
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

        FloatingActionButton floatingActionButton = view.findViewById(R.id.addBookFAB);
        if (listID != null){
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User.getCurrentUser().getBookLists().get(listID).addBook(book);
                    getParentFragmentManager().popBackStack(BookListOverviewFragment.ADD_BOOK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        }

        return view;
    }



    private void fillInBookData(View view){
        TextView titleTV = view.findViewById(R.id.titleTV);
        titleTV.setText(book.getTitle());

        TextView authorTV = view.findViewById(R.id.authorTV);
        authorTV.setText(book.getAuthor());

        TextView descriptionTV = view.findViewById(R.id.descriptionTV);
        descriptionTV.setText(book.getDescription());

        TextView pageCountTV = view.findViewById(R.id.pageCountTV);
        pageCountTV.setText(String.valueOf(book.getPageCount()));

        book.setDetailsDialogImageView(view.findViewById(R.id.bookImageView));
        book.setDetailsDialogProgressBar(view.findViewById(R.id.bookProgressBar));

        book.setImage(book.getDetailsDialogImageView(), book.getDetailsDialogProgressBar());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        book.setDetailsDialogImageView(null);
        book.setDetailsDialogProgressBar(null);
    }
}