package com.example.seproject.book_lists;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seproject.R;
import com.example.seproject.User;


public class BookListOverviewFragment extends Fragment {
    public static final String ARG_LIST_ID = "listID";
    private String listID;

    private Button addBookButton;
    private Button deleteBookButton;
    private Button listInfoButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;


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

        initViews(view);

        return view;
    }

    private void initViews(View view){
        TextView listNameTV = view.findViewById(R.id.listNameTV);
        listNameTV.setText(User.getCurrentUser().getBookLists().get(listID).getName());




        ImageButton listInfoButton = view.findViewById(R.id.listInfoButton);
    }
}