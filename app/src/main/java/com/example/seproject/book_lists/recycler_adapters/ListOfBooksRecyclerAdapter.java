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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.seproject.R;
import com.example.seproject.data_classes.User;
import com.example.seproject.data_classes.Book;
import com.example.seproject.book_lists.BookDetailsFragment;

public class ListOfBooksRecyclerAdapter extends ListRecyclerAdapter<ListOfBooksRecyclerAdapter.BookViewHolder, Book> {

    // if adapter accessed from addBookFragment, field stores the listID a book should be added to
    // else is null
    private String listID;

    public static class BookViewHolder extends ListRecyclerAdapter.ClickableViewHolder {
        private TextView titleTV;
        private TextView authorTV;
        private ImageView imageView;
        private ProgressBar progressBar;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.bookTitleTV);
            authorTV = itemView.findViewById(R.id.bookAuthorTV);
            imageView = itemView.findViewById(R.id.bookImage);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListOfBooksRecyclerAdapter adapter = (ListOfBooksRecyclerAdapter) getBindingAdapter();

                    int position = getAbsoluteAdapterPosition();


                    Log.d("SEProject", "Opening book details dialog for position " + position);

                    FragmentManager fragmentManager = adapter.fragment.getParentFragmentManager();

                    Bundle args = new Bundle();
                    args.putInt(BookDetailsFragment.ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST, position);
                    args.putString(BookDetailsFragment.ARG_LIST_ID, adapter.listID);

                    fragmentManager.beginTransaction()
                            .add(R.id.fragmentContainerView, BookDetailsFragment.class, args)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }
            };
        }

        public TextView getTitleTV() {
            return titleTV;
        }
        public TextView getAuthorTV(){
            return authorTV;
        }
        public ImageView getImageView() {return imageView;}
        public ProgressBar getProgressBar(){return progressBar;}

    }

    public ListOfBooksRecyclerAdapter(Fragment fragment, String listID) {
        super(User.getCurrentlyViewedListOfBooks(), fragment);

        this.listID = listID;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_recycler_layout, parent, false);
        return new ListOfBooksRecyclerAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = data.get(position);

        holder.getTitleTV().setText(book.getTitle());
        holder.getAuthorTV().setText(book.getAuthor());

        book.getAndSetImage(fragment.getContext(), holder.getImageView(), holder.getProgressBar());
    }
}