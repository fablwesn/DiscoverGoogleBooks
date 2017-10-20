package com.fablwesn.www.discovergooglebooks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class for everything relating to network
 */
class NetworkUtils {

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
}
