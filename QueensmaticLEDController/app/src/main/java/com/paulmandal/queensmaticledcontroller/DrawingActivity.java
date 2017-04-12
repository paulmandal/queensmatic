package com.paulmandal.queensmaticledcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.paulmandal.queensmaticledcontroller.api.LedApi;
import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;
import com.paulmandal.queensmaticledcontroller.data.Configuration;
import com.paulmandal.queensmaticledcontroller.data.Led;
import com.paulmandal.queensmaticledcontroller.data.SystemStatus;
import com.paulmandal.queensmaticledcontroller.workers.SystemStatusWorker;

import static java.lang.Math.max;

/**
 * Drawing activity - update LEDs via touch
 */
public class DrawingActivity extends AppCompatActivity {

    /**
     * How many strips our LED sets are divided into
     */
    private static final int LED_STRIPS = 4;

    /**
     * Indexes for LED layouts
     */
    private static final int LAYOUT_TOP = 0;
    private static final int LAYOUT_RIGHT = 1;
    private static final int LAYOUT_BOTTOM = 2;
    private static final int LAYOUT_LEFT = 3;

    /**
     * Indexes for LED color/brightness channels
     */
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final int BRIGHTNESS = 3;

    /**
     * Reference to the API connection
     */
    private LedApi mLedApi;

    /**
     * Current configuration from the API
     */
    private Configuration mConfiguration;

    /**
     * System Status update worker
     */
    private SystemStatusWorker mSystemStatusWorker;

    /**
     * The LEDs being controlled by this screen
     */
    private Led[] mLeds;

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
    private View[] mLedViews;

    /**
     * Rectangles for each LED view - for touch detection
     */
    private Rect[] mLedRects;

    /**
     * Rect for areas that don't contain LEDs
     */
    private Rect mDeadzone;

    /**
     * The View for the Color Preview
     */
    private View mColorPreview;

    /**
     * The View for the MOSFET temperature output
     */
    private TextView mMosfetTemperature;

    /**
     * The Power Switch
     */
    private Switch mPowerSwitch;

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
        mLedApi = LedApi.ledApiFactory(this, appConfiguration.getHostname());

        // LED Layouts
        mLedLayouts[LAYOUT_TOP] = (LinearLayout) findViewById(R.id.led_layout_top);
        mLedLayouts[LAYOUT_RIGHT] = (LinearLayout) findViewById(R.id.led_layout_right);
        mLedLayouts[LAYOUT_BOTTOM] = (LinearLayout) findViewById(R.id.led_layout_bottom);
        mLedLayouts[LAYOUT_LEFT] = (LinearLayout) findViewById(R.id.led_layout_left);

        // Color Preview
        mColorPreview = findViewById(R.id.color_preview);
        mColorPreview.setBackgroundColor(Color.argb(255, 255, 255, 255));
        mColorPreview.bringToFront();

        // MOSFET Temperature Output
        mMosfetTemperature = (TextView) findViewById(R.id.mosfet_temperature);

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
        mSeekbars[RED] = (SeekBar) findViewById(R.id.red_channel_seekbar);
        mSeekbars[GREEN] = (SeekBar) findViewById(R.id.blue_channel_seekbar);
        mSeekbars[BLUE] = (SeekBar) findViewById(R.id.green_channel_seekbar);
        mSeekbars[BRIGHTNESS] = (SeekBar) findViewById(R.id.brightness_seekbar);
        for (int i = 0; i < 4; i++) {
            mSeekbars[i].setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

        // Power Switch
        mPowerSwitch = (Switch) findViewById(R.id.power_switch);
        mPowerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLedApi.sendPowerUpdate(mPowerSwitch.isChecked());
            }
        });

        // Dead zone rectangle (makes touch handling faster)
        final View controlsLayout = findViewById(R.id.controls_layout);
        controlsLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                controlsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mDeadzone = new Rect(mLedLayouts[LAYOUT_LEFT].getRight(), mLedLayouts[LAYOUT_TOP].getBottom(), mLedLayouts[LAYOUT_RIGHT].getLeft(), mLedLayouts[LAYOUT_BOTTOM].getTop());
            }
        });

        // Touch handling
        findViewById(R.id.root_layout).setOnTouchListener(mOnTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mConfiguration == null) {
            // No configuration, fetch it from the API
            mLedApi.fetchConfiguration(mFetchConfigurationListener);
        }
        if (mSystemStatusWorker == null) {
            mSystemStatusWorker = new SystemStatusWorker(mLedApi, mFetchSystemStatusListener);
        }
        mSystemStatusWorker.start();
    }

    @Override
    public void onPause() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        if (mSystemStatusWorker != null) {
            mSystemStatusWorker.stop();
        }
        super.onPause();
    }

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Determine which seekbar was updated
            switch (seekBar.getId()) {
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
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    /**
     * Rescale brightness (5-bit) -> 8-bit alpha
     *
     * @param brightness 5-bit brightness
     * @return 8-bit alpha
     */
    private int scaleBrightness(int brightness) {
        return (int) (brightness / 31.0 * 255.0);
    }

    /**
     * Configuration fetching listener
     */
    private LedApi.FetchConfigurationListener mFetchConfigurationListener = new LedApi.FetchConfigurationListener() {
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
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mLedApi.fetchConfiguration(mFetchConfigurationListener);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    };

    /**
     * System Status update listener
     */
    private LedApi.FetchSystemStatusListener mFetchSystemStatusListener = new LedApi.FetchSystemStatusListener() {
        @Override
        public void onSystemStatusFetched(@NonNull SystemStatus systemStatus) {
            if (systemStatus.powerState != mPowerSwitch.isChecked()) {
                mPowerSwitch.setChecked(systemStatus.powerState);
            }
            mMosfetTemperature.setText(getString(R.string.degrees_celsius, systemStatus.mosfetTemperature));
        }

        @Override
        public void onSystemStatusFetchError() {
            // Do nothing
        }
    };

    /**
     * Update color SeekBars to startup colors
     */
    private void updateSeekbars() {
        mSeekbars[RED].setProgress(mConfiguration.startupRed);
        mSeekbars[GREEN].setProgress(mConfiguration.startupGreen);
        mSeekbars[BLUE].setProgress(mConfiguration.startupBlue);
        mSeekbars[BRIGHTNESS].setProgress(mConfiguration.startupBrightness);
    }

    /**
     * Update LED views and state variables
     */
    private void updateLedSetup() {
        Configuration configuration = mConfiguration;
        // Create new LED views with startup colors and brightness
        mLedCount = configuration.topLedCount + configuration.rightLedCount + configuration.bottomLedCount + configuration.leftLedCount;
        mLeds = new Led[mLedCount];
        mLedViews = new View[mLedCount];
        int alpha = scaleBrightness(configuration.startupBrightness);
        int color = Color.argb(alpha, configuration.startupRed, configuration.startupGreen, configuration.startupBlue);
        for (int i = 0; i < mLedCount; i++) {
            mLeds[i] = new Led(i, configuration.startupBrightness, configuration.startupRed, configuration.startupGreen, configuration.startupBlue);
            mLedViews[i] = new View(DrawingActivity.this);
            mLedViews[i].setBackgroundColor(color);
        }
        // Clear any existing LED Views
        for (int i = 0; i < LED_STRIPS; i++) {
            mLedLayouts[i].removeAllViews();
        }

        // Draw views for each strip - hardware dependent
        int stripLengths[] = {configuration.topLedCount, configuration.rightLedCount, configuration.bottomLedCount, configuration.leftLedCount};
        int j = 0;
        int end = 0;
        for (int i = 0; i < mLedLayouts.length; i++) {
            // Create LayoutParams for vertical vs. horizontal
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (i % 2 == 0) {
                width = mLedLayouts[i].getWidth() / max(stripLengths[i], 1);
            } else {
                height = mLedLayouts[i].getHeight() / max(stripLengths[i], 1);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            end = end + stripLengths[i];
            for (; j < end; j++) {
                mLedViews[j].setLayoutParams(params);
                mLedLayouts[i].addView(mLedViews[j]);
            }
        }

        // Build LED intersection Rects after the last view has drawn
        mLedRects = null;
        final View lastView = mLedViews[mLedCount - 1];
        lastView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lastView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                buildLedRects();
            }
        });
    }

    /**
     * Builds LED intersection Rects
     */
    private void buildLedRects() {
        mLedRects = new Rect[mLedCount];
        View v;
        int[] coords = new int[2];
        for (int i = 0; i < mLedCount; i++) {
            v = mLedViews[i];
            v.getLocationOnScreen(coords);
            int left = coords[0];
            int top = coords[1];
            int right = left + v.getWidth();
            int bottom = top + v.getHeight();
            mLedRects[i] = new Rect(left, top, right, bottom);
        }
    }

    /**
     * Handles touch detection - touches outside the LED strip or when the LED intersection Rects
     * have not yet been created are ignored, any touch inside an LED strip causes an attempt to
     * find the appropriate LED, set its color, and update the API
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mDeadzone.contains((int) event.getX(), (int) event.getY())
                    || mLedRects == null) {
                return true;
            }
            // Touch is inside one of the LED areas
            int ledIndex = -1;
            for (int i = 0; i < mLedCount; i++) {
                if (mLedRects[i].contains((int) event.getX(), (int) event.getY())) {
                    ledIndex = i;
                    break;
                }
            }
            if (ledIndex > -1) {
                mLedViews[ledIndex].setBackgroundColor(mArgbColor);
                Led target = mLeds[ledIndex];
                if(!target.equals(mColor[BRIGHTNESS], mColor[RED], mColor[GREEN], mColor[BLUE])) {
                    target.red = mColor[RED];
                    target.green = mColor[GREEN];
                    target.blue = mColor[BLUE];
                    target.brightness = mColor[BRIGHTNESS];
                    mLedApi.sendLedUpdate(target);
                }
            }
            return true;
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