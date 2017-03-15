package com.paulmandal.queensmaticledcontroller.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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

    /**
     * Hostname for API requests
     */
    private String mHostname;

    public ApiConnection(RequestQueue requestQueue, String hostname) {
        mRequestQueue = requestQueue;
        mHostname = hostname;
        mRequestQueue.start();
    }

    public static ApiConnection apiConnectionFactory(Context context, String hostname) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024);
        Network network = new BasicNetwork(new HurlStack());
        return new ApiConnection(new RequestQueue(cache, network), hostname);
    }

    public void setHostname(String hostname) {
        mHostname = hostname;
    }

    /**
     * Fetches the configuration from the API
     *
     * @param listener Callback after async fetch
     */
    public void fetchConfiguration(final FetchConfigurationListener listener) {
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, mHostname + CONFIGURATION_ENDPOINT, null, new Response.Listener<JSONObject>() {
                    FetchConfigurationListener mListener = listener;
                    @Override
                    public void onResponse(JSONObject response) {
                        Configuration configuration = ApiTranslator.configurationFromJson(response);
                        if(configuration != null) {
                            mListener.onConfigurationFetched(configuration);
                        } else {
                            mListener.onConfigurationFetchError();
                        }
                    }
                }, new Response.ErrorListener() {
                    FetchConfigurationListener mListener = listener;
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mListener.onConfigurationFetchError();
                    }
                });
        addRequest(request);
    }

    /**
     * Stores a Configuration using the API
     *
     * @param listener  Callback for async store operation
     * @param configuration
     */
    public void sendConfiguration(final StoreConfigurationListener listener, Configuration configuration) {
        JSONObject json = ApiTranslator.jsonObjectFromConfiguration(configuration);
        if(json != null) {
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.PUT, mHostname + CONFIGURATION_ENDPOINT, json, new Response.Listener<JSONObject>() {
                        StoreConfigurationListener mListener = listener;
                        @Override
                        public void onResponse(JSONObject response) {
                            mListener.onConfigurationStored();
                        }
                    }, new Response.ErrorListener() {
                        StoreConfigurationListener mListener = listener;
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            mListener.onConfigurationStoreError();
                        }
                    });
            addRequest(request);
        } else {
            listener.onConfigurationStoreError();
        }
    }

    /**
     * Disables caching and sends the request to the queue
     *
     * @param request JSONObject Volley request
     */
    private void addRequest(Request request) {
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

}
