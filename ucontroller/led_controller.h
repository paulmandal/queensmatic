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
    LedController();
    void begin(int ledCount);
    void powerOn();
    void updateLedCount(int ledCount);
    void updateLed(int ledNumber, int red, int green, int blue, int brightness);
  private:
    void _updateLeds();
    void _reallocateMemory();
};

#endif
