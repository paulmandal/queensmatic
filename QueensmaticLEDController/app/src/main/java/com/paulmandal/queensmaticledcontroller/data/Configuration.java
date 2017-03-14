package com.paulmandal.queensmaticledcontroller.data;

/**
 * Container for the LED controller system configuration
 */

public class Configuration {

    /**
     * Number of LEDs on the top strip
     */
    public final int topLedCount;

    /**
     * Number of LEDs on the top strip
     */
    public final int rightLedCount;

    /**
     * Number of LEDs on the top strip
     */
    public final int bottomLedCount;

    /**
     * Number of LEDs on the top strip
     */
    public final int leftLedCount;

    /**
     * Initial LED brightness for all LEDs
     */
    public final int startupBrightness;

    /**
     * Initial red brightness for all LEDs
     */
    public final int startupRed;

    /**
     * Initial green brightness for all LEDs
     */
    public final int startupGreen;

    /**
     * Initial blue brightness for all LEDs
     */
    public final int startupBlue;
    
    public Configuration(int topLedCount, int rightLedCount, int bottomLedCount, int leftLedCount,
                         int startupBrightness, int startupRed, int startupGreen, int startupBlue) {
        this.topLedCount = topLedCount;
        this.rightLedCount = rightLedCount;
        this.bottomLedCount =  bottomLedCount;
        this.leftLedCount = leftLedCount;
        this.startupBrightness = startupBrightness;
        this.startupRed = startupRed;
        this.startupGreen = startupGreen;
        this.startupBlue = startupBlue;
    }

}

