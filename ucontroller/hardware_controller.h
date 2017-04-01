/**
 * Hardware Controller Header
 */
#ifndef Hardware_Controller_h
#define Hardware_Controller_h

#include "Arduino.h"

// uController clock speed
extern const long DEVICE_CLOCK_SPEED;

// 10-bit ADC, measuring 0-5V, the LM335AZ sensor outputs 10mV/K
extern const float ADC_STEPS_TO_CV;

// Temperature Pin #
extern const int TEMPERATURE_PIN;

// Maximum MOSFET temperature, above this threshold the system will automatically turn MOSFET-controlled power off
extern const int MAX_MOSFET_TEMP;

// Power PIN #
extern const int POWER_PIN;

class HardwareController
{
  public:
    HardwareController();
    // Whether power is currently on
    boolean powerOn;
    // Current temperature in C
    float currentTemp;
    void updatePower(boolean powerOn);
    void updateTemp();
    void checkTemp();
  private:
    // Whether to restore the power state after the MOSFET cools below MAX_MOSFET_TEMP
    boolean _restorePowerState;
    void _updatePower();  
};

#endif

