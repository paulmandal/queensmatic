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
LedController ledController(DEFAULT_LED_COUNT);

// Message Handler
MessageHandler messageHandler;

/**
 * Init code
 */
void setup() {
  messageHandler.begin(ledController);
}

/**
 * Main loop
 */
void loop() {
  messageHandler.checkMessages();
  ledController.updateTemp();
  ledController.checkTemp();
  delay(100); // TODO: adjust delay time for smooth updates / add some delay logic?
}

