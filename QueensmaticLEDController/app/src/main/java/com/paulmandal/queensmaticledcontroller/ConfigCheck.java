package com.paulmandal.queensmaticledcontroller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.paulmandal.queensmaticledcontroller.data.AppConfiguration;

/**
 * Initial check for the presence of hostname - if no hostname is found redirect to the settings
 * activity
 */

public class ConfigCheck  extends AppCompatActivity {

    /**
     * Internet permission request code
     */
    private static final int INTERNET_PERMISSION_REQUEST = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_check);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseActivity();
                } else {
                    checkPermissions();
                }
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST);
        } else {
            chooseActivity();
        }
    }

    private void chooseActivity() {
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