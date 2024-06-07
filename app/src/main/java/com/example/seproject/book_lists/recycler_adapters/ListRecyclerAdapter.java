package com.example.seproject.book_lists.recycler_adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Abstract generic adapter containing some basic functionalities
 */
public abstract class ListRecyclerAdapter<U extends RecyclerView.ViewHolder, T extends Fragment> extends RecyclerView.Adapter<U>{
    protected final T fragment;

    /**
     * Abstract view holder class that holds a clickable view
     */
    public static abstract class ClickableViewHolder extends RecyclerView.ViewHolder{

        public ClickableViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(getOnClickListener());
        }

        /**
         * Method returns the onClickListener to set on the view
         * @return The desired onClickListener
         */
        public abstract View.OnClickListener getOnClickListener();
    }


    public ListRecyclerAdapter(T fragment){
        this.fragment = fragment;
    }


    /**
     * Method that sets the given adapter to the recyclerView
     * @param adapter Adapter to set
     * @param recyclerView Recycler view to set adapter to
     * @param context The context
     */
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
