package com.example.seproject.book_lists.recycler_adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ListRecyclerAdapter<U extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<U>{
    protected final List<T> data;
    protected final Fragment fragment;

    public static abstract class ClickableViewHolder extends RecyclerView.ViewHolder{

        public ClickableViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(getOnClickListener());
        }

        public abstract View.OnClickListener getOnClickListener();
    }


    public ListRecyclerAdapter(List<T> data, Fragment fragment){
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
