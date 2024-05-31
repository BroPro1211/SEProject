package com.example.seproject.book_lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.MainActivity;
import com.example.seproject.R;

import java.util.List;

public class BookListRecyclerAdapter extends RecyclerView.Adapter<BookListRecyclerAdapter.BookListViewHolder> {

    private List<BookList> bookLists;
    private Fragment fragment;

    public static class BookListViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV;
        private TextView listSizeTV;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.listNameTV);
            listSizeTV = itemView.findViewById(R.id.listSizeTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookListRecyclerAdapter adapter = (BookListRecyclerAdapter)getBindingAdapter();

                    int position = getAbsoluteAdapterPosition();
                    String listID = adapter.bookLists.get(position).getListID();
                    Bundle args = new Bundle();
                    args.putString(BookListOverviewFragment.ARG_LIST_ID, listID);

                    FragmentManager fragmentManager = adapter.fragment.getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, BookListOverviewFragment.class,args)
                            .setReorderingAllowed(true)
                            .addToBackStack(MainActivity.HOME_SCREEN_TAG)
                            .commit();


                }

            });

        }

        public TextView getNameTV() {
            return nameTV;
        }
        public TextView getListSizeTV() {
            return listSizeTV;
        }
    }


    public BookListRecyclerAdapter(List<BookList> bookLists, Fragment parentFragment){
        this.bookLists = bookLists;
        fragment = parentFragment;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_recycler_layout, parent, false);
        return new BookListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position) {
        BookList bookList = bookLists.get(position);
        holder.getNameTV().setText(bookList.getName());
        holder.getListSizeTV().setText(bookList.getBookCount() + " books");

    }

    @Override
    public int getItemCount() {
        return bookLists.size();
    }


}
