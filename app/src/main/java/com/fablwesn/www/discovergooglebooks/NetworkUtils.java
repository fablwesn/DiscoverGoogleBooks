package com.fablwesn.www.discovergooglebooks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.fablwesn.www.discovergooglebooks.MiscUtils.parseResultsJson;
import static com.fablwesn.www.discovergooglebooks.MiscUtils.readFromStream;

/**
 * Utility class for everything relating to network
 */
class NetworkUtils {

    //Tag for logging
    private static final String LOG_TAG = NetworkUtils.class.getName();
    //Error message for http request
    private static final String ERROR_HTTP = "Problem making the HTTP request. Please inform feedback@fablwesn.com";
    //Error message for error response code
    private static final String ERROR_RESPONSE = "Server Error! Response Code: ";
    //Error message for problems with retrieved json
    private static final String ERROR_JSON = "Problem retrieving the results. Please try again later or inform feedback@fablwesn.com";

    /**
     * check's if the device has internet connectivity
     *
     * @param context current context
     * @return true if there is network access, false if the device is offline
     */
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * create the needed data for the list view
     *
     * @param url   request url
     *
     * @return  list containing {@link BookModel} objects from requested search query
     */
    static List<BookModel> fetchSearchResults(URL url){

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, ERROR_HTTP, e);
        }

        // Return the list of {@link BookModel}s
        return parseResultsJson(jsonResponse);
    }

    /**
     * Make a HTTP request to the given URL and return a String containing the json response
     *
     * @param url   url to get the response from
     *
     * @return  string containing the response as json
     *
     * @throws IOException closing the input stream could throw an IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        // try connecting to the url
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERROR_RESPONSE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, ERROR_JSON, e);
        } finally {
            // close the connection if we didn't get the response we needed
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        // return the response string
        return jsonResponse;
    }
}
