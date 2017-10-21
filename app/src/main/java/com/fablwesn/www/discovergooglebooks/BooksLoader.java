package com.fablwesn.www.discovergooglebooks;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.net.URL;
import java.util.List;

/**
 * Loader class to fetch the search query results from Google Books' Web API
 */
class BooksLoader extends AsyncTaskLoader<List<BookModel>> {

    // url the load bases upon
    private URL url;

    /* Class constructor */
    BooksLoader(Context context, URL url) {
        super(context);
        this.url = url;
    }

    /* onStartLoading
    *   - starts the loading task
    ***************************************/
    @Override
    protected void onStartLoading() {
        // start the load
        forceLoad();
    }


    /* loadInBackground
    *   - background thread
    ***************************************/
    @Override
    public List<BookModel> loadInBackground() {
        if (url == null) {
            return null;
        }

        // Perform network request, parse the response, and extract a list of books
        // matching search criteria
        return NetworkUtils.fetchSearchResults(url);
    }
}
