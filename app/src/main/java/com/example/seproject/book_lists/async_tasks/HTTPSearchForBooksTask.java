package com.example.seproject.book_lists.async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * AsyncTask to get book search results from google books api
 */
public class HTTPSearchForBooksTask extends AsyncTask<String, Void, String> {
    /**
     * Interface for the results receiver to call a method with the results
     */
    public interface SearchResultsReceiver{
        /**
         * Method to call with the search results
         * @param result Book search results
         */
        void getSearchResults(String result);
    }

    /**
     * Exception to throw when failed to search for the book
     */
    public static class FailedToSearchBooksException extends RuntimeException{}

    private final SearchResultsReceiver receiver;

    /**
     * Constructor for HTTPSearchForBooksTask
     * @param receiver Search results receiver to call when done
     */
    public HTTPSearchForBooksTask(SearchResultsReceiver receiver){
        super();
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlString = strings[0];

        HttpURLConnection apiConnection;
        try {
            URL urlGetRequest = new URL(urlString);
            apiConnection = (HttpURLConnection) urlGetRequest.openConnection();

            int responseCode = apiConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK){
                Log.d("SEProject", "HTTP response code not OK: " + responseCode);
                throw new FailedToSearchBooksException();
            }

        } catch (MalformedURLException e) {
            Log.d("SEProject", "MalformedURLException occurred for " + urlString, e);
            throw new FailedToSearchBooksException();
        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new FailedToSearchBooksException();
        }

        Log.d("SEProject", "HTTP connection successfully established, getting data");

        try {
            apiConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String inputLine = in.readLine();
            while (inputLine != null) {
                response.append(inputLine);
                inputLine = in.readLine();
            }
            in.close();

            Log.d("SEProject", "Returning data");
            return response.toString();

        } catch (IOException e) {
            Log.d("SEProject", "IOException occurred for " + urlString, e);
            throw new FailedToSearchBooksException();
        } finally {
            apiConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        receiver.getSearchResults(s);
    }
}
