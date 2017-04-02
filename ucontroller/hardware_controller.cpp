/**
 * Hardware Controller Code
 */
#include "Arduino.h"
#include "hardware_controller.h"

// Constants
const long DEVICE_CLOCK_SPEED = 16000000L;
const int DEFAULT_LED_COUNT = 180;
const int LED_UPDATE_VALUE_COUNT = 5;
const float ADC_STEPS_TO_CV = (5.0/*V*/ / 1024.0/*steps*/) * 100.0/*cV/V*/;
const int TEMPERATURE_PIN = 21;
const int MAX_MOSFET_TEMP = 40;
const int POWER_PIN = 10;

// Constructor, empty
HardwareController::HardwareController() {}

void HardwareController::begin() {
  powerOn = false;
  currentTemp = 0.0;
  _restorePowerState = false;
  pinMode(POWER_PIN, OUTPUT);
  pinMode(TEMPERATURE_PIN, INPUT);  
}

/**
 * Update Power state
 */
void HardwareController::updatePower(boolean powerState) {  
  powerOn = powerState;
  _updatePower();  
}

/**
 * Reads the temperature from the temperature pin
 */
 void HardwareController::updateTemp() {
  currentTemp = analogRead(TEMPERATURE_PIN) * ADC_STEPS_TO_CV - 273.15; // steps->cV, 1cV/K, Kelvin->Celsius
 }

/**
 * Checks whether the temperature is at an acceptable level
 */
void HardwareController::checkTemp() {
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
void HardwareController::_updatePower() {
  digitalWrite(POWER_PIN, powerOn);
}


