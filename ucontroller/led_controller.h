/**
 * LED Controller Header
 */
#ifndef Led_Controller_h
#define Led_Controller_h

#include "Arduino.h"

// Default # of LEDs this controller is driving
extern const int DEFAULT_LED_COUNT;

// LED update value count (index, red, green, blue, brightness)
extern const int LED_UPDATE_VALUE_COUNT;

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

// An LED on our light strip
struct LED {
  byte red        = 0;
  byte green      = 0;
  byte blue       = 0;
  byte brightness = 0;
};

class LedController
{
  public:
    // Current # of LEDs being controlled
    int currentLedCount;
    // LED state
    LED *leds;
    // Whether power is currently on
    boolean powerOn;
    // Current temperature in C
    float currentTemp;
    LedController(int ledCount);
    void updateLedCount(int ledCount);
    boolean updateLed(int ledNumber, int red, int green, int blue, int brightness);
    void updatePower(boolean powerOn);
    void updateTemp();
    void checkTemp();
  private:
    // Whether to restore the power state after the MOSFET cools below MAX_MOSFET_TEMP
    boolean _restorePowerState;
    void _updateLeds();
    void _updatePower();  
    void _reallocateMemory();
};

#endif
