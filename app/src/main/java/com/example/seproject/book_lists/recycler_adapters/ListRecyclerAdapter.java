package com.example.seproject.book_lists.recycler_adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

}
