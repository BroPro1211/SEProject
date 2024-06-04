package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seproject.R;
import com.example.seproject.book_lists.dialog_fragments.AddBookListDialogFragment;
import com.example.seproject.book_lists.dialog_fragments.DeleteBookListDialogFragment;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.User;
import com.example.seproject.book_lists.recycler_adapters.BookListsRecyclerAdapter;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;

public class BookListsFragment extends ListFragmentAddDelete<BookList> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_lists, container, false);

        initView(view);

        adapter =  new BookListsRecyclerAdapter(User.getOrderedBookLists(), this);
        ListRecyclerAdapter.setAdapterToRecycler(adapter, recyclerView, getContext());

        return view;
    }



    @Override
    public void onClickAddToList() {
        Log.d("SEProject", "Opening add book list dialog");

        DialogFragment addListFragment = new AddBookListDialogFragment();
        addListFragment.show(getChildFragmentManager(), "add list");
    }

    @Override
    public void onClickDeleteFromList() {
        Log.d("SEProject", "Opening delete book list dialog");

        DialogFragment deleteListFragment = new DeleteBookListDialogFragment();
        deleteListFragment.show(getChildFragmentManager(), "delete list");
    }


}