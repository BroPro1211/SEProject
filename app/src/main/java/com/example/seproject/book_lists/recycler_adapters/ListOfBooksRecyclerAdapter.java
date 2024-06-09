package com.example.seproject.book_lists.recycler_adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.data_classes.Book;
import com.example.seproject.book_lists.BookDetailsFragment;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Recycler adapter to display a list of books (the contents of a book list or the results of a search)
 */
public class ListOfBooksRecyclerAdapter extends ListRecyclerAdapter<ListOfBooksRecyclerAdapter.BookViewHolder, Fragment> {

    /**
     * if adapter accessed from addBookFragment, field stores the listID a book should be added to
     * else is null
     */
    private final String addBooksToListID;

    /**
     * View holder for a book
     */
    public static class BookViewHolder extends ListRecyclerAdapter.ClickableViewHolder {
        private final TextView titleTV;
        private final TextView authorTV;
        private final ImageView imageView;
        private final ProgressBar progressBar;

        /**
         * Initializes the book view holder
         * @param itemView The view holder's view
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.bookTitleTV);
            authorTV = itemView.findViewById(R.id.bookAuthorTV);
            imageView = itemView.findViewById(R.id.bookImage);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListOfBooksRecyclerAdapter adapter = (ListOfBooksRecyclerAdapter) getBindingAdapter();

                    int position = getAbsoluteAdapterPosition();


                    Log.d("SEProject", "Opening book details screen for position " + position);

                    FragmentManager fragmentManager = adapter.fragment.getParentFragmentManager();

                    Bundle args = new Bundle();
                    args.putInt(BookDetailsFragment.ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST, position);
                    args.putString(BookDetailsFragment.ARG_ADD_BOOKS_TO_LIST_ID, adapter.addBooksToListID);

                    // adds the fragment on top so the add book search list isn't destroyed
                    fragmentManager.beginTransaction()
                            .add(R.id.fragmentContainerView, BookDetailsFragment.class, args)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }
            };
        }

        /**
         * Returns the title text view
         * @return The titleTV
         */
        public TextView getTitleTV() {
            return titleTV;
        }

        /**
         * Returns the author text view
         * @return The authorTV
         */
        public TextView getAuthorTV(){
            return authorTV;
        }
        /**
         * Returns the book image view
         * @return The image view
         */
        public ImageView getImageView() {return imageView;}

        /**
         * Returns the book image progress bar
         * @return The progress bar
         */
        public ProgressBar getProgressBar(){return progressBar;}

    }

    /**
     * Constructor for ListOfBooksRecyclerAdapter
     * @param fragment The parent fragment
     * @param addBooksToListID A BookList ID, to add the books to if clicked. If null, the books won't
     *                         be added to a list.
     */
    public ListOfBooksRecyclerAdapter(Fragment fragment, @Nullable String addBooksToListID) {
        super(fragment);

        this.addBooksToListID = addBooksToListID;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_recycler_layout, parent, false);
        return new ListOfBooksRecyclerAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = MainActivity.getCurrentlyViewedListOfBooks().get(position);

        holder.getTitleTV().setText(book.getTitle());
        holder.getAuthorTV().setText(book.getAuthor());

        if (book.getBookImage() == null) {
            holder.getImageView().setVisibility(View.INVISIBLE);
            holder.getProgressBar().setVisibility(View.VISIBLE);
        }
        else{
            holder.getImageView().setVisibility(View.VISIBLE);
            holder.getImageView().setImageBitmap(book.getBookImage());
            holder.getProgressBar().setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return MainActivity.getCurrentlyViewedListOfBooks().size();
    }
}
