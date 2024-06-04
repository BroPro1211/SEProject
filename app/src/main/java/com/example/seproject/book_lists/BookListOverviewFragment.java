package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.seproject.R;
import com.example.seproject.book_lists.dialog_fragments.DeleteBookDialogFragment;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;
import com.example.seproject.book_lists.recycler_adapters.ListOfBooksRecyclerAdapter;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class BookListOverviewFragment extends ListFragmentAddDelete<Book> implements BookList.OrderedBooksReceiver {
    public static final String ARG_LIST_ID = "listID";

    private String listID;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar recyclerProgressBar;
    private ImageButton addToListButton;
    private ImageButton deleteFromListButton;


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

        BookList bookList = User.getCurrentUser().getBookLists().get(listID);

        TextView listNameTV = view.findViewById(R.id.listNameTV);
        listNameTV.setText(bookList.getName());
        TextView listDescriptionTV = view.findViewById(R.id.listDescriptionTV);
        String description = bookList.getDescription();
        if (description.length() != 0)
            listDescriptionTV.setText(bookList.getDescription());
        else
            listDescriptionTV.setVisibility(View.GONE);

        view.findViewById(R.id.recyclerProgressBar).setVisibility(View.VISIBLE);

        initView(view);

        User.setCurrentlyViewedListOfBooks(new ArrayList<>());
        adapter = new ListOfBooksRecyclerAdapter(this, null);
        bookList.getOrderedBooks(this);


        return view;
    }

    @Override
    protected void initView(View view){
        super.initView(view);

        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        recyclerProgressBar = view.findViewById(R.id.recyclerProgressBar);

        addToListButton = view.findViewById(R.id.addToListButton);
        deleteFromListButton = view.findViewById(R.id.deleteFromListButton);

    }

    @Override
    public void getOrderedBooks(List<Book> books) {
        User.setCurrentlyViewedListOfBooks(books);

        ListRecyclerAdapter.setAdapterToRecycler(adapter, recyclerView, getContext());

        progressBar1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        recyclerProgressBar.setVisibility(View.GONE);

        addToListButton.setVisibility(View.VISIBLE);
        deleteFromListButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickAddToList() {
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
    public void onClickDeleteFromList() {
        Log.d("SEProject", "Opening delete book dialog");

        DialogFragment deleteBookDialogFragment = DeleteBookDialogFragment.newInstance(listID);
        deleteBookDialogFragment.show(getChildFragmentManager(), "delete book");
    }



}