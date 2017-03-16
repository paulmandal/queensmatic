package com.paulmandal.queensmaticledcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static final int LAYOUT_RIGHT = 1;
    private static final int LAYOUT_BOTTOM = 2;
    private static final int LAYOUT_LEFT = 3;

    /**
     * TODO: desc
     */
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final int BRIGHTNESS = 3;

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
     * Current color
     */
    private int[] mColor = new int[4];

    /**
     * The Views representing LEDs
     */
    View[] mLedViews;

    /**
     * The View for the Color Preview
     */
    private View mColorPreview;

    /**
     * Layouts the LED views go into - top, right, bottom, left
     */
    private LinearLayout[] mLedLayouts = new LinearLayout[4];

    /**
     * Alert dialog if one is being displayed
     */
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // Get configuration
        AppConfiguration appConfiguration = new AppConfiguration(this);
        mApiConnection = ApiConnection.apiConnectionFactory(this, appConfiguration.getHostname());

        // LED Layouts
        mLedLayouts[LAYOUT_TOP] = (LinearLayout)findViewById(R.id.led_layout_top);
        mLedLayouts[LAYOUT_RIGHT] = (LinearLayout)findViewById(R.id.led_layout_right);
        mLedLayouts[LAYOUT_BOTTOM] = (LinearLayout)findViewById(R.id.led_layout_bottom);
        mLedLayouts[LAYOUT_LEFT] = (LinearLayout)findViewById(R.id.led_layout_left);

        // Color Preview
        mColorPreview = findViewById(R.id.color_preview);
        mColorPreview.setBackgroundColor(Color.argb(255,255,255,255));
        mColorPreview.bringToFront();

        // Setup Button
        findViewById(R.id.button_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DrawingActivity.this, ConfigActivity.class);
                startActivity(i);
                finish();
            }
        });

        // SeekBars
        ((SeekBar)findViewById(R.id.red_channel_seekbar)).setOnSeekBarChangeListener(mSeekBarChangeListener);
        ((SeekBar)findViewById(R.id.blue_channel_seekbar)).setOnSeekBarChangeListener(mSeekBarChangeListener);
        ((SeekBar)findViewById(R.id.green_channel_seekbar)).setOnSeekBarChangeListener(mSeekBarChangeListener);
        ((SeekBar)findViewById(R.id.brightness_seekbar)).setOnSeekBarChangeListener(mSeekBarChangeListener);

        // Power Switch
        findViewById(R.id.power_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: power switch
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mConfiguration == null) {
            // No configuration, fetch it from the API
            mApiConnection.fetchConfiguration(mFetchConfigurationListener);
        }
    }

    @Override
    public void onPause() {
        if(mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        super.onPause();
    }

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Determine which seekbar was updated
            switch(seekBar.getId()) {
                case R.id.red_channel_seekbar:
                    mColor[RED] = progress;
                    break;
                case R.id.green_channel_seekbar:
                    mColor[GREEN] = progress;
                    break;
                case R.id.blue_channel_seekbar:
                    mColor[BLUE] = progress;
                    break;
                case R.id.brightness_seekbar:
                    mColor[BRIGHTNESS] = progress;
                    break;
            }
            // Update the Color Preview
            int alpha = (int)(mColor[BRIGHTNESS] / 31.0 * 255.0); // Rescale brightness (5-bit) -> 8-bit alpha
            mColorPreview.setBackgroundColor(Color.argb(alpha, mColor[RED], mColor[GREEN], mColor[BLUE]));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private ApiConnection.FetchConfigurationListener mFetchConfigurationListener = new ApiConnection.FetchConfigurationListener() {
        @Override
        public void onConfigurationFetched(@NonNull Configuration configuration) {
            mConfiguration = configuration;
            // TODO: set up LED data structs
            Log.d("DEBUG", "LEDS:" + configuration.topLedCount);
//            drawLeds();
            // TODO: set view listeners
        }

        @Override
        public void onConfigurationFetchError() {
            mAlertDialog = new AlertDialog.Builder(DrawingActivity.this)
                    .setTitle(getString(R.string.error_fetching_configuration))
                    .setMessage(getString(R.string.error_fetching_configuration))
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };


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