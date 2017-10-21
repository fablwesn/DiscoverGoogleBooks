package com.fablwesn.www.discovergooglebooks;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class for creating and manipulating string links for online json requests
 */
class UriUtils {

    //tag used for debugging
    static final String LOG_TAG = UriUtils.class.getName();

    /**
     * url parts resulting in "https://www.googleapis.com/books/v1/volumes?q=SEARCH+TERM&maxResults=PARAM_MAX&orderBy=PARAM_ORDER"
     */
    private final static String REQUEST_URL_RAW = "https://www.googleapis.com/books/v1/volumes?q=";
    private final static String REQUEST_URL_QUERY_PARAM_MAX = "&maxResults=";
    private final static String REQUEST_URL_QUERY_PARAM_ORDER = "&orderBy=";
    private final static String REQUEST_URL_QUERY_SEARCH_TITLE = "+intitle:";
    private final static String REQUEST_URL_QUERY_SEARCH_AUTHOR = "+inauthor:";
    private final static String REQUEST_URL_QUERY_SEARCH_PUBLISHER = "+inpublisher:";

    // the other possible param besides the default one for sorting order
    // used to be compared to the spinner selection in the advanced request url building
    private final static String RESULTS_ORDER_PARAM_NEWEST = "newest";


    /**
     * returns an URL in format of
     * "https://www.googleapis.com/books/v1/volumes?q=searchQuery&maxResults=resultQuantity&orderBy=orderBy"
     *
     * @param searchQuery    user search input
     * @param resultQuantity default max results to display
     * @param orderBy        default sort type
     * @return URL to be used for the API request
     */
    static URL buildDefaultUrl(String searchQuery, String resultQuantity, String orderBy) {
        URL requestURL;

        // create String url
        String requestString = REQUEST_URL_RAW +
                searchQuery.replace(" ", "+") +
                REQUEST_URL_QUERY_PARAM_MAX +
                resultQuantity +
                REQUEST_URL_QUERY_PARAM_ORDER +
                orderBy;

        // create url and return null on fail
        try {
            requestURL = new URL(requestString);
            return requestURL;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "buildDefaultUrl(): Problem building the URL ", e);
            return null;
        }
    }

    /**
     * returns an URL in format of
     * "https://www.googleapis.com/books/v1/volumes?q=+intitle:titleQuery+inauthor:authorQuery+inpublisher:publisherQuery&maxResults=resultQuantity&orderBy=orderBy"
     * (one of 3^3 possibilities, depending on how many of the three edit texts have been filled with valid input)
     *
     * @param searchQueries  user search input/s
     * @param resultQuantity selected max results to display or default if not selected
     * @param orderBy        selected sort type or default if not selected
     * @return URL to be used for the API request
     */
    static URL buildAdvancedUrl(String[] searchQueries, String resultQuantity, String orderBy) {
        // url to return if everything goes well
        URL requestURL;

        /* extract the search queries from the array
        *   first position is the title edit text input
        *   second the author edit text input
        *   third and last the publisher  edit text input
        * */
        String titleQuery = searchQueries[0].trim();
        String authorQuery = searchQueries[1].trim();
        String publisherQuery = searchQueries[2].trim();

        // spinner entries after being checked against non-selection
        String maxResults;
        String sortBy;

        // check if its a numerical value (0-9), if it's not then the spinner hasn't been used and we can use the default value
        if (!resultQuantity.matches("\\d+(?:\\.\\d+)?"))
            maxResults = ResultsActivity.DEFAULT_RESULTS_QUANTITY;
        else
            maxResults = resultQuantity;

        // check the second spinner, in case it doesn't match the two possible values, use the default value
        if (!orderBy.contentEquals(ResultsActivity.DEFAULT_RESULTS_ORDER) && !orderBy.contentEquals(RESULTS_ORDER_PARAM_NEWEST))
            sortBy = ResultsActivity.DEFAULT_RESULTS_ORDER;
        else
            sortBy = orderBy;


        // create a new builder and build the puzzle
        StringBuilder builder = new StringBuilder(REQUEST_URL_RAW);

        // add search values for a title search if one is requested
        if(!titleQuery.isEmpty() && !titleQuery.contentEquals(ResultsActivity.EMPTY_LABEL_CHAR)){
            builder.append(REQUEST_URL_QUERY_SEARCH_TITLE)
                    .append(titleQuery.replace(" ", "+"));
        }

        // add search values for an author search if one is requested
        if(!authorQuery.isEmpty() && !authorQuery.contentEquals(ResultsActivity.EMPTY_LABEL_CHAR)){
            builder.append(REQUEST_URL_QUERY_SEARCH_AUTHOR)
                    .append(authorQuery.replace(" ", "+"));
        }

        // add search values for a publisher search if one is requested
        if(!publisherQuery.isEmpty() && !publisherQuery.contentEquals(ResultsActivity.EMPTY_LABEL_CHAR)){
            builder.append(REQUEST_URL_QUERY_SEARCH_PUBLISHER)
                    .append(publisherQuery.replace(" ", "+"));
        }

        // add the maxResults and sortBy parameters
        builder.append(REQUEST_URL_QUERY_PARAM_MAX).append(maxResults)
                .append(REQUEST_URL_QUERY_PARAM_ORDER).append(sortBy);

        // convert to string
        String builtUrl = builder.toString();

        // create url and return null on fail
        try {
            requestURL = new URL(builtUrl);
            return requestURL;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "buildDefaultUrl(): Problem building the URL ", e);
            return null;
        }
    }
}
