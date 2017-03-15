package com.paulmandal.queensmaticledcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paulmandal.queensmaticledcontroller.api.ApiConnection;
import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;
import com.paulmandal.queensmaticledcontroller.data.Configuration;

/**
 * Config activity - update API and app configuration
 */

public class ConfigActivity extends AppCompatActivity {

    /**
     * API Connection
     */
    ApiConnection mApiConnection;

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
        mHostname = (TextView)findViewById(R.id.hostname);
        mTopLedCount = (TextView)findViewById(R.id.top_led_count);
        mRightLedCount = (TextView)findViewById(R.id.right_led_count);
        mBottomLedCount = (TextView)findViewById(R.id.bottom_led_count);
        mLeftLedCount = (TextView)findViewById(R.id.left_led_count);
        mStartupBrightness = (TextView)findViewById(R.id.startup_brightness);
        mStartupRed = (TextView)findViewById(R.id.startup_red);
        mStartupGreen = (TextView)findViewById(R.id.startup_green);
        mStartupBlue = (TextView)findViewById(R.id.startup_blue);
        mSaveButton = (Button)findViewById(R.id.button_save);

        mHostname.setOnFocusChangeListener(mHostnameFocusListener);
        mSaveButton.setOnClickListener(mSaveButtonListener);

        mAppConfiguration = new AppConfiguration(this);
        mApiConnection = ApiConnection.apiConnectionFactory(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetch config if hostname is available
        String hostname = mAppConfiguration.getHostname();
        if(hostname != null) {
            mHostname.setText(hostname);
        } else {
            mHostname.requestFocus();
        }
    }

    @Override
    public void onPause() {
        if(mAlertDialog != null) {
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
            if(!hasFocus) {
                String hostname = mHostname.getText().toString();
                boolean hostnameChanged = !hostname.equals(mAppConfiguration.getHostname());
                if(!hostname.equals("http://") && hostnameChanged) {
                    mAppConfiguration.setHostname(hostname);
                    mApiConnection.fetchConfiguration(mFetchConfigurationListener, hostname);
                }
            }
        }
    };

    private ApiConnection.FetchConfigurationListener mFetchConfigurationListener = new ApiConnection.FetchConfigurationListener() {
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
     * Save button listener - sends settings to the API
     */
    private View.OnClickListener mSaveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Package API settings and send them to the API
            int topLedCount = Integer.parseInt(mTopLedCount.getText().toString());
            int rightLedCount = Integer.parseInt(mRightLedCount.getText().toString());
            int bottomLedCount = Integer.parseInt(mBottomLedCount.getText().toString());
            int leftLedCount = Integer.parseInt(mLeftLedCount.getText().toString());
            int startupBrightness = Integer.parseInt(mStartupBrightness.getText().toString());
            int startupRed = Integer.parseInt(mStartupRed.getText().toString());
            int startupGreen = Integer.parseInt(mStartupGreen.getText().toString());
            int startupBlue = Integer.parseInt(mStartupBlue.getText().toString());

            Configuration configuration = new Configuration(topLedCount, rightLedCount,
                    bottomLedCount, leftLedCount, startupBrightness, startupRed,
                    startupGreen, startupBlue);

            if(!mConfiguration.equals(configuration)) {
                Log.d("DEBUG", "config is not equal, sending");
                mApiConnection.sendConfiguration(configuration);
            } else {
                Log.d("DEBUG", "config is equal, opening other activity");
                Intent i = new Intent(ConfigActivity.this, ConfigCheck.class);
                startActivity(i);
                finish();
            }
        }
    };

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
                .setTitle(getString(R.string.error_dialog_title, mHostname.getText()))
                .setMessage(getString(R.string.error_dialog_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mHostname.setText("");
                        mHostname.requestFocus();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

}