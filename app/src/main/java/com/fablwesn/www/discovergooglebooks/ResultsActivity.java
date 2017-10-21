package com.fablwesn.www.discovergooglebooks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

/**
 * Starts a search with the user input from {@link MainActivity} and responds correspondingly
 */
public class ResultsActivity extends AppCompatActivity {

    // replace character for empty queries used for the toolbar label
    final static String EMPTY_LABEL_CHAR = "-";
    // default value for search results to display
    final static String DEFAULT_RESULTS_QUANTITY = "5";
    // default value for search result's sorting type
    final static String DEFAULT_RESULTS_ORDER = "relevance";

    // loading indicator
    ProgressBar loadingIndicator;
    // error/info/empty list text view
    TextView noListText;

    /* onCreate
    *  - gets extras
    *  - sets up toolbar and label
    *  - starts building the url and begins loading data if no problems occurred
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

        //url to use for the request
        final URL requestUrl;

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

        //TODO:REMOVE
        loadingIndicator.setVisibility(View.GONE);
        noListText.setText(requestUrl.toString());
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
                    if(queryInputs[i].isEmpty())
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

}
