package com.paulmandal.queensmaticledcontroller.workers;

import android.support.annotation.NonNull;

import com.paulmandal.queensmaticledcontroller.api.LedApi;
import com.paulmandal.queensmaticledcontroller.data.SystemStatus;

/**
 * Worker that polls the API for system status updates
 */
public class SystemStatusWorker implements Runnable, LedApi.FetchSystemStatusListener {

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
    private LedApi mLedApi;

    /**
     * Listener for this worker
     */
    private SystemStatusUpdateListener mSystemStatusUpdateListener;

    public SystemStatusWorker(LedApi ledApi, SystemStatusUpdateListener systemStatusUpdateListener) {
        mLedApi = ledApi;
        mSystemStatusUpdateListener = systemStatusUpdateListener;
    }

    /**
     * Starts the worker
     */
    public void start() {
        if (mThread == null) {
            mThread = new Thread(this);
        }
        mThread.start();
    }

    /**
     * Stops the worker
     */
    public void stop() {
        mThread = null;
    }

    /**
     * Worker loop, fetches the system status then sleeps
     */
    @Override
    public void run() {
        while (mThread != null) {
            try {
                mLedApi.fetchSystemStatus(this);
                Thread.sleep(DEFAULT_POLLING_INTERVAL);
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
