/*
 * Message Handler Code
 */
#include "Arduino.h"
#include "message_handler.h"

// Constants
const int MSG_BUFFER_SIZE = 64;

// Constructor, empty
MessageHandler::MessageHandler() {}

void MessageHandler::begin(LedController *ledController, HardwareController *hardwareController) {
  _bufferPos = 0;
  _ledController = ledController;
  _hardwareController = hardwareController;
  _reallocateMemory(); 
  Serial.begin(115200);
}

void MessageHandler::checkMessages() {
  while(Serial.available() > 0) {
    _readByte = Serial.read();
    _msgBuf[_bufferPos] = _readByte;
    _bufferPos++;
    if(_bufferPos >= MSG_BUFFER_SIZE) {
      // Prevent buffer overrun
      _bufferPos = 0;
    }
    if(_readByte == '\n') {
      // End of message, process the message
      _processCommand(_msgBuf, _bufferPos);
      // Reset buffer position
      _bufferPos = 0;
    }
  }  
}

void MessageHandler::_reallocateMemory() {
  free(_msgBuf);
  _msgBuf = (char*)calloc(MSG_BUFFER_SIZE, sizeof(char));
}

/**
 * Process a command and update the uController state, commands defined in docs/ucontroller-commands.md
 */
boolean MessageHandler::_processCommand(char *command, int commandLength) {
  if(command[0] == 'C') {
    _processConfigurationUpdate(command);
  } else if(command[0] == 'U') {
    _processLedUpdate(command);
  } else if(command[0] == 'P') {
    _processPowerUpdate(command);
  } else if(command[0] == 'R') {
    _sendStatusUpdate();
  } else if(command[0] == 'S') {
    // Output status - currently for debugging but could be adapted for bidirectional communication
    _outputLedState(_ledController->currentLedCount);
  }
  return false;
}

/**
 * Process configuration update command
 */
void MessageHandler::_processConfigurationUpdate(char *command) {
  int ledCount = _extractInt(command);
  if(ledCount > 0) {
    _ledController->updateLedCount(ledCount);
    _hardwareController->updatePower(_hardwareController->powerOn);
  }
}

/**
 * Process LED update command
 */
void MessageHandler::_processLedUpdate(char *command) {
  char *valuesStr = strchr(command, ':');
  // Extract individual values from string
  if(valuesStr != NULL) {
    int values[LED_UPDATE_VALUE_COUNT];
    int readValues = 0;
    valuesStr++;
    char *valueStr = strtok(valuesStr, ",");
    while(valueStr != NULL && readValues < LED_UPDATE_VALUE_COUNT) {
      int value = atoi(valueStr);
      values[readValues] = value;
      readValues++;
      valueStr = strtok(NULL, ",");
    }
    if(readValues == LED_UPDATE_VALUE_COUNT) {
      // Update LED if enough values were read
      _ledController->updateLed(values[0], values[1], values[2], values[3], values[4]);
    }
  }
}

/**
 * Procses power update command
 */
void MessageHandler::_processPowerUpdate(char *command) {
  int powerState = _extractInt(command);
  boolean powerOn = powerState == 1 ? true : false;
  _hardwareController->updatePower(powerOn);
  if(powerOn) {
     _ledController->powerOn();
  }  
}

/**
 * Extract integer from string with format *:n, returns -1 if nothing found
 */
int MessageHandler::_extractInt(char *command) {
  char *seperator = strchr(command, ':');
  if(seperator != NULL) {
    seperator++;
    return atoi(seperator);
  }
  return -1;  
}

/**
 * Send status update to the Serial port
 */
 void MessageHandler::_sendStatusUpdate() {
  Serial.print("P:");
  Serial.print(_hardwareController->powerOn);
  Serial.print(",T:");
  Serial.println(_hardwareController->currentTemp);
 }

/**
 * Output the current LED state to the serial port
 * Limit with count, or pass currentLedCount for all LEDs
 */
void MessageHandler::_outputLedState(int count) {
  Serial.println();
  Serial.println();
  Serial.println("LED State");
  Serial.println("#,r,g,b,br");
  LED *led;
  for(int i = 0 ; i < _ledController->currentLedCount && i < count ; i++) {
    led = &_ledController->leds[i];
    Serial.print("LED: ");
    Serial.print(i);
    Serial.print(", ");
    Serial.print(led->red);
    Serial.print(", ");
    Serial.print(led->green);
    Serial.print(", ");
    Serial.print(led->blue);
    Serial.print(", ");
    Serial.print(led->brightness);
    Serial.println();
  }
  Serial.println();
  Serial.println();
}



