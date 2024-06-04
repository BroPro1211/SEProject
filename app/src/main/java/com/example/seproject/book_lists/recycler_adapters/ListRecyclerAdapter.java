package com.example.seproject.book_lists.recycler_adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class ListRecyclerAdapter<U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<U>{
    protected final Fragment fragment;

    public static abstract class ClickableViewHolder extends RecyclerView.ViewHolder{

        public ClickableViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(getOnClickListener());
        }

        public abstract View.OnClickListener getOnClickListener();
    }


    public ListRecyclerAdapter(Fragment fragment){
        this.fragment = fragment;
    }


    public static void setAdapterToRecycler(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView recyclerView, Context context){
        Log.d("SEProject", "Initializing recycler");

        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
