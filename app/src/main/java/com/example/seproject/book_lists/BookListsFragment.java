package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.seproject.R;
import com.example.seproject.User;

public class BookListsFragment extends Fragment implements AddBookListDialogFragment.onReturnFromAddDialog, DeleteBookListDialogFragment.onReturnFromDeleteDialog {
    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_lists, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view){
        ImageButton addListButton = view.findViewById(R.id.addToListButton);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addList();
            }
        });


        ImageButton deleteListButton = view.findViewById(R.id.deleteFromListButton);
        deleteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteList();
            }
        });


        RecyclerView recyclerView = view.findViewById(R.id.listRecyclerView);
        adapter = new BookListRecyclerAdapter(User.getOrderedBookLists(), this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void addList(){
        DialogFragment addListFragment = new AddBookListDialogFragment();
        addListFragment.show(getChildFragmentManager(), "add list");
    }
    @Override
    public void listAdded(){
        int position = User.getCurrentUser().getBookLists().size()-1;
        adapter.notifyItemInserted(position); // update recycler view
        Log.d("SEProject", "Recycler view list added at position " + position);
    }

    private void deleteList(){
        DialogFragment deleteListFragment = new DeleteBookListDialogFragment();
        deleteListFragment.show(getChildFragmentManager(), "delete list");
    }
    @Override
    public void listDeleted(int position) {
        adapter.notifyItemRemoved(position); // update recycler view
        Log.d("SEProject", "Recycler view list removed at position " + position);
    }
}