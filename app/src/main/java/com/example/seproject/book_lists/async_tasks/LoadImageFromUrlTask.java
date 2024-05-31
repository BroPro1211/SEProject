package com.example.seproject.book_lists.async_tasks;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.seproject.data_classes.Book;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageFromUrlTask extends AsyncTask<String, Void, Bitmap> {
    public static class FailedToLoadImageException extends RuntimeException{}

    private Book book;


    // these fields cause a memory leakage, however an AsyncTask is short lived, on the order of
    // at most a couple of seconds, and so even if the task finishes after the views are destroyed,
    // they will still be garbage collected quickly after, once the task finishes
    @SuppressLint("StaticFieldLeak")
    private ImageView imageView;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;

    public LoadImageFromUrlTask(Book book, ImageView imageView, ProgressBar progressBar) {
        this.book = book;
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];

        HttpURLConnection connection;
        try {
            URL connectionUrl = new URL(urlString);
            Log.d("SEProject", "Downloading image from url "+ connectionUrl.toString());
            connection = (HttpURLConnection) connectionUrl.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK){
                Log.d("SEProject", "HTTP response code not OK: " + responseCode);
                throw new LoadImageFromUrlTask.FailedToLoadImageException();
            }

        } catch (MalformedURLException e) {
            Log.d("SEProject", "MalformedURLException occurred for " + urlString, e);
            throw new LoadImageFromUrlTask.FailedToLoadImageException();
        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new LoadImageFromUrlTask.FailedToLoadImageException();
        }

        Log.d("SEProject", "HTTP connection successfully established, getting image data");

        try {
            connection.connect();

            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new LoadImageFromUrlTask.FailedToLoadImageException();
        } finally {
            connection.disconnect();
        }

    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        Log.d("SEProject", "Successfully downloaded image");

        book.saveImage(result);

        book.setImage(imageView, progressBar);
    }
}
