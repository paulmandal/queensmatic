/*
 * LED Strip Controller Code
 */
#include "Arduino.h"
#include "led_controller.h"
#include <SPI.h>

// Constants
const long DEVICE_CLOCK_SPEED = 16000000L;
const int DEFAULT_LED_COUNT = 180;
const int LED_UPDATE_VALUE_COUNT = 5;
const float ADC_STEPS_TO_CV = (5.0/*V*/ / 1024.0/*steps*/) * 100.0/*cV/V*/;
const int TEMPERATURE_PIN = 21;
const int MAX_MOSFET_TEMP = 40;
const int POWER_PIN = 10;

LedController::LedController(int ledCount) {
  currentLedCount = ledCount;  
  powerOn = false;
  _reallocateMemory();
  SPI.begin();
  pinMode(POWER_PIN, OUTPUT);
  pinMode(TEMPERATURE_PIN, INPUT);
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
    currentTemp = 0.0;
    _restorePowerState = false;
    _reallocateMemory();
    _updateLeds();
    _updatePower();
  }
}

/**
 * Update an LED's state
 */
boolean LedController::updateLed(int ledNumber, int red, int green, int blue, int brightness) {  
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
  }
  return needsUpdate;
}

/**
 * Update Power state
 */
void LedController::updatePower(boolean powerState) {  
  powerOn = powerState;
  _updatePower();
}

/**
 * Reads the temperature from the temperature pin
 */
 void LedController::updateTemp() {
  currentTemp = analogRead(TEMPERATURE_PIN) * ADC_STEPS_TO_CV - 273.15; // steps->cV, 1cV/K, Kelvin->Celsius
 }

/**
 * Checks whether the temperature is at an acceptable level
 */
void LedController::checkTemp() {
  if(currentTemp > MAX_MOSFET_TEMP) {
    digitalWrite(POWER_PIN, LOW);
    _restorePowerState = true;
  } else if(_restorePowerState) {
    _restorePowerState = false;
    digitalWrite(POWER_PIN, powerOn);
  }
}

/**
 * Update Power Pin
 */
void LedController::_updatePower() {
  digitalWrite(POWER_PIN, powerOn);
  if(powerOn) {
    delay(50);
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
  SPI.beginTransaction(SPISettings(16000000, MSBFIRST, SPI_MODE0));
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

