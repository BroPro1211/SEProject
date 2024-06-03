package com.example.seproject.data_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.seproject.R;
import com.example.seproject.book_lists.async_tasks.LoadImageFromUrlTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    @Exclude
    public void getImage(Context context, BookImageReceiver receiver){
        Log.d("SEProject", "Getting book image for id " + bookID);

        // checks if the image was already loaded
        if (bookImage != null){
            receiver.receiveBookImage(this, bookImage);
            Log.d("SEProject", "Book image already on device");
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
                    LoadImageFromUrlTask task = new LoadImageFromUrlTask(context, this, receiver);
                    try{
                        // task downloads image, saves it to this.bookImage, calls the receiver and
                        // updates the details ImageView and ProgressBar if present
                        task.execute(imageLink);
                    }
                    catch (LoadImageFromUrlTask.FailedToLoadImageException e){
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

    private void getDefaultImage(Context context, BookImageReceiver receiver){
        // creates a bitmap of the default icon
        Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.baseline_book_75);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        saveImage(context, bitmap, receiver);
    }

    // Called when the book image is set. If the book image finishes downloading after the book details
    // page is opened, the image will be loaded there.
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
    public void setImage(ImageView imageView, ProgressBar progressBar){
        if (bookImage == null)
            return;

        imageView.setImageBitmap(bookImage);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public interface BookImageReceiver{
        void receiveBookImage(Book book, Bitmap image);
    }

    @Exclude
    public void setDetailsImage(){
        if (bookImage == null)
            return;
        if (detailsProgressBar == null || detailsImageView == null)
            return;

        Log.d("SEProject", "Setting image in details page for book " + bookID);
        setImage(detailsImageView, detailsProgressBar);
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
    public List<Review> getOrderedReviews(){
        if (orderedReviews == null)
            orderedReviews = new ArrayList<>();
        return orderedReviews;
    }

    // adds review and returns the position in orderedReviews to which it was added
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
    // deletes review and returns the position in orderedReviews from which it was deleted
    public int deleteReview(Review review){
        if (review == null)
            throw new RuntimeException("Attempted to remove null review from book " + bookID);

        Log.d("SEProject", "removing review by " + review.getUid() + " from book " + bookID);

        getReviews().remove(review.getUid());

        int pos = 0;
        for (int i = 0; i < getOrderedReviews().size(); i++){
            if (getOrderedReviews().get(i).getUid().equals(review.getUid())){
                pos = i;
                break;
            }
        }
        getOrderedReviews().remove(pos);
        return pos;
    }
    private boolean containsUserReview(){
        return !getOrderedReviews().isEmpty() && getOrderedReviews().get(0).isCurrentUserReview();
    }
    public void clearReviews(){
        reviews = null;
        orderedReviews = null;
    }

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


    private String shortenString(String s, int length){
        if (s.length() <= length)
            return s;
        return s.substring(0, length) + "...";
    }
    @Exclude
    public String getBookShortInfo(){
        return shortenString(title, 15) + "\n" + shortenString(author, 15);
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

    public void setReviews(Map<String, Review> reviews) {

        this.reviews = reviews;
    }


    public Map<String, Review> getReviews() {
        if (reviews == null)
            reviews = new LinkedHashMap<>();
        return reviews;
    }


}
