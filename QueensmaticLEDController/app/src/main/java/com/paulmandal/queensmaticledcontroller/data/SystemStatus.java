package com.paulmandal.queensmaticledcontroller.data;

/**
 * System Status for the LED controller
 */
public class SystemStatus {

    /**
     * Power state of the LED strips
     */
    public final boolean powerState;

    /**
     * Temperature of the power MOSFET
     */
    public final float mosfetTemperature;

    public SystemStatus(boolean powerState, float mosfetTemperature) {
        this.powerState = powerState;
        this.mosfetTemperature = mosfetTemperature;
    }

}
