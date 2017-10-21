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

    //Tag for logging
    private static final String LOG_TAG = MiscUtils.class.getName();
    //Error message for parsing
    private static final String ERROR_PARSING = "Problem parsing the results. Please inform feedback@fablwesn.com";

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
    private static final String JSON_PARSE_ITEM_RELEASE_YEAR = "publishedDate";
    private static final String JSON_PARSE_OBJECT_IMAGE_URL = "imageLinks";
    private static final String JSON_PARSE_STRING_THUMBNAIL_URL = "smallThumbnail";
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
     * parsing the given JSON response.
     */
    static List<BookModel> parseResultsJson(String searchResultsJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(searchResultsJson)) {
            return null;
        }

        // Create an empty ArrayList used to add books
        List<BookModel> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(searchResultsJson);

            if (baseJsonResponse.has(JSON_PARSE_MAIN_ITEMS)) {
                JSONArray bookArray = baseJsonResponse.getJSONArray(JSON_PARSE_MAIN_ITEMS);

                for (int i = 0; i < bookArray.length(); i++) {

                    JSONObject currentBook = bookArray.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject(JSON_PARSE_ARRAY_VOLUME_INFO);

                    // get title
                    String title = volumeInfo.getString(JSON_PARSE_ITEM_TITLE);

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

                    // get publisher
                    String publisher = "";
                    if (volumeInfo.has(JSON_PARSE_ITEM_PUBLISHER))
                        publisher = volumeInfo.getString(JSON_PARSE_ITEM_PUBLISHER);

                    // get release year
                    String releaseYear = "";
                    if (volumeInfo.has(JSON_PARSE_ITEM_RELEASE_YEAR))
                        releaseYear = volumeInfo.getString(JSON_PARSE_ITEM_RELEASE_YEAR);

                    // get cover img url
                    String thumbnail = "";
                    if (volumeInfo.has(JSON_PARSE_OBJECT_IMAGE_URL)) {
                        JSONObject imageLinks = volumeInfo.getJSONObject(JSON_PARSE_OBJECT_IMAGE_URL);
                        if (imageLinks.has(JSON_PARSE_STRING_THUMBNAIL_URL))
                            thumbnail = imageLinks.getString(JSON_PARSE_STRING_THUMBNAIL_URL);
                    }

                    // get info link
                    String directLink = "";
                    if (volumeInfo.has(JSON_PARSE_ITEM_DIRECT_LINK_URL))
                        directLink = volumeInfo.getString(JSON_PARSE_ITEM_DIRECT_LINK_URL);


                    // Create a new {@link Book} object with parameters obtained from JSON response
                    BookModel book = new BookModel(
                            title,
                            author,
                            publisher,
                            releaseYear,
                            thumbnail,
                            directLink
                    );

                    // Add the new {@link Book} object to the list of books
                    books.add(book);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, ERROR_PARSING, e);
        }

        // Return the list of books
        return books;
    }
}
