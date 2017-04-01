package com.paulmandal.queensmaticledcontroller.workers;

import android.support.annotation.NonNull;

import com.paulmandal.queensmaticledcontroller.api.ApiConnection;
import com.paulmandal.queensmaticledcontroller.data.SystemStatus;

/**
 * Worker that polls the API for system status updates
 */
public class SystemStatusWorker implements Runnable, ApiConnection.FetchSystemStatusListener {

    /**
     * Frequency to request a system status update (ms)
     */
    private static final int DEFAULT_POLLING_INTERVAL = 1000;

    /**
     * Interface for listeners to receive system status updates through
     */
    public interface SystemStatusUpdateListener {
        void onSystemStatusUpdated(@NonNull SystemStatus systemStatus);
    }

    /**
     * Thread this worker is running on
     */
    private Thread mThread;


    /**
     * LED API
     */
    private ApiConnection mApiConnection;

    /**
     * Listener for this worker
     */
    private SystemStatusUpdateListener mSystemStatusUpdateListener;

    public SystemStatusWorker(ApiConnection apiConnection, SystemStatusUpdateListener systemStatusUpdateListener) {
        mApiConnection = apiConnection;
        mSystemStatusUpdateListener = systemStatusUpdateListener;
    }

    /**
     * Starts the worker
     */
    public void start() {
        if(mThread == null) {
            mThread = new Thread(this);
        }
        mThread.start();
    }

    public void stop() {
        mThread = null;
    }

    @Override
    public void run() {
        while(mThread != null) {
            try {
                mApiConnection.fetchSystemStatus(this);
                mThread.sleep(DEFAULT_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener implementations - simply forward the events
     */
    @Override
    public void onSystemStatusFetched(@NonNull SystemStatus systemStatus) {
        mSystemStatusUpdateListener.onSystemStatusUpdated(systemStatus);
    }

    @Override
    public void onSystemStatusFetchError() {
        // Drop errors
    }

}
