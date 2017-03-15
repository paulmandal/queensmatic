package com.paulmandal.queensmaticledcontroller.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paulmandal.queensmaticledcontroller.data.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles communication with the API
 */

public class ApiConnection {

    /**
     * API Endpoints
     */
    private static final String CONFIGURATION_ENDPOINT = "/configuration";
    private static final String LED_UPDATE_ENDPOINT = "/leds";

    /**
     * Callback for fetching a Configuration
     */
    public interface FetchConfigurationListener {
        void onConfigurationFetched(@NonNull Configuration configuration);
        void onConfigurationFetchError();
    }

    /**
     * Callback for storing a Configuration
     */
    public interface StoreConfigurationListener {
        void onConfigurationStored();
        void onConfigurationStoreError();
    }

    /**
     * Volley Request Queue
     */
    private final RequestQueue mRequestQueue;

    public ApiConnection(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        mRequestQueue.start();
    }

    public static ApiConnection apiConnectionFactory(Context context) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024);
        Network network = new BasicNetwork(new HurlStack());
        return new ApiConnection(new RequestQueue(cache, network));
    }

    /**
     * Fetches the configuration from the API
     *
     * @param callback Callback after async fetch
     * @param hostname Hostname to fetch data from
     */
    public void fetchConfiguration(final FetchConfigurationListener callback, String hostname) {
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, hostname + CONFIGURATION_ENDPOINT, null, new Response.Listener<JSONObject>() {
                    FetchConfigurationListener mCallback = callback;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int topLedCount = response.getInt("topLedCount");
                            int rightLedCount = response.getInt("rightLedCount");
                            int bottomLedCount = response.getInt("bottomLedCount");
                            int leftLedCount = response.getInt("leftLedCount");
                            int startupBrightness = response.getInt("startupBrightness");
                            int startupRed = response.getInt("startupRed");
                            int startupGreen = response.getInt("startupGreen");
                            int startupBlue = response.getInt("startupBlue");

                            Configuration configuration = new Configuration(topLedCount,
                                    rightLedCount, bottomLedCount, leftLedCount, startupBrightness,
                                    startupRed, startupGreen, startupBlue);

                            mCallback.onConfigurationFetched(configuration);
                        } catch(JSONException e) {
                            e.printStackTrace();
                            mCallback.onConfigurationFetchError();
                        }
                    }
                }, new Response.ErrorListener() {
                    FetchConfigurationListener mCallback = callback;
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mCallback.onConfigurationFetchError();
                    }
                });
        addRequest(request);
    }

    public void sendConfiguration(Configuration configuration) {
        // TODO: method
    }

    /**
     * Disables caching and sends the request to the queue
     *
     * @param request JSONObject Volley request
     */
    private void addRequest(Request<JSONObject> request) {
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

}
