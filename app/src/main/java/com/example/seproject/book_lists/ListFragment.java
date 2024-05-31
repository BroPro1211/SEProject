package com.example.seproject.book_lists;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.R;
import com.example.seproject.User;

public abstract class ListFragment extends Fragment {

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ImageButton addToListButton = view.findViewById(R.id.addToListButton);
        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddToList(v);
            }
        });


        ImageButton deleteFromListButton = view.findViewById(R.id.deleteFromListButton);
        deleteFromListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteFromList(v);
            }
        });


        RecyclerView recyclerView = view.findViewById(R.id.listRecyclerView);
        adapter = initAdapter();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }


    @NonNull
    public abstract ListRecyclerAdapter initAdapter();

    public abstract void onClickAddToList(View v);
    public void notifyAdapterItemInserted(int position){
        adapter.notifyItemInserted(position); // update recycler view
        Log.d("SEProject", "Recycler view item added at position " + position);
    }

    public abstract void onClickDeleteFromList(View v);
    public void notifyAdapterItemRemoved(int position) {
        adapter.notifyItemRemoved(position); // update recycler view
        Log.d("SEProject", "Recycler view list removed at position " + position);
    }
}
