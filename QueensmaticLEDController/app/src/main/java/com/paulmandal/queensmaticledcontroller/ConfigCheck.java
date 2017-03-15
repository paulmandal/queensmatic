package com.paulmandal.queensmaticledcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;

/**
 * Initial check for the presence of hostname - if no hostname is found redirect to the settings
 * activity
 */

public class ConfigCheck  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_check);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i;
        AppConfiguration appConfiguration = new AppConfiguration(this);
        if(appConfiguration.getHostname() == null) {
            // Head to Config activity
            i = new Intent(this, ConfigActivity.class);
        } else {
            // Head to Drawing activity
            i = new Intent(this, DrawingActivity.class);
        }
        startActivity(i);
        finish();
    }

}