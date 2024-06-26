package com.example.seproject.book_lists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.dialog_fragments.AddReviewDialogFragment;
import com.example.seproject.book_lists.recycler_adapters.ListRecyclerAdapter;
import com.example.seproject.book_lists.recycler_adapters.ReviewsRecyclerAdapter;
import com.example.seproject.data_classes.Book;
import com.example.seproject.data_classes.BookList;
import com.example.seproject.data_classes.FBref;
import com.example.seproject.data_classes.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


public class BookDetailsFragment extends Fragment implements View.OnClickListener,FetchBookFromFB.OnGetBook {

    public static final String ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST = "book pos in currently viewed list";
    public static final String ARG_ADD_BOOKS_TO_LIST_ID = "add books to list id";

    private Book book;
    private boolean alreadyInFB;

    private String addToListID; // if adding book, stores the list id to add the book to, else null
    private BookList bookList;



    private ProgressBar progressBarReviews;
    private ConstraintLayout reviewsLayout;
    private ProgressBar progressBarFAB;
    private FloatingActionButton bookDetailsFAB;

    private RatingBar totalRatingBar;
    private TextView noReviewsTV;
    private TextView averageRatingTV;
    private TextView reviewsNumberTV;

    private int totalStars;


    private RecyclerView recyclerView;
    private ReviewsRecyclerAdapter adapter;

    private DatabaseReference reviewsReference;
    private ChildEventListener reviewsListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int bookPos = getArguments().getInt(ARG_BOOK_POSITION_IN_CURRENTLY_VIEWED_LIST);
            book = MainActivity.getCurrentlyViewedListOfBooks().get(bookPos);

            addToListID = getArguments().getString(ARG_ADD_BOOKS_TO_LIST_ID);
            reviewsReference = FBref.FBBooks.child(book.getBookID()).child(FBref.BOOK_REVIEWS);
            reviewsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Log.d("SEProject", "on child added called for " + book.getBookID());

                    Review review = snapshot.getValue(Review.class);
                    if (review == null){
                        return;
                    }

                    int pos = book.addReview(review);
                    totalStars += review.getStarNum();

                    if (review.isCurrentUserReview() && !addingToList()){
                        bookDetailsFAB.setVisibility(View.INVISIBLE);
                    }


                    if (! showingReviews()) {
                        initReviewList();
                    }
                    else{
                        adapter.notifyItemInserted(pos);
                        updateReviewsTVs();
                    }

                    String fileName = review.getUid() + FBref.IMAGE_FILE_EXTENSION;
                    StorageReference bookImageReference = FBref.FBUserImages.child(fileName);
                    Log.d("SEProject", "Downloading user profile image");

                    bookImageReference.getBytes(FBref.MAX_IMAGE_BYTES).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            int pos = book.findReviewUid(review.getUid());
                            if (task.isSuccessful()){
                                Log.d("SEProject", "Successfully downloaded image from FB");
                                byte[] bytes = task.getResult();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                book.getOrderedReviews().get(pos).setUserProfileImage(bitmap);
                            }
                            else{
                                Log.d("SEProject", "Profile image not found");
                                book.getOrderedReviews().get(pos)
                                        .setUserProfileImage(MainActivity.getBitmapFromDrawable(getContext(), R.drawable.baseline_person_gray_100));
                            }
                            adapter.notifyItemChanged(pos);

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("SEProject", "on child changed called for book " + book.getBookID());

                    Review review = snapshot.getValue(Review.class);
                    if (review == null){
                        return;
                    }

                    int pos = book.findReviewUid(review.getUid());

                    book.getOrderedReviews().get(pos).setUserLikesMap(review.getUserLikesMap());


                    Log.d("SEProject", "updating adapter at pos " + pos);
                    adapter.notifyItemChanged(pos);

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("SEProject", "on child removed called for " + book.getBookID());

                    Review review = snapshot.getValue(Review.class);
                    if (review == null){
                        return;
                    }

                    totalStars -= review.getStarNum();

                    int pos = book.deleteReview(review);
                    adapter.notifyItemRemoved(pos);

                    if (review.isCurrentUserReview() && ! addingToList()){
                        bookDetailsFAB.setVisibility(View.VISIBLE);
                    }
                    updateReviewsTVs();

                    if (book.getReviews().isEmpty()){
                        noBookReviews();

                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("SEProject", "on child moved called for book " + book.getBookID());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("SEProject", "reviews listener cancelled for book " + book.getBookID(), error.toException());
                    onException();
                }
            };
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        if (book == null)
            throw new RuntimeException("Attempted to open book details dialog without book");

        fillInBookData(view);
        alreadyInFB = false;

        initViews(view);

        String bookID = book.getBookID();

        if (addingToList()) {
            bookList = MainActivity.getCurrentUser().getBookLists().get(addToListID);

            // checks if the book already exists in the list
            if (bookList.getBooks().containsKey(addToListID)){
                Log.d("SEProject", "List " + addToListID + " already contains book " + bookID);
                Toast.makeText(getContext(), "This list already contains this book", Toast.LENGTH_LONG).show();

                // setting listID to null, meaning the book won't be added
                addToListID = null;
                bookList = null;
            }
        }

        if (addingToList()) {
            bookDetailsFAB.setImageResource(R.drawable.baseline_white_add_24);
            initFAB();
        }
        else
            bookDetailsFAB.setImageResource(R.drawable.baseline_rate_review_24);

        totalStars = 0;
        book.clearReviews();

        // Searches for the book in FB, and get its reviews. Since reviews are public, we need
        // to check every time if they changed.

        FetchBookFromFB fetchBook = new FetchBookFromFB(this);
        fetchBook.getBookFromFB(bookID);

        return view;
    }
    /**
     * Initializes the fragment views
     * @param view The view of the fragment
     */
    private void initViews(View view){
        bookDetailsFAB = view.findViewById(R.id.bookDetailsFAB);
        progressBarFAB = view.findViewById(R.id.progressBarFAB);

        progressBarReviews = view.findViewById(R.id.progressBarReviews);
        reviewsLayout = view.findViewById(R.id.reviewsLayout);

        totalRatingBar = reviewsLayout.findViewById(R.id.totalRatingBar);
        noReviewsTV = reviewsLayout.findViewById(R.id.noReviewsTV);
        averageRatingTV = reviewsLayout.findViewById(R.id.averageRatingTV);
        reviewsNumberTV = reviewsLayout.findViewById(R.id.reviewsNumberTV);
        recyclerView = view.findViewById(R.id.reviewsRecyclerView);

    }
    /**
     * Initializes the floating action button
     */
    private void initFAB(){
        bookDetailsFAB.setVisibility(View.VISIBLE);
        bookDetailsFAB.setOnClickListener(this);

        progressBarFAB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (addingToList()){
            progressBarFAB.setVisibility(View.VISIBLE);
            bookDetailsFAB.setVisibility(View.INVISIBLE);

            bookList.addBook(book, this, alreadyInFB);

        }
        else{
            Log.d("SEProject", "Opening add review dialog");

            DialogFragment addReviewDialogFragment = AddReviewDialogFragment.newInstance(book.getBookID());
            addReviewDialogFragment.show(getChildFragmentManager(), "add review");
        }


    }

    /**
     * Method to call when finished adding the book to the book list
     */
    public void onAddedBook(){
        getParentFragmentManager().popBackStack(BookListOverviewFragment.ADD_BOOK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Fills in the book data
     * @param view Fragment view
     */
    private void fillInBookData(View view){
        Log.d("SEProject", "Filling in book data");

        TextView titleTV = view.findViewById(R.id.titleTV);
        titleTV.setText(book.getDisplayTitle());

        TextView authorTV = view.findViewById(R.id.authorTV);
        authorTV.setText(book.getDisplayAuthor());

        TextView descriptionTV = view.findViewById(R.id.descriptionTV);
        descriptionTV.setText(book.getDisplayDescription());

        TextView genreTV = view.findViewById(R.id.genreTV);
        genreTV.setText(book.getDisplayGenre());

        TextView pageCountTV = view.findViewById(R.id.pageCountTV);
        pageCountTV.setText(book.getDisplayPageCount());

        TextView datePublishedTV = view.findViewById(R.id.datePublishedTV);
        datePublishedTV.setText(book.getDisplayDatePublished());

        book.setDetailsImageView(view.findViewById(R.id.bookImageView));
        book.setDetailsProgressBar(view.findViewById(R.id.bookProgressBar));

        book.setDetailsImage();
    }



    @Override
    public void onSuccess(Book book) {
        if (book == null)
            Log.d("SEProject", "Book " + this.book.getBookID() + " isn't in FB");

        else {
            Log.d("SEProject", "Book " + this.book.getBookID() + " is in FB");
            alreadyInFB = true;
        }

        progressBarReviews.setVisibility(View.INVISIBLE);
        reviewsLayout.setVisibility(View.VISIBLE);

        startShowingReviews();
    }

    @Override
    public void onFailure() {
        onException();
    }

    /**
     * If exception encountered, exits fragment
     */
    private void onException(){
        Log.d("SEProject", "Encountered FB failure while displaying book " + book.getBookID());
        Toast.makeText(getContext(), "Encountered error", Toast.LENGTH_LONG).show();

        getParentFragmentManager().popBackStack();
    }



    /**
     * Method initializes the reviews display
     */
    private void startShowingReviews(){

        // set initial reviews screen to no reviews
        noBookReviews();

        if (!addingToList())
            initFAB();

        // the listener is called on initialization for every already existing child node, and also
        // once a new child node is added
        reviewsReference.addChildEventListener(reviewsListener);

    }

    /**
     * Sets the review layout to show no reviews
     */
    private void noBookReviews(){
        Log.d("SEProject", "Setting book " + book.getBookID() + " details screen to no reviews");

        if (showingReviews())
            closeReviewsList();

        noReviewsTV.setVisibility(View.VISIBLE);
        totalRatingBar.setRating(0);
        totalStars = 0;
    }

    /**
     * Closes the review list if all reviews were deleted
     */
    private void closeReviewsList(){
        recyclerView.setVisibility(View.GONE);
        adapter = null;
        recyclerView.setAdapter(null);

        averageRatingTV.setVisibility(View.GONE);
        reviewsNumberTV.setVisibility(View.GONE);

    }

    /**
     * Sets the review layout to show the book reviews
     */
    private void initReviewList(){
        if (book.getReviews().isEmpty()){
            Log.d("SEProject", "Attempting to initialize reviews list for " + book.getBookID() + " when reviews are empty");
            onException();
            return;
        }
        Log.d("SEProject", "showing review list on book " + book.getBookID() + " details screen");

        recyclerView.setVisibility(View.VISIBLE);

        adapter = new ReviewsRecyclerAdapter(this, book.getOrderedReviews());
        ListRecyclerAdapter.setAdapterToRecycler(adapter, recyclerView, getContext());

        updateReviewsTVs();
    }

    /**
     * Updates the data shown in the reviews display
     */
    private void updateReviewsTVs(){
        if (book.getOrderedReviews().isEmpty()){
            return;
        }

        float averageRating = ((float)totalStars) / book.getReviews().size();
        totalRatingBar.setRating(averageRating);

        averageRatingTV.setVisibility(View.VISIBLE);
        averageRatingTV.setText(String.format("%.1f",averageRating));

        reviewsNumberTV.setVisibility(View.VISIBLE);
        int num = book.getReviews().size();
        if (num == 1)
            reviewsNumberTV.setText(num + " review");
        else
            reviewsNumberTV.setText(num + " reviews");

        noReviewsTV.setVisibility(View.GONE);

    }

    /**
     * Returns whether there are any reviews displayed
     * @return True if there are reviews displayed, false otherwise
     */
    private boolean showingReviews(){
        return adapter != null;
    }

    /**
     * Deletes the current user's review
     */
    public void deleteUserReview(){
        FBref.FBBooks.child(book.getBookID()).child(FBref.BOOK_REVIEWS)
                .child(MainActivity.getCurrentUser().getUid()).removeValue();
    }

    /**
     * Returns whether the book should be added to a list or is viewed from a list
     * @return True if the book should be added to a list, false otherwise
     */
    private boolean addingToList(){
        return addToListID != null;
    }

    /**
     * Removes like from a review
     * @param pos Position of the review in the recycler view
     */
    public void removeReviewLikeFromFB(int pos){
        String reviewUid = book.getOrderedReviews().get(pos).getUid();

        FBref.FBBooks.child(book.getBookID()).child(FBref.BOOK_REVIEWS)
                .child(reviewUid).child(FBref.REVIEW_USER_LIKES_MAP).child(MainActivity.getCurrentUser().getUid())
                .removeValue();
    }

    /**
     * Adds like to a review
     * @param pos Position of the review in the recycler view
     */
    public void addReviewLikeToFB(int pos){
        String reviewUid = book.getOrderedReviews().get(pos).getUid();

        FBref.FBBooks.child(book.getBookID()).child(FBref.BOOK_REVIEWS)
                .child(reviewUid).child(FBref.REVIEW_USER_LIKES_MAP).child(MainActivity.getCurrentUser().getUid())
                .setValue(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // removes the details fields from the book when they are destroyed
        book.setDetailsImageView(null);
        book.setDetailsProgressBar(null);

        reviewsReference.removeEventListener(reviewsListener);
    }
}