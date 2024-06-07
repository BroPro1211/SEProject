package com.example.seproject.data_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.async_tasks.LoadBookImageFromUrlTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Book {
    private String bookID;
    private String title;
    private String author;
    private String description;
    private String genre;
    private int pageCount;
    private String datePublished;

    private Map<String, Review> reviews;
    private List<Review> orderedReviews;

    private String imageLink;
    private Bitmap bookImage;
    public static final String IMAGE_IN_FIREBASE_STORAGE = "image stored in firebase storage";

    private ProgressBar detailsProgressBar;
    private ImageView detailsImageView;




    public Book(String bookID, String title, String author, String description, String genre, int pageCount, String datePublished, String imageLink) {
        if (Arrays.asList(bookID, title, author, description, genre, imageLink).contains(null))
            throw new RuntimeException("Book cannot have null field");
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.pageCount = pageCount;
        this.datePublished = datePublished;
        this.imageLink = imageLink;
        reviews = null;
    }

    /**
     * Downloads the book's image and calls the receiver with it
     * @param context The context
     * @param receiver The receiver to call
     */
    @Exclude
    public void getImage(Context context, BookImageReceiver receiver){
        Log.d("SEProject", "Getting book image for id " + bookID);

        // checks if the image was already loaded
        if (bookImage != null){
            Log.d("SEProject", "Book image already on device");
            receiver.receiveBookImage(this, bookImage);
        }

        // else, the image isn't loaded, and we download the image from the link or FB Storage
        else {
            // checks if the image link is valid
            if (imageLink != null && imageLink.length() != 0){
                // if the book is from FB, we download the image from firebase storage
                if (imageLink.equals(IMAGE_IN_FIREBASE_STORAGE)){
                    Log.d("SEProject", "Downloading book image from FB");
                    String fileName = bookID + FBref.IMAGE_FILE_EXTENSION;
                    StorageReference bookImageReference = FBref.FBBookImages.child(fileName);

                    final long ONE_MEGABYTE = 1024 * 1024;
                    bookImageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d("SEProject", "Successfully downloaded image from FB");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            saveImage(context, bitmap, receiver);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("SEProject", "FB image download from " + fileName + " failed");

                            // if the download failed, the default book icon will be applied
                            getDefaultImage(context, receiver);
                        }
                    });
                }

                // else, we use the download link
                else {
                    Log.d("SEProject", "Downloading book image from url " + imageLink);
                    LoadBookImageFromUrlTask task = new LoadBookImageFromUrlTask(context, this, receiver);
                    try{
                        // task calls the saveImage method
                        task.execute(imageLink);
                    }
                    catch (LoadBookImageFromUrlTask.FailedToLoadImageException e){
                        Log.d("SEProject", "Image download from " + imageLink + " failed, downloading default icon");

                        // if the task failed, the default book icon will be applied
                        getDefaultImage(context, receiver);

                    }
                }
            }

            // if no image link provided, the default icon will be applied
            else {
                Log.d("SEProject", "No image url found, downloading default icon");
                getDefaultImage(context, receiver);
            }
        }

    }

    /**
     * Interface to call when finishing loading the image
     */
    public interface BookImageReceiver{
        /**
         * Method to call when finished loading image
         * @param book Book whose image was loaded
         * @param image Image bitmap
         */
        void receiveBookImage(Book book, Bitmap image);
    }
    /**
     * Sets the default image
     * @param context The context
     * @param receiver The receiver to call
     */
    private void getDefaultImage(Context context, BookImageReceiver receiver){
        // creates a bitmap of the default icon
        saveImage(context, MainActivity.getBitmapFromDrawable(context, R.drawable.baseline_book_75), receiver);
    }

    /**
     * Saves the downloaded image
     * @param context The context
     * @param image The loaded image
     * @param receiver The receiver to call
     */
    public void saveImage(Context context, Bitmap image, BookImageReceiver receiver){
        if (image == null)
            getDefaultImage(context, receiver);

        Log.d("SEProject", "Saving book image for " + bookID);
        this.bookImage = image;

        if (receiver != null)
            receiver.receiveBookImage(this, bookImage);
        setDetailsImage();
    }




    @Exclude
    public void setDetailsImage(){
        if (bookImage == null)
            return;
        if (detailsProgressBar == null || detailsImageView == null)
            return;

        Log.d("SEProject", "Setting image in details page for book " + bookID);
        detailsImageView.setImageBitmap(bookImage);
        detailsImageView.setVisibility(View.VISIBLE);
        detailsProgressBar.setVisibility(View.INVISIBLE);

        detailsImageView = null;
        detailsProgressBar = null;

    }
    @Exclude
    public void setDetailsProgressBar(ProgressBar progressBar){
        this.detailsProgressBar = progressBar;
    }
    @Exclude
    public void setDetailsImageView(ImageView imageView){
        this.detailsImageView = imageView;
    }

    @Exclude
    public void setImageLink(String imageLink){
        this.imageLink = imageLink;
    }


    @Exclude
    @NonNull
    public List<Review> getOrderedReviews(){
        if (orderedReviews == null)
            orderedReviews = new ArrayList<>();
        return orderedReviews;
    }

    /**
     * Adds review
     * @param review Review to add
     * @return Position in orderedReviews to which review was added
     */
    public int addReview(Review review){
        if (review == null)
            throw new RuntimeException("Attempted to add null review to book " + bookID);

        Log.d("SEProject", "adding review by " + review.getUid() + " to book " + bookID);

        getReviews().put(review.getUid(), review);

        if (! containsUserReview()){
            Log.d("SEProject", "added review to index 0");
            getOrderedReviews().add(0, review);
            return 0;
        }
        else {
            Log.d("SEProject", "added review to index 1");
            getOrderedReviews().add(1, review);
            return 1;
        }
    }
    /**
     * Deletes review
     * @param review Review to delete
     * @return Position in orderedReviews from which review was deleted
     */
    public int deleteReview(Review review){
        if (review == null)
            throw new RuntimeException("Attempted to remove null review from book " + bookID);

        Log.d("SEProject", "removing review by " + review.getUid() + " from book " + bookID);

        getReviews().remove(review.getUid());

        int pos = findReviewUid(review.getUid());
        getOrderedReviews().remove(pos);
        return pos;
    }

    /**
     * Finds the index of the review in the list
     * @param uid Uid of the review to search for
     * @return The index of the review, or -1 if not found
     */
    public int findReviewUid(String uid){
        for (int i = 0; i < getOrderedReviews().size(); i++){
            if (getOrderedReviews().get(i).getUid().equals(uid)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns if book contains a review by the currently signed in user
     * @return True if such a review exists, false otherwise
     */
    private boolean containsUserReview(){
        return !getOrderedReviews().isEmpty() && getOrderedReviews().get(0).isCurrentUserReview();
    }

    /**
     * Clears all reviews
     */
    public void clearReviews(){
        reviews = null;
        orderedReviews = null;
    }

    /**
     * Returns an appropriate description if s is empty
     * @param s String to check if empty
     * @param parameter The parameter
     * @return Returns s if s is not empty, else returns a string desccribing that the parameter wasn't found
     */
    private String getTextIfEmpty(String s, String parameter){
        if (s == null || s.length() == 0)
            return "Book " + parameter + " not found";
        return s;
    }
    @Exclude
    public String getDisplayTitle(){
        return getTextIfEmpty(title, "title");
    }
    @Exclude
    public String getDisplayAuthor(){
        return getTextIfEmpty(author, "author");
    }
    @Exclude
    public String getDisplayDescription(){
        return getTextIfEmpty(description, "description");
    }
    @Exclude
    public String getDisplayGenre(){
        return getTextIfEmpty(genre, "genre");
    }
    @Exclude
    public String getDisplayPageCount(){
        if (pageCount == 0)
            return "Book page count not found";
        return String.valueOf(pageCount);
    }
    @Exclude
    public String getDisplayDatePublished(){
        return getTextIfEmpty(datePublished, "date published");
    }



    @Exclude
    public String getBookShortInfo(){
        return MainActivity.shortenString(title, 15) + "\n" + MainActivity.shortenString(author, 15);
    }

    @Exclude
    public Bitmap getBookImage(){
        return bookImage;
    }



    public Book(){

    }

    public String getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getDatePublished() {
        return datePublished;
    }


    @NonNull
    public Map<String, Review> getReviews() {
        if (reviews == null)
            reviews = new LinkedHashMap<>();
        return reviews;
    }


}
