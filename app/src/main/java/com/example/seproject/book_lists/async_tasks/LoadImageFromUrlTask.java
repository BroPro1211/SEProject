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

public class LoadImageFromUrlTask extends AsyncTask<String, Void, Bitmap> {
    public static class FailedToLoadImageException extends RuntimeException{}

    private final Book book;
    private final Book.BookImageReceiver receiver;
    private final Context context;

    public LoadImageFromUrlTask(Context context, Book book, Book.BookImageReceiver receiver) {
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

        book.saveImage(context, result, receiver);
    }
}
