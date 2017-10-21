package com.fablwesn.www.discovergooglebooks;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for misc. tasks
 */
class MiscUtils {

    private static final String LOG_TAG = MiscUtils.class.getName();

    // if one of the requested items can't be found in the json parsing process, display this instead
    private static final String JSON_EMPTY_STRING_REPLACEMENT = "N/A";
    // if more than one author exists, put this sign between them for separation
    private static final String JSON_MULTIPLE_AUTHORS_SEPARATOR = ", ";

    // key names of the Google Books Web API response used for parsing
    private static final String JSON_PARSE_MAIN_ITEMS = "items";
    private static final String JSON_PARSE_ARRAY_VOLUME_INFO = "volumeInfo";
    private static final String JSON_PARSE_ITEM_TITLE = "title";
    private static final String JSON_PARSE_ARRAY_AUTHORS = "authors";
    private static final String JSON_PARSE_ITEM_PUBLISHER = "publisher";
    private static final String JSON_PARSE_ARRAY_COVER_URL = "imageLinks";
    private static final int JSON_PARSE_ARRAY_POSITION_COVER_URL = 1;
    private static final String JSON_PARSE_ITEM_DIRECT_LINK_URL = "infoLink";

    /**
     * display a centered toast showing the passed String
     *
     * @param context      activity context
     * @param toastMessage message to display
     */
    static void displayCenteredToast(Context context, String toastMessage) {
        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * check if enough characters have been entered
     *
     * @param query user input
     * @return true/false
     */
    static boolean isQueryLongEnough(String query) {
        return query.replace(" ", "").length() >= MainActivity.MIN_CHARS_FOR_SEARCH;
    }

    /**
     * check if user input text is valid
     *
     * @param query user input
     * @return true/false
     */
    static boolean isQueryValid(String query) {
        String patternName = "[a-zA-z.]+([ '-][a-zA-Z.]+)*";
        Pattern pattern = Pattern.compile(patternName);
        Matcher matcher = pattern.matcher(query);

        return matcher.matches();
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole (validated) JSON response from the server
     */
    static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link BookModel} objects that has been built up from
     * parsing the given JSON response. TODO
     */
    static List<BookModel> parseResultsJson(String searchResultsJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(searchResultsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<BookModel> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(searchResultsJson);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray itemsArray = baseJsonResponse.getJSONArray(JSON_PARSE_MAIN_ITEMS);

            // For each volume in the itemsArray, create an {@link BookModel} object
            for (int i = 0; i < itemsArray.length(); i++) {
                // Get a single book at position i w/in the list of books
                JSONObject currentItem = itemsArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all volumeInfo
                // for that book.
                JSONObject volumeInfo = currentItem.getJSONObject(JSON_PARSE_ARRAY_VOLUME_INFO);

                // get the title
                String title = volumeInfo.getString(JSON_PARSE_ITEM_TITLE);
                // get the publisher
                String publisher = volumeInfo.getString(JSON_PARSE_ITEM_PUBLISHER);
                // get the external info link
                String infoUrl = volumeInfo.getString(JSON_PARSE_ITEM_DIRECT_LINK_URL);

                // get the author(s)
                String author = "";
                JSONArray authorsArray;
                if (volumeInfo.has(JSON_PARSE_ARRAY_AUTHORS)) {
                    authorsArray = volumeInfo.getJSONArray(JSON_PARSE_ARRAY_AUTHORS);

                    int authLength = authorsArray.length();
                    // if authorsArray is not found(<1) respond accordingly in JSONArray("authors")
                    if (authLength < 1) {
                        author = JSON_EMPTY_STRING_REPLACEMENT;
                        // if only one authorsArray is found(==1) respond authors name(0 Array) in JSONArray("authors")
                    } else if (authLength == 1) {
                        author = authorsArray.getString(0);
                        // if more than one authorsArray's are found(>1) then PrintOut all the authors name's in JSONArray("authors")
                    } else {
                        for (int j = 0; j < authorsArray.length(); j++) {
                            if (j == 0) {
                                author += authorsArray.getString(j);
                                continue;
                            }
                            author += JSON_MULTIPLE_AUTHORS_SEPARATOR + authorsArray.getString(j);
                        }
                    }
                }

                // get the link to the preview Image load from
                String coverUrl = "";

                //if found, assign the correct url - leave empty if not
                if (volumeInfo.has(JSON_PARSE_ARRAY_COVER_URL)) {
                    JSONArray thumbnailUrls = volumeInfo.getJSONArray(JSON_PARSE_ARRAY_COVER_URL);
                    coverUrl = thumbnailUrls.getString(JSON_PARSE_ARRAY_POSITION_COVER_URL);
                }

                BookModel book = new BookModel(title, author, publisher, coverUrl, infoUrl);

                // Add the new {@link BookModel} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block
            Log.e(LOG_TAG, "Problem parsing the volume JSON results", e);
        }

        // Return the list of books
        return books;
    }
}
