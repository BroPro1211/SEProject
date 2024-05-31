package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.seproject.R;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;
import com.example.seproject.book_lists.recycler_adapters.ListOfBooksRecyclerAdapter;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;


public class BookListOverviewFragment extends ListFragmentAddDelete<Book> {
    public static final String ARG_LIST_ID = "listID";

    private String listID;
    private BookList bookList;

    private Button listInfoButton;
    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

    public static final String ADD_BOOK_TAG = "add book tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            listID = getArguments().getString(ARG_LIST_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_list_overview, container, false);

        if (listID == null)
            throw new RuntimeException("Attempted to open book list overview with no id provided");

        bookList = User.getCurrentUser().getBookLists().get(listID);

        bookList.createOrderedBooks();
        User.setCurrentlyViewedListOfBooks(bookList.getOrderedBooks());

        TextView listNameTV = view.findViewById(R.id.listNameTV);
        listNameTV.setText(bookList.getName());
        TextView listDescriptionTV = view.findViewById(R.id.listDescriptionTV);
        String description = bookList.getDescription();
        if (description.length() != 0)
            listDescriptionTV.setText(bookList.getDescription());
        else
            listDescriptionTV.setVisibility(View.GONE);

        initView(view);

        return view;
    }


    @NonNull
    @Override
    public ListRecyclerAdapter<? extends RecyclerView.ViewHolder, Book> initAdapter() {
        return new ListOfBooksRecyclerAdapter(this, null);
    }

    @Override
    public void onClickAddToList(View v) {
        FragmentManager fragmentManager = getParentFragmentManager();

        Bundle args = new Bundle();
        args.putString(AddBookFragment.ARG_LIST_ID, listID);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, AddBookFragment.class, args)
                .setReorderingAllowed(true)
                .addToBackStack(ADD_BOOK_TAG)
                .commit();
    }

    @Override
    public void onClickDeleteFromList(View v) {

    }
}