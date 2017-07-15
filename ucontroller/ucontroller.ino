/**
 * LED Controller:
 *  - Receives configuration updates over serial
 *  - Receives LED state updates over serial
 *  - Updates attached LED strip
 */

#include <SPI.h>
#include "led_controller.h"
#include "message_handler.h"

// LED Controller
LedController ledController;

// Hardware Controller - Power & Temperature
HardwareController hardwareController;

// Message Handler
MessageHandler messageHandler;

/**
 * Init code
 */
void setup() {
  ledController.begin(DEFAULT_LED_COUNT);
  hardwareController.begin();
  messageHandler.begin(&ledController, &hardwareController);
}

/**
 * Main loop
 */
void loop() {  
  messageHandler.checkMessages();
  hardwareController.updateTemp();
  hardwareController.checkTemp();
}

