package com.paulmandal.queensmaticledcontroller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
     * How many strips our LED sets are divided into
     */
    private static final int LED_STRIPS = 4;

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
     * Current LED count
     */
    private int mLedCount;

    /**
     * Current color
     */
    private int[] mColor = new int[4];

    /**
     * Current color in a/r/g/b
     */
    private int mArgbColor;

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
    private LinearLayout[] mLedLayouts = new LinearLayout[LED_STRIPS];

    /**
     * SeekBars for red, green, blue, brightness
     */
    private SeekBar[] mSeekbars = new SeekBar[4];

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
        mSeekbars[RED] = (SeekBar)findViewById(R.id.red_channel_seekbar);
        mSeekbars[GREEN] = (SeekBar)findViewById(R.id.blue_channel_seekbar);
        mSeekbars[BLUE] = (SeekBar)findViewById(R.id.green_channel_seekbar);
        mSeekbars[BRIGHTNESS] = (SeekBar)findViewById(R.id.brightness_seekbar);
        for(int i = 0 ; i < 4 ; i++) {
            mSeekbars[i].setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

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
            mArgbColor = Color.argb(scaleBrightness(mColor[BRIGHTNESS]), mColor[RED], mColor[GREEN], mColor[BLUE]);
            mColorPreview.setBackgroundColor(mArgbColor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    /**
     * Rescale brightness (5-bit) -> 8-bit alpha
     * @param brightness 5-bit brightness
     * @return 8-bit alpha
     */
    private int scaleBrightness(int brightness) {
        return (int)(brightness / 31.0 * 255.0);
    }

    private ApiConnection.FetchConfigurationListener mFetchConfigurationListener = new ApiConnection.FetchConfigurationListener() {
        @Override
        public void onConfigurationFetched(@NonNull Configuration configuration) {
            mConfiguration = configuration;
            updateSeekbars();
            updateLedSetup();
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

    private void updateSeekbars() {
        mSeekbars[RED].setProgress(mConfiguration.startupRed);
        mSeekbars[GREEN].setProgress(mConfiguration.startupGreen);
        mSeekbars[BLUE].setProgress(mConfiguration.startupBlue);
        mSeekbars[BRIGHTNESS].setProgress(mConfiguration.startupBrightness);
    }

    private void updateLedSetup() {
        Configuration configuration = mConfiguration;
        mLedCount = configuration.topLedCount + configuration.rightLedCount + configuration.bottomLedCount + configuration.leftLedCount;
        mLeds = new Led[mLedCount];
        mLedViews = new View[mLedCount];
        int alpha = scaleBrightness(configuration.startupBrightness);
        int color = Color.argb(alpha, configuration.startupRed, configuration.startupGreen, configuration.startupBlue);
        for(int i = 0 ; i < mLedCount ; i++) {
            mLeds[i] = new Led(i, configuration.startupBrightness, configuration.startupRed, configuration.startupGreen, configuration.startupBlue);
            mLedViews[i] = new View(DrawingActivity.this);
            mLedViews[i].setBackgroundColor(color);
            mLedViews[i].setOnTouchListener(mLedTouchListener);
        }
        // Clear any existing LED Views
        for(int i = 0 ; i < LED_STRIPS ; i++) {
            mLedLayouts[i].removeAllViews();
        }

        // Draw views for each strip - hardware dependent
        // TODO: abstract this
        int width = mLedLayouts[LAYOUT_TOP].getWidth() / Math.max(configuration.topLedCount, 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        for(int i = 0 ; i < configuration.topLedCount ; i++) {
            mLedViews[i].setLayoutParams(params);
            mLedLayouts[LAYOUT_TOP].addView(mLedViews[i]);
        }
        int height = mLedLayouts[LAYOUT_RIGHT].getHeight() / Math.max(configuration.rightLedCount, 1);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        for(int i = configuration.topLedCount ; i < configuration.rightLedCount ; i++) {
            mLedViews[i].setLayoutParams(params);
            mLedLayouts[LAYOUT_RIGHT].addView(mLedViews[i]);
        }
        width = mLedLayouts[LAYOUT_BOTTOM].getWidth() / Math.max(configuration.bottomLedCount, 1);
        params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        for(int i = configuration.rightLedCount ; i < configuration.bottomLedCount ; i++) {
            mLedViews[i].setLayoutParams(params);
            mLedLayouts[LAYOUT_BOTTOM].addView(mLedViews[i]);
        }
        height = mLedLayouts[LAYOUT_LEFT].getHeight() / Math.max(configuration.leftLedCount, 1);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        for(int i = configuration.bottomLedCount ; i < configuration.leftLedCount ; i++) {
            mLedViews[i].setLayoutParams(params);
            mLedLayouts[LAYOUT_LEFT].addView(mLedViews[i]);
        }
    }


    private View.OnTouchListener mLedTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundColor(mArgbColor);
                    int i = indexOfLedByView(v);
                    mLeds[i].red = mColor[RED];
                    mLeds[i].green = mColor[GREEN];
                    mLeds[i].blue = mColor[BLUE];
                    mLeds[i].brightness = mColor[BRIGHTNESS];
                    mApiConnection.sendLedUpdate(mLeds[i]);
            }
            return true;
        }
    };

    int indexOfLedByView(View v) {
        int i;
        for(i = 0 ; i < mLedCount ; i++) {
            if(mLedViews[i] == v) {
                return i;
            }
        }
        return -1;
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