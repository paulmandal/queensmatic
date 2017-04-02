package com.paulmandal.queensmaticledcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paulmandal.queensmaticledcontroller.api.LedApi;
import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;
import com.paulmandal.queensmaticledcontroller.data.Configuration;

/**
 * Config activity - update API and app configuration
 */
public class ConfigActivity extends AppCompatActivity {

    /**
     * What to clear the hostname field to
     */
    private static final String EMPTY_HOSTNAME = "http://";

    /**
     * API Connection
     */
    LedApi mLedApi;

    /**
     * Current application configuration
     */
    AppConfiguration mAppConfiguration;

    /**
     * Current LED strip configuration
     */
    Configuration mConfiguration = null;

    /**
     * References to UI elements
     */
    TextView mHostname;
    TextView mTopLedCount;
    TextView mRightLedCount;
    TextView mBottomLedCount;
    TextView mLeftLedCount;
    TextView mStartupBrightness;
    TextView mStartupRed;
    TextView mStartupGreen;
    TextView mStartupBlue;
    Button mSaveButton;

    /**
     * Error alert dialog
     */
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Get UI element references
        mHostname = (TextView) findViewById(R.id.hostname);
        mTopLedCount = (TextView) findViewById(R.id.top_led_count);
        mRightLedCount = (TextView) findViewById(R.id.right_led_count);
        mBottomLedCount = (TextView) findViewById(R.id.bottom_led_count);
        mLeftLedCount = (TextView) findViewById(R.id.left_led_count);
        mStartupBrightness = (TextView) findViewById(R.id.startup_brightness);
        mStartupRed = (TextView) findViewById(R.id.startup_red);
        mStartupGreen = (TextView) findViewById(R.id.startup_green);
        mStartupBlue = (TextView) findViewById(R.id.startup_blue);
        mSaveButton = (Button) findViewById(R.id.button_save);

        mHostname.setOnFocusChangeListener(mHostnameFocusListener);
        mSaveButton.setOnClickListener(mSaveButtonListener);

        mAppConfiguration = new AppConfiguration(this);
        mLedApi = LedApi.ledApiFactory(this, mAppConfiguration.getHostname());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetch config if hostname is available
        String hostname = mAppConfiguration.getHostname();
        if (hostname != null) {
            mHostname.setText(hostname);
            mAppConfiguration.setHostname(hostname);
            mLedApi.fetchConfiguration(mFetchConfigurationListener);
        } else {
            mHostname.setText(EMPTY_HOSTNAME);
            mHostname.requestFocus();
        }
    }

    @Override
    public void onPause() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        super.onPause();
    }

    /**
     * Hostname focus listener - attempts to store hostname when it changes & refresh settings from
     * API
     */
    private View.OnFocusChangeListener mHostnameFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String hostname = mHostname.getText().toString();
                boolean hostnameChanged = !hostname.equals(mAppConfiguration.getHostname());
                if (!hostname.equals(EMPTY_HOSTNAME) && hostnameChanged) {
                    mAppConfiguration.setHostname(hostname);
                    mLedApi.setHostname(hostname);
                    mLedApi.fetchConfiguration(mFetchConfigurationListener);
                }
            }
        }
    };

    /**
     * Configuration fetching listener
     */
    private LedApi.FetchConfigurationListener mFetchConfigurationListener = new LedApi.FetchConfigurationListener() {
        @Override
        public void onConfigurationFetched(@NonNull Configuration configuration) {
            mConfiguration = configuration;
            updateEditTexts();
        }

        @Override
        public void onConfigurationFetchError() {
            hostnameError();
        }
    };

    /**
     * Configuration storing listener
     */
    private LedApi.StoreConfigurationListener mStoreConfigurationListener = new LedApi.StoreConfigurationListener() {
        @Override
        public void onConfigurationStored() {
            configSaved();
        }

        @Override
        public void onConfigurationStoreError() {
            mAlertDialog = new AlertDialog.Builder(ConfigActivity.this)
                    .setTitle(getString(R.string.error_saving_title))
                    .setMessage(getString(R.string.error_saving_message))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    /**
     * Save button listener - sends settings to the API
     */
    private View.OnClickListener mSaveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Package API settings and send them to the API
            int topLedCount = parseIntHelper(mTopLedCount.getText().toString());
            int rightLedCount = parseIntHelper(mRightLedCount.getText().toString());
            int bottomLedCount = parseIntHelper(mBottomLedCount.getText().toString());
            int leftLedCount = parseIntHelper(mLeftLedCount.getText().toString());
            int startupBrightness = parseIntHelper(mStartupBrightness.getText().toString());
            int startupRed = parseIntHelper(mStartupRed.getText().toString());
            int startupGreen = parseIntHelper(mStartupGreen.getText().toString());
            int startupBlue = parseIntHelper(mStartupBlue.getText().toString());

            Configuration configuration = new Configuration(topLedCount, rightLedCount,
                    bottomLedCount, leftLedCount, startupBrightness, startupRed,
                    startupGreen, startupBlue);

            if (!mConfiguration.equals(configuration)) {
                mLedApi.sendConfiguration(mStoreConfigurationListener, configuration);
            } else {
                configSaved();
            }
        }
    };

    /**
     * Helper for Integer.parseInt(String)
     */
    private int parseIntHelper(String s) {
        try {
            return Integer.parseInt(s);
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update the EditTexts on this screen with configuration from the API
     */
    private void updateEditTexts() {
        mTopLedCount.setText(String.valueOf(mConfiguration.topLedCount));
        mRightLedCount.setText(String.valueOf(mConfiguration.rightLedCount));
        mBottomLedCount.setText(String.valueOf(mConfiguration.bottomLedCount));
        mLeftLedCount.setText(String.valueOf(mConfiguration.leftLedCount));
        mStartupBrightness.setText(String.valueOf(mConfiguration.startupBrightness));
        mStartupRed.setText(String.valueOf(mConfiguration.startupRed));
        mStartupGreen.setText(String.valueOf(mConfiguration.startupGreen));
        mStartupBlue.setText(String.valueOf(mConfiguration.startupBlue));
    }

    /**
     * Display an error dialog and refocus hostname
     */
    private void hostnameError() {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_connecting_title, mHostname.getText()))
                .setMessage(getString(R.string.error_connecting_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mHostname.setText(EMPTY_HOSTNAME);
                        mHostname.requestFocus();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Configuration saved - go back to the ConfigCheck activity
     */
    private void configSaved() {
        Intent i = new Intent(ConfigActivity.this, ConfigCheck.class);
        startActivity(i);
        finish();
    }

}
