package com.paulmandal.queensmaticledcontroller.workers;

import com.paulmandal.queensmaticledcontroller.api.LedApi;

/**
 * Worker that polls the API for system status updates
 */
public class SystemStatusWorker implements Runnable {

    /**
     * Frequency to request a system status update (ms)
     */
    private static final int DEFAULT_POLLING_INTERVAL = 1000;

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
    private LedApi.FetchSystemStatusListener mFetchSystemStatusListener;

    public SystemStatusWorker(LedApi ledApi, LedApi.FetchSystemStatusListener fetchSystemStatusListener) {
        mLedApi = ledApi;
        mFetchSystemStatusListener = fetchSystemStatusListener;
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
                mLedApi.fetchSystemStatus(mFetchSystemStatusListener);
                Thread.sleep(DEFAULT_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
