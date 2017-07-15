package com.paulmandal.queensmaticledcontroller.data;

/**
 * State for an LED
 */
public class Led {

    /**
     * Index of this LED on the strip
     */
    public final int ledNumber;

    /**
     * Current LED brightness
     */
    public int brightness;

    /**
     * Current LED red level
     */
    public int red;

    /**
     * Current LED blue level
     */
    public int blue;

    /**
     * Current LED green level
     */
    public int green;

    public Led(int ledNumber, int brightness, int red, int green, int blue) {
        this.ledNumber = ledNumber;
        this.brightness = brightness;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Check if this LED is equal to the supplied values
     */
    public boolean equals(int brightness, int red, int green, int blue) {
        return this.brightness == brightness
                && this.red == red
                && this.green == green
                && this.blue == blue;
    }

}
