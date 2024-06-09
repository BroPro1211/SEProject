package com.example.seproject.book_lists;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.dialog_fragments.DeleteBookDialogFragment;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.book_lists.recycler_adapters.ListOfBooksRecyclerAdapter;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;
import java.util.List;


/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Fragment to display the contents of a book list
 */
public class BookListOverviewFragment extends ListFragmentAddDelete implements BookList.OrderedBooksReceiver {
    public static final String ARG_LIST_ID = "listID";

    private String listID;
    private BookList bookList;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar recyclerProgressBar;
    private ImageButton addToListButton;
    private ImageButton deleteFromListButton;

    public static final String ADD_BOOK_TAG = "add book tag";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            listID = getArguments().getString(ARG_LIST_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list_overview, container, false);

        if (listID == null)
            throw new RuntimeException("Attempted to open book list overview with no id provided");

        bookList = MainActivity.getCurrentUser().getBookLists().get(listID);

        initView(view);

        adapter = new ListOfBooksRecyclerAdapter(this, null);
        recyclerView = view.findViewById(R.id.listRecyclerView);

        bookList.getOrderedBooks(this);


        return view;
    }

    /**
     * Initializes the fragment views
     * @param view The view of the fragment
     */
    private void initView(View view){
        initButtons(view);

        TextView listNameTV = view.findViewById(R.id.listNameTV);
        listNameTV.setText(bookList.getName());

        TextView listDescriptionTV = view.findViewById(R.id.listDescriptionTV);
        String description = bookList.getDescription();
        if (description.length() != 0)
            listDescriptionTV.setText(bookList.getDescription());
        else
            listDescriptionTV.setVisibility(View.GONE);

        view.findViewById(R.id.recyclerProgressBar).setVisibility(View.VISIBLE);

        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        recyclerProgressBar = view.findViewById(R.id.recyclerProgressBar);

        addToListButton = view.findViewById(R.id.addToListButton);
        deleteFromListButton = view.findViewById(R.id.deleteFromListButton);

    }

    @Override
    public void getOrderedBooks(@NonNull List<Book> books) {
        MainActivity.setCurrentlyViewedListOfBooks(books);

        ListRecyclerAdapter.setAdapterToRecycler(adapter, recyclerView, getContext());

        // set listener on books to get images and update adapter
        for (Book book : MainActivity.getCurrentlyViewedListOfBooks()){
            book.getImage(getContext(), new Book.BookImageReceiver() {
                @Override
                public void receiveBookImage(Book book, Bitmap image) {
                    adapter.notifyItemChanged(MainActivity.findBookInCurrentlyViewed(book.getBookID()));
                }
            });
        }

        progressBar1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        recyclerProgressBar.setVisibility(View.GONE);

        addToListButton.setVisibility(View.VISIBLE);
        deleteFromListButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickAddToList() {
        Log.d("SEProject", "Opening add book dialog");

        FragmentManager fragmentManager = getParentFragmentManager();

        Bundle args = new Bundle();
        args.putString(AddBookFragment.ARG_LIST_ID, listID);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, AddBookFragment.class, args)
                .setReorderingAllowed(true)
                .addToBackStack(ADD_BOOK_TAG)
                .commit();
    }

    @Override
    public void onClickDeleteFromList() {
        if (bookList.getBooks().isEmpty())
            return;

        Log.d("SEProject", "Opening delete book dialog");

        DialogFragment deleteBookDialogFragment = DeleteBookDialogFragment.newInstance(listID);
        deleteBookDialogFragment.show(getChildFragmentManager(), "delete book");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MainActivity.setCurrentlyViewedListOfBooks(null);
    }
}