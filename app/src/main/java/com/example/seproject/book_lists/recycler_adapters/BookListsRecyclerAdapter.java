package com.example.seproject.book_lists.recycler_adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.BookListsFragment;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.book_lists.BookListOverviewFragment;

import java.util.List;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * The adapter for the list of the user's book lists
 */
public class BookListsRecyclerAdapter extends ListRecyclerAdapter<BookListsRecyclerAdapter.BookListViewHolder, BookListsFragment> {

    private final List<BookList> data;

    public static class BookListViewHolder extends ListRecyclerAdapter.ClickableViewHolder {
        private final TextView nameTV;
        private final TextView listSizeTV;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.listNameTV);
            listSizeTV = itemView.findViewById(R.id.listSizeTV);
        }

        public View.OnClickListener getOnClickListener(){
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookListsRecyclerAdapter adapter = (BookListsRecyclerAdapter)getBindingAdapter();

                    int position = getAbsoluteAdapterPosition();
                    String listID = adapter.data.get(position).getListID();
                    Bundle args = new Bundle();
                    args.putString(BookListOverviewFragment.ARG_LIST_ID, listID);

                    Log.d("SEProject", "Opening list id " + listID + " overview");

                    FragmentManager fragmentManager = adapter.fragment.getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, BookListOverviewFragment.class,args)
                            .setReorderingAllowed(true)
                            .addToBackStack(MainActivity.HOME_SCREEN_TAG)
                            .commit();
                }
            };
        }

        /**
         * Returns the name text view
         * @return The nameTV
         */
        public TextView getNameTV() {
            return nameTV;
        }

        /**
         * Returns the list size text view
         * @return The listSizeTV
         */
        public TextView getListSizeTV() {
            return listSizeTV;
        }
    }

    /**
     * Constructor for BookListsRecyclerAdapter
     * @param bookLists The list of book lists to display
     * @param parentFragment The
     */
    public BookListsRecyclerAdapter(List<BookList> bookLists, BookListsFragment parentFragment){
        super(parentFragment);

        data = bookLists;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_recycler_layout, parent, false);
        return new BookListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position) {
        BookList bookList = data.get(position);

        holder.getNameTV().setText(bookList.getName());
        holder.getListSizeTV().setText(bookList.getBookCount() + " books");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
