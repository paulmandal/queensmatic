package com.paulmandal.queensmaticledcontroller.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Application-level configuration
 */

public class AppConfiguration {

    /**
     * Shared preferences filename
     */
    private static final String SHARED_PREFS_NAME = "queensmatic";

    /**
     * Shared preference key holding the hostname setting
     */
    private static final String HOSTNAME_PREFS_KEY = "hostname";

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Hostname of the LED Controller API server
     */
    private String mHostname;

    public AppConfiguration(@NonNull Context context) {
        mContext = context;
    }

    /**
     * @return Gets the hostname for the LED Controller API
     */
    @Nullable
    public String getHostname() {
        if(mHostname == null) {
            SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            mHostname = prefs.getString(HOSTNAME_PREFS_KEY, null);
        }
        return mHostname;
    }

    /**
     * Stores the hostname for the LED Controller API
     *
     * @param hostname new hostname to connect to
     * @return true if the storage operation was successful
     */
    public boolean setHostname(@NonNull String hostname) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(HOSTNAME_PREFS_KEY, hostname);
        if(editor.commit()) {
            mHostname = hostname;
            return true;
        }
        return false;
    }

}
