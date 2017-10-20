package com.fablwesn.www.discovergooglebooks;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for misc. tasks
 */
class MiscUtils {

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
}
