package com.example.seproject.data_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.seproject.R;
import com.example.seproject.book_lists.async_tasks.LoadImageFromUrlTask;
import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.List;

public class Book {
    private String bookID;
    private String title;
    private String author;
    private String description;
    private String genre;
    private int pageCount;
    private String imageLink;
    private List<Review> reviews;
    private Bitmap bookImage;


    private ProgressBar detailsDialogProgressBar;
    private ImageView detailsDialogImageView;


    public Book(String bookID, String title, String author, String description, String genre, int pageCount, String imageLink) {
        if (Arrays.asList(bookID, title, author, description, genre, imageLink).contains(null))
            throw new RuntimeException("Book cannot have null field");
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.pageCount = pageCount;
        this.imageLink = imageLink;
    }



    @Exclude
    public void getAndSetImage(Context context, ImageView imageView, ProgressBar progressBar){
        Log.d("SEProject", "Getting book image");

        // checks if the image is already loaded, in the case of a book stored in a list
        if (bookImage != null)
            setImage(imageView, progressBar);
        else {
            if (imageLink != null && imageLink.length() != 0){
                LoadImageFromUrlTask task = new LoadImageFromUrlTask(this, imageView, progressBar);
                try{
                    // task downloads image, saves it to this.bookImage and calls setImage
                    task.execute(imageLink);
                } catch (LoadImageFromUrlTask.FailedToLoadImageException e){
                    Log.d("SEProject", "Image download from " + imageLink + " failed");

                    // if the task failed, the default book icon will be applied
                    getAndSetDefaultImage(context, imageView, progressBar);

                }
            }
            else
                getAndSetDefaultImage(context, imageView, progressBar);
        }

    }

    private void getAndSetDefaultImage(Context context, ImageView imageView, ProgressBar progressBar){
        Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.baseline_book_75);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        bookImage = bitmap;

        setImage(imageView, progressBar);
    }

    @Exclude
    public void saveImage(Bitmap image){
        this.bookImage = image;

        if (detailsDialogImageView != null && detailsDialogProgressBar != null){
            setImage(detailsDialogImageView, detailsDialogProgressBar);
        }
    }

    @Exclude
    public void setImage(ImageView imageView, ProgressBar progressBar){
        if (bookImage == null)
            return;

        imageView.setImageBitmap(bookImage);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Exclude
    public void setDetailsDialogProgressBar(ProgressBar progressBar){
        this.detailsDialogProgressBar = progressBar;
    }
    @Exclude
    public void setDetailsDialogImageView(ImageView imageView){
        this.detailsDialogImageView = imageView;
    }
    @Exclude
    public ProgressBar getDetailsDialogProgressBar() {
        return detailsDialogProgressBar;
    }
    @Exclude
    public ImageView getDetailsDialogImageView() {
        return detailsDialogImageView;
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


    public List<Review> getReviews() {
        return reviews;
    }
}
