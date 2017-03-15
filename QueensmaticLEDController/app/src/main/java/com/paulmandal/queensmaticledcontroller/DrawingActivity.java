package com.paulmandal.queensmaticledcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paulmandal.queensmaticledcontroller.api.ApiConnection;
import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;
import com.paulmandal.queensmaticledcontroller.data.Led;

import java.util.Random;

/**
 * Drawing activity - update LEDs via touch
 */

public class DrawingActivity extends AppCompatActivity {

    private Button mTestButton;

    Random mRandom;

    Led[] mLeds;

    ApiConnection mApiConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        mRandom = new Random(System.currentTimeMillis());
        AppConfiguration appConfiguration = new AppConfiguration(this);
        mApiConnection = ApiConnection.apiConnectionFactory(this, appConfiguration.getHostname());

        // Test code
        mLeds = new Led[4];
        for(int i = 0 ; i < 4 ; i++) {
            mLeds[i] = new Led(i, 0, 0, 0, 0);
        }
        mTestButton = (Button)findViewById(R.id.button_test);
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a random LED update and transmit
                for(int i = 0 ; i < 4 ; i++) {
                    mLeds[i].brightness = mRandom.nextInt(32);
                    mLeds[i].red = mRandom.nextInt(256);
                    mLeds[i].green = mRandom.nextInt(256);
                    mLeds[i].blue = mRandom.nextInt(256);
                    mApiConnection.sendLedUpdate(mLeds[i]);
                }
            }
        });
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