package com.paulmandal.queensmaticledcontroller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.paulmandal.queensmaticledcontroller.api.ApiConnection;
import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;
import com.paulmandal.queensmaticledcontroller.data.Configuration;
import com.paulmandal.queensmaticledcontroller.data.Led;

/**
 * Drawing activity - update LEDs via touch
 */

public class DrawingActivity extends AppCompatActivity {

    /**
     * TODO: desc
     */
    private static final int LAYOUT_TOP = 0;
    private static final int LAYOUT_RIGHT = 0;
    private static final int LAYOUT_BOTTOM = 0;
    private static final int LAYOUT_LEFT = 0;

    /**
     * Reference to the API connection
     */
    ApiConnection mApiConnection;

    /**
     * Current configuration from the API
     */
    Configuration mConfiguration;

    /**
     * The LEDs being controlled by this screen
     */
    Led[] mLeds;

    /**
     * The Views representing LEDs
     */
    View[] mLedViews;

    /**
     * Layouts the LED views go into - top, right, bottom, left
     */
    private LinearLayout[] mLedLayouts;

    /**
     * Alert dialog if one is being displayed
     */
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        AppConfiguration appConfiguration = new AppConfiguration(this);
        mApiConnection = ApiConnection.apiConnectionFactory(this, appConfiguration.getHostname());
        mLedLayouts[LAYOUT_TOP] = (LinearLayout)findViewById(R.id.led_layout_top);
        mLedLayouts[LAYOUT_RIGHT] = (LinearLayout)findViewById(R.id.led_layout_right);
        mLedLayouts[LAYOUT_BOTTOM] = (LinearLayout)findViewById(R.id.led_layout_bottom);
        mLedLayouts[LAYOUT_LEFT] = (LinearLayout)findViewById(R.id.led_layout_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mConfiguration == null) {
            // No configuration, fetch it from the API
            mApiConnection.fetchConfiguration();
        }
    }

    @Override
    public void onPause() {
        if(mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        super.onPause();
        SeekBar s = new SeekBar(this);
        s.setOnDragListener();
    }

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Determine which seekbar was updated
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    private ApiConnection.FetchConfigurationListener mFetchConfigurationListener = new ApiConnection.FetchConfigurationListener() {
        @Override
        public void onConfigurationFetched(@NonNull Configuration configuration) {
            mConfiguration = configuration;
            // TODO: set up LED data structs
            drawLeds();
            // TODO: set view listeners
        }

        @Override
        public void onConfigurationFetchError() {
            mAlertDialog = new AlertDialog.Builder(DrawingActivity.this)
                    .setTitle(getString(R.string.error_sending_led_update))
                    .setMessage(getString(R.string.error_sending_led_update))
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}