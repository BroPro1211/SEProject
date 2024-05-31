package com.example.seproject.book_lists;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ListRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public final List<T> data;

    public static abstract class ClickableViewHolder extends RecyclerView.ViewHolder{

        public ClickableViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(getOnClickListener());
        }

        public abstract View.OnClickListener getOnClickListener();
    }


    public ListRecyclerAdapter(List<T> data){
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
