package com.fablwesn.www.discovergooglebooks;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

/**
 * Starts a search with the user input from {@link MainActivity} and responds correspondingly
 */
public class ResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookModel>> {

    // replace character for empty queries used for the toolbar label
    final static String EMPTY_LABEL_CHAR = "-";
    // default value for search results to display
    final static String DEFAULT_RESULTS_QUANTITY = "5";
    // default value for search result's sorting type
    final static String DEFAULT_RESULTS_ORDER = "relevance";

    // constructed url to be loaded from
    private URL requestUrl;

    // list containing all books to display
    List<BookModel> displayedBookList;

    // RecyclerView displaying the list
    private RecyclerView recyclerView;
    // recycler view's adapter
    private ResultsListAdapter recyclerViewAdapter;
    // bundle used to save the list's scroll position
    private static Bundle bundleRecyclerViewState;

    // loading indicator
    private ProgressBar loadingIndicator;
    // error/info/empty list text view
    private TextView noListText;

    /* onCreate
    *  - gets extras
    *  - sets up toolbar and label
    *  - starts building the url
    *  - prepare recycler view and it's adapter
     * - begins loading data if no problems occurred
    **********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // get stored extra to find out if we are starting a default or advanced search
        Bundle extras = getIntent().getExtras();
        final boolean isAdvancedSearch = extras.getBoolean(getResources().getString(R.string.extra_key_bool_advanced_search), false);

        // set up the custom toolbar
        setUpToolbar(isAdvancedSearch, extras);
        // prepare the loading indicator and the text view for state feedback
        setLoadingView();

        // start the corresponding building of the URL, depending on the search type
        if (!isAdvancedSearch)
            requestUrl = UriUtils.buildDefaultUrl(
                    extras.getString(getResources().getString(R.string.extra_key_string_default_input), ""),
                    DEFAULT_RESULTS_QUANTITY,
                    DEFAULT_RESULTS_ORDER);
        else
            requestUrl = UriUtils.buildAdvancedUrl(
                    extras.getStringArray(getResources().getString(R.string.extra_key_string_array_adv_input)),
                    extras.getString(getResources().getString(R.string.extra_key_string_max_results), ""),
                    extras.getString(getResources().getString(R.string.extra_key_string_sort_by), ""));

        // if we failed building null is returned - display an error in that case and return early
        if (requestUrl == null) {
            loadingIndicator.setVisibility(View.GONE);
            noListText.setText(R.string.error_build_url);
            return;
        }

        //prepare the Recycler to receive an adapter after loading finishes
        recyclerView = findViewById(R.id.results_content_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    /* onPause
    *   - saves recycler view scroll position (on screen orientation change for example)
    **********************************************************************/
    @Override
    protected void onPause()
    {
        super.onPause();
        // save RecyclerView state
        bundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        bundleRecyclerViewState.putParcelable(getResources().getString(R.string.extra_key_recycler_state), listState);
    }

    /* onResume
    *   - retrieve previous recycler view scroll position (on screen orientation change for example)
    **********************************************************************/
    @Override
    protected void onResume()
    {
        super.onResume();

        // restore RecyclerView state
        if (bundleRecyclerViewState != null) {
            Parcelable listState = bundleRecyclerViewState.getParcelable(getResources().getString(R.string.extra_key_recycler_state));
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    /* onCreateLoader
    *   - called when a new Loader needs to be created
    **********************************************************************/
    @Override
    public Loader<List<BookModel>> onCreateLoader(int i, Bundle bundle) {
        return new BooksLoader(ResultsActivity.this, requestUrl);
    }

    /* onLoadFinished
     *  - update views accordingly
     *  - update list adapter
     *********************************************************************/
    @Override
    public void onLoadFinished(Loader<List<BookModel>> loader, List<BookModel> books) {

        // Hide progress indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous book data
        clearBookAdapter();

        // If there is a valid list of {@link BookModel}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            noListText.setVisibility(View.GONE);
            recyclerViewAdapter = new ResultsListAdapter(books);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        // if empty, inform the user
        else {
            noListText.setText(R.string.error_no_results);
        }
    }

    /* onLoaderReset
    *   - clears the adapter to make room for new data
    *************************************************************/
    @Override
    public void onLoaderReset(Loader<List<BookModel>> loader) {
        clearBookAdapter();
    }


    /*                                      prepare UI                                            */
    /*                                      ¯¯¯¯¯¯¯¯¯¯                                            */

    /**
     * sets up the toolbar and it's title, depending on the search type
     *
     * @param isAdvancedSearch if it's a default or advanced search
     * @param extras           stored values from previous activity
     */
    private void setUpToolbar(boolean isAdvancedSearch, Bundle extras) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        final String title;
        // set the toolbar's title in default search mode
        if (!isAdvancedSearch) {
            // set the title and pass the search query from the user into the string to us it inside the title
            title = getResources().getString(R.string.results_label, extras.getString(getResources().getString(R.string.extra_key_string_default_input), ""));
            toolbar.setTitle(title);
        }
        // set the title in advanced search mode
        else {
            //set the title with user inputs
            String[] queryInputs = extras.getStringArray(getResources().getString(R.string.extra_key_string_array_adv_input));

            if (queryInputs != null) {
                //replace any empty item with a EMPTY_LABEL_CHAR char
                for (int i = 0; i < queryInputs.length; i++) {
                    if (queryInputs[i].isEmpty())
                        queryInputs[i] = EMPTY_LABEL_CHAR;
                }
                title = getResources().getString(R.string.results_label_adv, queryInputs[0], queryInputs[1], queryInputs[2]);
            }
            // use the default app name in case something goes wrong
            else
                title = getResources().getString(R.string.app_name);

            toolbar.setTitle(title);
        }

        //enable custom toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * initialize views displayed when there is no list visible and sets the loading text
     */
    private void setLoadingView() {
        loadingIndicator = findViewById(R.id.results_content_progressbar);
        noListText = findViewById(R.id.results_content_message_txt);

        noListText.setText(R.string.info_loading);
    }


    /*                                   private functions                                        */
    /*                                   ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯                                        */

    /**
     * equivalent of list's .clear() for the recycler view
     */
    private void clearBookAdapter(){
        if(displayedBookList != null){
            displayedBookList.clear();
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

}
