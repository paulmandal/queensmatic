/*
 * LED Strip Controller Code
 */
#include "Arduino.h"
#include "led_controller.h"
#include <SPI.h>

// Constants
const long DEVICE_CLOCK_SPEED = 16000000L;
const int DEFAULT_LED_COUNT = 174;
const int LED_UPDATE_VALUE_COUNT = 5;

// Constructor, empty
LedController::LedController() {}

void LedController::begin(int ledCount) {
  currentLedCount = ledCount;  
  _reallocateMemory();
  SPI.begin();
}

/**
 * Call when power state changes
 */
void LedController::powerOn() {
  delay(50);
  _updateLeds();
}

/**
 * Reallocate memory for LED state
 */
void LedController::_reallocateMemory() {
  free(leds);
  leds = (LED*)calloc(currentLedCount, sizeof(LED));  
}

/** 
 * Update the LED count
 */
void LedController::updateLedCount(int ledCount) {
  boolean reallocate = currentLedCount != ledCount;
  if(reallocate) {
    currentLedCount = ledCount;
    _reallocateMemory();
    _updateLeds();
  }
}

/**
 * Update an LED's state
 */
void LedController::updateLed(int ledNumber, int red, int green, int blue, int brightness) {  
  if(ledNumber >= currentLedCount) {
    // LED out of bounds
    return false;
  }
  // Normalize brightness value
  brightness = min(brightness, 31);
  LED *led = &leds[ledNumber];
  boolean needsUpdate = led->red != red
                          || led->green != green
                          || led->blue != blue
                          || led->brightness != brightness;
  if(needsUpdate) {
    led->red = red;
    led->green = green;
    led->blue = blue;
    led->brightness = brightness;
    _updateLeds();
  }
}

/**
 * Send commands to LED hardware to update lights
 */
void LedController::_updateLeds() {  
  // For APA102C
  int endLength = round(((float)(currentLedCount - 1) / 16.0) + 0.5F);
  endLength = max(endLength, 4);
  int i;
  byte headerByte;
  LED *led;
  SPI.beginTransaction(SPISettings(DEVICE_CLOCK_SPEED, MSBFIRST, SPI_MODE0));
  // Start by sending 32 zero bits
  for(i = 0 ; i < 4 ; i++) {
    SPI.transfer(0x00);
  }
  // Send each LED state, header is 1110 0000 & 5-bit brightness, then b/g/r color channels
  for(i = 0 ; i < currentLedCount ; i++) {
    led = &leds[i];
    headerByte = B11100000 | led->brightness;
    SPI.transfer(headerByte);
    SPI.transfer(led->blue);
    SPI.transfer(led->green);
    SPI.transfer(led->red);
  }
  for(i = 0 ; i < endLength ; i++) {
    SPI.transfer(0xFF);
  }
  SPI.endTransaction();  
}

