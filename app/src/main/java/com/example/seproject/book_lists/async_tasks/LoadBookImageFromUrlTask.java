package com.example.seproject.book_lists.async_tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.seproject.data_classes.Book;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * AsyncTask to load the image of a book from a url
 */
public class LoadBookImageFromUrlTask extends AsyncTask<String, Void, Bitmap> {
    /**
     * Exception to throw when image download failed
     */
    public static class FailedToLoadImageException extends RuntimeException{}

    private final Book book;
    private final Book.BookImageReceiver receiver;
    private final Context context;

    /**
     * Constructor for LoadBookImageFromUrlTask
     * @param context The context
     * @param book The book to download the image for
     * @param receiver The image receiver
     */

    public LoadBookImageFromUrlTask(Context context, Book book, Book.BookImageReceiver receiver) {
        this.context = context;
        this.book = book;
        this.receiver = receiver;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];

        HttpURLConnection connection;
        try {
            URL connectionUrl = new URL(urlString);
            Log.d("SEProject", "Downloading image from url "+ connectionUrl);
            connection = (HttpURLConnection) connectionUrl.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK){
                Log.d("SEProject", "HTTP response code not OK: " + responseCode);
                throw new LoadBookImageFromUrlTask.FailedToLoadImageException();
            }

        } catch (MalformedURLException e) {
            Log.d("SEProject", "MalformedURLException occurred for " + urlString, e);
            throw new LoadBookImageFromUrlTask.FailedToLoadImageException();
        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new LoadBookImageFromUrlTask.FailedToLoadImageException();
        }

        Log.d("SEProject", "HTTP connection successfully established, getting image data");

        try {
            connection.connect();

            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new LoadBookImageFromUrlTask.FailedToLoadImageException();
        } finally {
            connection.disconnect();
        }

    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        Log.d("SEProject", "Successfully downloaded image");

        book.saveImage(context, result, receiver);
    }
}
