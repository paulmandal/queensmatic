package com.paulmandal.queensmaticledcontroller.api;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paulmandal.queensmaticledcontroller.data.Configuration;
import com.paulmandal.queensmaticledcontroller.data.Led;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts API objects, JSON<->Native
 */

public class ApiTranslator {

    /**
     * Creates a Configuration from a JSON object
     * @param json JSONObject containing Configuration data
     * @return Configuration object representation of supplied JSON, or null if there was a parsing error
     */
    @Nullable
    public static Configuration configurationFromJson(JSONObject json) {
        try {
            int topLedCount = json.getInt("topLedCount");
            int rightLedCount = json.getInt("rightLedCount");
            int bottomLedCount = json.getInt("bottomLedCount");
            int leftLedCount = json.getInt("leftLedCount");
            int startupBrightness = json.getInt("startupBrightness");
            int startupRed = json.getInt("startupRed");
            int startupGreen = json.getInt("startupGreen");
            int startupBlue = json.getInt("startupBlue");

            Configuration configuration = new Configuration(topLedCount,
                    rightLedCount, bottomLedCount, leftLedCount, startupBrightness,
                    startupRed, startupGreen, startupBlue);
            
            return configuration;
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a JSONObject representation of the supplied Configuration
     * @param configuration the Configuration to translate to JSON
     * @return JSONObject representation of the supplied Configuration, or null if there was an error
     */
    @Nullable
    public static JSONObject jsonObjectFromConfiguration(Configuration configuration) {
        try {
            JSONObject json = new JSONObject();

            // Native->JSON
            json.put("topLedCount", configuration.topLedCount);
            json.put("rightLedCount", configuration.rightLedCount);
            json.put("bottomLedCount", configuration.bottomLedCount);
            json.put("leftLedCount", configuration.leftLedCount);
            json.put("startupBrightness", configuration.startupBrightness);
            json.put("startupRed", configuration.startupRed);
            json.put("startupGreen", configuration.startupGreen);
            json.put("startupBlue", configuration.startupBlue);

            return json;
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a JSONObject representation of an LED state
     * @param led The LED to translate to JSON
     * @return JSONObject representation of the supplied LED, or null if there was an error
     */
    @Nullable
    public static JSONObject jsonObjectFromLed(Led led) {
        try {
            JSONObject json = new JSONObject();

            // Native->JSON
            json.put("ledNumber", led.ledNumber);
            json.put("red", led.red);
            json.put("green", led.green);
            json.put("blue", led.blue);
            json.put("brightness", led.brightness);

            return json;
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
