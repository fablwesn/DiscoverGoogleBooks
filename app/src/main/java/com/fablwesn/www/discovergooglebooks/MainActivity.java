package com.fablwesn.www.discovergooglebooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import static com.fablwesn.www.discovergooglebooks.MiscUtils.displayCenteredToast;
import static com.fablwesn.www.discovergooglebooks.MiscUtils.isQueryLongEnough;
import static com.fablwesn.www.discovergooglebooks.MiscUtils.isQueryValid;

/**
 * mainly responsible for setting up the views so the user can interact with them and for the correct
 * building of the request uri
 */
public class MainActivity extends AppCompatActivity {

    // min required characters typed to start a search
    final static int MIN_CHARS_FOR_SEARCH = 2;

    // stores the user input from the default search view
    private String userMainQuery = "";

    // text view informing the user about errors or giving tips
    private TextView infoTxtV;

    /* onCreate
     *  - sets up toolbar
     *  - prepares ui elements and listeners
     *  - checks for internet connection and displays info text accordingly
     **********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // activate custom toolbar
        setUpToolbar();
        // prepare default search ui elements
        prepareDefaultSearch();
        // fill in spinner content
        fillSpinner();

        // set appropriate welcome message, depending on user's network state
        infoTxtV = findViewById(R.id.main_content_txt_message);
        if (!NetworkUtils.isNetworkAvailable(MainActivity.this))
            infoTxtV.setText(R.string.note_device_offline);
        else
            infoTxtV.setText(R.string.note_greeting);
    }


    /*                                      prepare UI                                            */
    /*                                      ¯¯¯¯¯¯¯¯¯¯                                            */

    /**
     * set custom toolbar
     */
    private void setUpToolbar() {
        //enable custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //remove up navigation because it is not needed in the parent activity
        toolbar.setNavigationIcon(null);
    }

    /**
     * sets up search view and it's listener
     */
    private void prepareDefaultSearch() {
        // find the view
        SearchView searchView = findViewById(R.id.main_content_searchview);
        // get it's edit text to center the hint and text
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setGravity(Gravity.CENTER);
        // expand it by default
        searchView.setIconified(false);

         /* disable the collapsing ability of the searchView by returning true */
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });

        // set a listener to the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /* on submit: initiate default search */
            @Override
            public boolean onQueryTextSubmit(String query) {
                initiateDefaultSearch();
                return false;
            }

            /* after every entry/deletion of input: save input to var */
            @Override
            public boolean onQueryTextChange(String newText) {
                userMainQuery = newText;
                return false;
            }
        });
    }

    /**
     * fill spinner with items from array
     */
    private void fillSpinner() {
        //find both spinners
        final Spinner spinnerResult = findViewById(R.id.main_content_adv_spinner_results);
        final Spinner spinnerOrder = findViewById(R.id.main_content_adv_spinner_order);

        // arrays including the items to fill in
        String[] resultsItems = getResources().getStringArray(R.array.main_advanced_spinner_max_results);
        String[] orderItems = getResources().getStringArray(R.array.main_advanced_spinner_order_by);

        // Create adapter for spinner
        ArrayAdapter<String> resultsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, resultsItems);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderItems);

        // set the drop down layout style
        resultsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerResult.setAdapter(resultsAdapter);
        spinnerOrder.setAdapter(orderAdapter);
    }


    /*                                   private functions                                        */
    /*                                   ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯                                        */

    /**
     * verifies if the user input is valid and starts the {@link ResultsActivity} after storing needed values
     */
    private void initiateDefaultSearch() {

        //if connection has been lost since the start of the app, inform the user and return early.
        if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            infoTxtV.setText(R.string.note_device_offline);
            return;
        }
        // if the input query is too short, display a toast message and return early.
        if (!isQueryLongEnough(userMainQuery)) {
            displayCenteredToast(MainActivity.this, getResources().getString(R.string.toast_empty_request_main, MIN_CHARS_FOR_SEARCH));
            return;
        }
        // if the input query contains invalid characters, display a toast message and return early.
        if (!isQueryValid(userMainQuery)) {
            displayCenteredToast(MainActivity.this, getResources().getString(R.string.toast_invalid_request_main));
            return;
        }

        // if everything is valid, start the next activity after storing the query and search type
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);

        // store needed variables for the next activity
        intent.putExtra(getResources().getString(R.string.extra_key_bool_advanced_search), false);
        intent.putExtra(getResources().getString(R.string.extra_key_string_default_input), userMainQuery);

        //start activity
        startActivity(intent);
    }

    /**
     * check if the entry/entries is/are valid and pass all needed values to {@link ResultsActivity} we are starting
     */
    private void initiateAdvancedSearch() {
        // find views to get the content of the edit texts inside the advanced search view
        final EditText titleEdTxt = findViewById(R.id.main_content_adv_edtxt_title);
        final EditText authorEdTxt = findViewById(R.id.main_content_adv_edtxt_author);
        final EditText publisherEdTxt = findViewById(R.id.main_content_adv_edtxt_publisher);

        // check if the input is long enough, return and display a toast if not, continue if it is
        if (!isQueryLongEnough(titleEdTxt.getText().toString())
                && !isQueryLongEnough(authorEdTxt.getText().toString())
                && !isQueryLongEnough(publisherEdTxt.getText().toString())) {
            displayCenteredToast(MainActivity.this, getResources().getString(R.string.toast_empty_request_adv_main, MIN_CHARS_FOR_SEARCH));
            return;
        }

        // store the inputs in an array
        String[] advancedQueries = createAdvancedQuery(titleEdTxt.getText().toString(), authorEdTxt.getText().toString(), publisherEdTxt.getText().toString());

        // if the array is null, none of the entries were valid and we can display an error toast and return early.
        if (advancedQueries == null) {
            displayCenteredToast(MainActivity.this, getResources().getString(R.string.toast_invalid_request_main));
            return;
        }

        // If the input is valid we want to get the spinner selections, for that we need the views
        final Spinner quantitySpinner = findViewById(R.id.main_content_adv_spinner_results);
        final Spinner orderTypeSpinner = findViewById(R.id.main_content_adv_spinner_order);

        // if everything is valid, start the next activity after storing the query, spinner selections and search type
        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);

        // store needed variables for the next activity
        intent.putExtra(getResources().getString(R.string.extra_key_bool_advanced_search), true);
        intent.putExtra(getResources().getString(R.string.extra_key_string_array_adv_input), advancedQueries);
        intent.putExtra(getResources().getString(R.string.extra_key_string_max_results), quantitySpinner.getSelectedItem().toString());
        intent.putExtra(getResources().getString(R.string.extra_key_string_sort_by), orderTypeSpinner.getSelectedItem().toString());

        //start activity
        startActivity(intent);
    }

    /**
     * check's if at least one of the inputs is valid
     *
     * @param title     user input in the title search edit text
     * @param author    user input in the author search edit text
     * @param publisher user input in the publisher search edit text
     * @return null if no entries are valid, if at least one is return a String[] holding all valid entries and empty for invalid ones
     */
    private String[] createAdvancedQuery(String title, String author, String publisher) {
        // holding the validated query inputs
        String validTitleQuery = "";
        String validAuthorQuery = "";
        String validPublisherQuery = "";

        /* checks to see if the strings are valid, if not don't use them
           check for the length again as we only want to include those long enough*/
        if (isQueryLongEnough(title)) {
            if (isQueryValid(title))
                validTitleQuery = title;
        }

        if (isQueryLongEnough(author)) {
            if (isQueryValid(author))
                validAuthorQuery = author;
        }

        if (isQueryLongEnough(publisher)) {
            if (isQueryValid(publisher))
                validPublisherQuery = publisher;
        }

        // return null if no strings were accepted
        if (validTitleQuery.isEmpty() && validAuthorQuery.isEmpty() && validPublisherQuery.isEmpty())
            return null;

        // create an array containing all
        return new String[]{validTitleQuery, validAuthorQuery, validPublisherQuery};
    }


    /*                                  xml onClick methods                                       */
    /*                                  ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯                                       */

    /**
     * displays a toast notifying the user about the Web API used
     *
     * @param floatingButton button on top the expanded toolbar
     */
    public void clickDisplayInfo(View floatingButton) {
        displayCenteredToast(getApplicationContext(), getResources().getString(R.string.toast_info_main));
    }

    /**
     * initiates the default search, same as the search view's submit listener
     *
     * @param discoverButton button below the default search view
     */
    public void clickDefaultSearchGo(View discoverButton) {
        initiateDefaultSearch();
    }

    /**
     * collapses/expands the advanced search tab, depending on it's current state
     *
     * @param advancedTab view getting the listener
     */
    public void clickAdvancedSearchToggle(View advancedTab) {
        final LinearLayout advancedView = findViewById(R.id.main_content_linlay_adv_expanded);
        final ImageView leftExpandIcon = findViewById(R.id.main_content_adv_left_icon);
        final ImageView rightExpandIcon = findViewById(R.id.main_content_adv_right_icon);

        // expand, change icon and set info text accordingly to the now visible advanced view
        if (advancedView.getVisibility() == View.GONE) {
            advancedView.setVisibility(View.VISIBLE);
            leftExpandIcon.setImageResource(R.drawable.ic_expand_less_accent_24dp);
            rightExpandIcon.setImageResource(R.drawable.ic_expand_less_accent_24dp);

            //check for connectivity before displaying the message to inform the user in case
            if (!NetworkUtils.isNetworkAvailable(MainActivity.this))
                infoTxtV.setText(R.string.note_device_offline);
            else
                infoTxtV.setText(getResources().getString(R.string.note_advanced_search, MIN_CHARS_FOR_SEARCH));
        }
        // collapse if it was expanded
        else {
            advancedView.setVisibility(View.GONE);
            leftExpandIcon.setImageResource(R.drawable.ic_expand_more_accent_24dp);
            rightExpandIcon.setImageResource(R.drawable.ic_expand_more_accent_24dp);

            //check for connectivity before displaying the message to inform the user in case
            if (!NetworkUtils.isNetworkAvailable(MainActivity.this))
                infoTxtV.setText(R.string.note_device_offline);
            else
                infoTxtV.setText(R.string.note_greeting);
        }
    }

    /**
     * starts the advanced search
     *
     * @param searchButton submit button on the bottom of the expanded advanced search tab
     */
    public void clickAdvancedSearchGo(View searchButton) {
        initiateAdvancedSearch();
    }
}
