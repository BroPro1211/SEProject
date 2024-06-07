package com.example.seproject.book_lists;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.async_tasks.HTTPSearchForBooksTask;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.User;
import com.example.seproject.book_lists.recycler_adapters.ListOfBooksRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Fragment to search for books to add to a list
 */
public class AddBookFragment extends Fragment implements View.OnClickListener, HTTPSearchForBooksTask.SearchResultsReceiver {
    public static final String ARG_LIST_ID = "listID";

    private String listID;

    private EditText searchTitleET;
    private EditText searchAuthorET;
    private Button searchButton;
    private ProgressBar progressBar;


    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;


    private final String ID = "id";
    private final String VOLUME_INFO = "volumeInfo";
    private final String TITLE = "title";
    private final String AUTHORS = "authors";
    private final String PAGE_COUNT = "pageCount";
    private final String DESCRIPTION = "description";
    private final String CATEGORIES = "categories";
    private final String IMAGE_LINKS = "imageLinks";
    private final String IMAGE = "thumbnail";
    private final String DATE_PUBLISHED = "publishedDate";
    private final String NUM_RESULTS_FOUND = "totalItems";
    private final String RESULTS_ARRAY = "items";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listID = getArguments().getString(ARG_LIST_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        if (listID == null)
            throw new RuntimeException("Attempted to open add book screen without list");

        initViews(view);

        return view;
    }

    /**
     * Initializes the fragment views
     * @param view The view of the fragment
     */
    private void initViews(View view){
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        progressBar = view.findViewById(R.id.searchProgressBar);

        searchTitleET = view.findViewById(R.id.searchTitleET);
        searchAuthorET = view.findViewById(R.id.searchAuthorET);

        MainActivity.setCurrentlyViewedListOfBooks(new ArrayList<>());

        Log.d("SEProject", "Initializing adapter");
        RecyclerView recyclerView = view.findViewById(R.id.listRecyclerView);
        adapter = new ListOfBooksRecyclerAdapter(this, listID);

        ListRecyclerAdapter.setAdapterToRecycler(adapter, recyclerView, getContext());
    }

    @Override
    public void onClick(View v) {
        String urlString = getUrl();

        if (urlString == null) {
            Toast.makeText(getContext(), "Enter at least one search parameter", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("SEProject", "Search's url: " + urlString);

        progressBar.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.INVISIBLE);

        HTTPSearchForBooksTask task = new HTTPSearchForBooksTask(this);
        try{
            task.execute(urlString);
        } catch (HTTPSearchForBooksTask.FailedToSearchBooksException e){
            Toast.makeText(getContext(), "Failed to connect with search server", Toast.LENGTH_LONG).show();
            showButton();
        }
    }

    private void showButton(){
        progressBar.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void getSearchResults(String result) {
        Log.d("SEProject", "Processing search results");

        showButton();

        try {
            JSONObject jsonResults = new JSONObject(result);

            int resultCount = jsonResults.getInt(NUM_RESULTS_FOUND);
            if (resultCount == 0){
                Log.d("SEProject", "No results found");
                Toast.makeText(getContext(), "No results found", Toast.LENGTH_LONG).show();
                return;
            }


            processResultsArray(jsonResults.getJSONArray(RESULTS_ARRAY));


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void processResultsArray(JSONArray resultsArray) throws JSONException {
        int size = resultsArray.length();


        for (int i = MainActivity.getCurrentlyViewedListOfBooks().size()-1; i >= 0 ; i--){
            MainActivity.getCurrentlyViewedListOfBooks().remove(i);
            adapter.notifyItemRemoved(i);
        }


        for (int i = 0; i < size; i++){
            JSONObject bookData = resultsArray.getJSONObject(i);

            String id = getJSONProperty(bookData, ID, "");

            JSONObject volumeInfo = getJSONProperty(bookData, VOLUME_INFO, null);

            String title = getJSONProperty(volumeInfo, TITLE, "");

            String author = getFirstJSONEntry(getJSONProperty(volumeInfo, AUTHORS, null), "");

            String description = getJSONProperty(volumeInfo, DESCRIPTION, "");

            String genre = getFirstJSONEntry(getJSONProperty(volumeInfo, CATEGORIES, null), "");

            int pageCount = getJSONProperty(volumeInfo, PAGE_COUNT, 0);

            String datePublished = getJSONProperty(volumeInfo, DATE_PUBLISHED, "");

            String imageLink = getJSONProperty(getJSONProperty(volumeInfo, IMAGE_LINKS, null), IMAGE, "");
            final String INSECURE_PREFIX = "http://";
            final String SECURE_PREFIX = "https://";
            if (imageLink.startsWith("http://"))
                imageLink = SECURE_PREFIX + imageLink.substring(INSECURE_PREFIX.length());

            Book book = new Book(id, title, author, description, genre, pageCount, datePublished, imageLink);
            MainActivity.getCurrentlyViewedListOfBooks().add(book);
            adapter.notifyItemInserted(i);

            book.getImage(getContext(), new Book.BookImageReceiver() {
                @Override
                public void receiveBookImage(Book book, Bitmap image) {
                    adapter.notifyItemChanged(MainActivity.findBookInCurrentlyViewed(book.getBookID()));
                }
            });

            Log.d("SEProject", "Added book " + title + ", " + author);
        }

        Log.d("SEProject", "Finished creating the recycler list of books");

    }

    /**
     * Method to get property from json object, and return a default value if doesn't exist
     * @param jsonObject Json object to get property from
     * @param propertyName Name of property
     * @param defaultVal The default value to return
     * @return The property, or default value if doesn't exist
     * @param <T> The type of the property
     * @throws JSONException Json exception
     */
    private <T> T getJSONProperty(JSONObject jsonObject, String propertyName, T defaultVal) throws JSONException {
        if (jsonObject == null || !jsonObject.has(propertyName))
            return defaultVal;
        return (T) jsonObject.get(propertyName);
    }

    /**
     * Method to get first entry from json array, and return a default value if doesn't exist
     * @param jsonArray Json array to get entry from
     * @param defaultVal The default value to return
     * @return The entry, or default value if doesn't exist
     * @param <T> The type of the entry
     * @throws JSONException Json exception
     */
    private <T> T getFirstJSONEntry(JSONArray jsonArray, T defaultVal) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0)
            return defaultVal;
        return (T) jsonArray.get(0);
    }

    /**
     * Returns the search url based on the user's input
     * @return The search url. Null if both input fields are empty.
     */
    @Nullable
    private String getUrl() {
        final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
        final String SEARCH_TITLE_KEYWORD = "intitle:";
        final String SEARCH_AUTHOR_KEYWORD = "inauthor:";

        final String MAX_RESULTS_KEYWORD = "&maxResults=";
        final int MAX_RESULTS = 20;
        if (! (0 <= MAX_RESULTS && MAX_RESULTS <= 40))
            throw new RuntimeException("Max results parameter must be between 0 and 40 (inclusive)");


        String searchTitle = searchTitleET.getText().toString();
        String searchAuthor = searchAuthorET.getText().toString();

        if (searchTitle.length() == 0 && searchAuthor.length() == 0)
            return null;

        String urlString = BASE_URL;
        if (searchTitle.length() != 0)
            urlString += SEARCH_TITLE_KEYWORD + formatUrl(searchTitle);
        if (searchAuthor.length() != 0)
            urlString += SEARCH_AUTHOR_KEYWORD + formatUrl(searchAuthor);
        urlString += MAX_RESULTS_KEYWORD + MAX_RESULTS;


        final String FILTER_RESULTS = "&fields="
                + NUM_RESULTS_FOUND + ","
                + RESULTS_ARRAY
                    + "("
                    + ID + ","
                    + VOLUME_INFO
                        + "("
                        + TITLE + ","
                        + AUTHORS + ","
                        + DESCRIPTION + ","
                        + CATEGORIES + ","
                        + PAGE_COUNT + ","
                        + DATE_PUBLISHED +","
                        + IMAGE_LINKS
                            + "("
                            + IMAGE
                            + ")"
                        + ")"
                    + ")";


        return urlString + FILTER_RESULTS;
    }

    /**
     * Formats the url by replacing spaces with pluses
     * @param s The string to format
     * @return The formatted url
     */
    private String formatUrl(String s){
        return s.replace(' ', '+');
    }
}