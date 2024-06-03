package com.example.seproject.book_lists;

import android.content.Context;
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
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;

public abstract class ListFragmentAddDelete<T> extends Fragment {

    protected RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;
    protected RecyclerView recyclerView;


    protected void initView(View view){
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


        recyclerView = view.findViewById(R.id.listRecyclerView);
    }


    /**
     * Method to call when the add button is pressed. This method must cause the notifyAdapterItemInserted
     * method to be called.
     * @param v View clicked
     */
    public abstract void onClickAddToList(View v);
    /**
     * Notifies the adapter of the insertion
     * @param position Position at which the item was inserted. Call with -1 to signify insertion to the end of the list.
     */
    public void notifyAdapterItemInserted(int position){
        if (position == -1)
            position = adapter.getItemCount()-1;
        adapter.notifyItemInserted(position); // update recycler view
        Log.d("SEProject", "Recycler view item added at position " + position);
    }

    /**
     * Method to call when the delete button is pressed. This method must cause the notifyAdapterItemRemoved
     * method to be called.
     * @param v View clicked
     */
    public abstract void onClickDeleteFromList(View v);
    /**
     * Notifies the adapter of the deletion
     * @param position Position at which the item was deleted
     */
    public void notifyAdapterItemRemoved(int position) {
        adapter.notifyItemRemoved(position); // update recycler view
        Log.d("SEProject", "Recycler view list removed at position " + position);
    }
}
