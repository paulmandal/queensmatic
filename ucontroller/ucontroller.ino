/**
 * LED Controller:
 *  - Receives configuration updates over serial
 *  - Receives LED state updates over serial
 *  - Updates attached LED strip
 */

#include <SPI.h>

// Default # of LEDs this controller is driving
const int DEFAULT_LED_COUNT = 180;

// LED update value count (index, red, green, blue, brightness)
const int LED_UPDATE_VALUE_COUNT = 5;

// Message buffer size
const int MSG_BUFFER_SIZE = 64;

// Current # of LEDs this controller is driving
int currentLedCount = DEFAULT_LED_COUNT;

// uController clock speed
long DEVICE_CLOCK_SPEED = 16000000L;

// An LED on our light strip
struct LED {
  byte red        = 0;
  byte green      = 0;
  byte blue       = 0;
  byte brightness = 0;
};

// Current LED state
LED *leds;

// Message buffer
char *msgBuf;
int bufferPos;

// Last read byte from serial
char readByte;

// Power PIN #
const int POWER_PIN = 8;

// Whether power is currently on
boolean powerOn = false;

// Current temperature in C
int currentTemp;

/**
 * Init code
 */
void setup() {
  Serial.begin(115200);
  SPI.begin();
  pinMode(POWER_PIN, OUTPUT);
  allocateMemory();
  updateLeds();
  updatePower();
}

/**
 * Main loop
 */
void loop() {
  while(Serial.available() > 0) {
    readByte = Serial.read();
    msgBuf[bufferPos] = readByte;
    bufferPos++;
    if(bufferPos >= MSG_BUFFER_SIZE) {
      // Prevent buffer overrun
      bufferPos = 0;
    }
    if(readByte == '\n') {
      // End of message, process the message
      if(processCommand(msgBuf, bufferPos)) {
        updateLeds();
      }
      // Reset buffer position
      bufferPos = 0;
    }
  }
}

/**
 * Allocate memory for LED state and message buffer
 */
void allocateMemory() {
  leds = (LED*)calloc(currentLedCount, sizeof(LED));
  msgBuf = (char*)calloc(MSG_BUFFER_SIZE, sizeof(char));
}

/**
 * Free up allocated memory
 */
void freeMemory() {
  free(leds);
  free(msgBuf);
}

/**
 * Process a command and update the uController state, commands defined in docs/ucontroller-commands.md
 */
boolean processCommand(char *command, int commandLength) {
  if(command[0] == 'C') {
    // Configuration update
    char *seperator = strchr(command, ':');
    // Extract LED count from string
    if(seperator != NULL) {
      seperator++;      
      int ledCount = atoi(seperator);
      if(ledCount > 0) {
        updateConfig(ledCount);
      }
    }
  } else if(command[0] == 'U') {
    // LED update
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
        return updateLed(values[0], values[1], values[2], values[3], values[4]);
      }
    }
  } else if(command[0] == 'P') {
    // Power update
    char *seperator = strchr(command, ':');
    if(seperator != NULL) {
      seperator++;      
      int powerState = atoi(seperator);
      powerOn = powerState == 1 ? true : false;
      updatePower();
    }
  } else if(command[0] == 'R') {
    sendStatusUpdate();
  } else if(command[0] == 'S') {
    // Output status - currently for debugging but could be adapted for bidirectional communication
    outputLedState(currentLedCount);
  }
  return false;
}

/**
 * Update an LED's state
 */
boolean updateLed(int ledNumber, int red, int green, int blue, int brightness) {  
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
 * Update Power PIN state
 */
 void updatePower() {
  digitalWrite(POWER_PIN, powerOn);
 }

/**
 * Send status update to the Serial port
 */
 void sendStatusUpdate() {
  Serial.print("P:");
  Serial.print(powerOn);
  Serial.print(",T:");
  Serial.println(currentTemp);
 }

/**
 * Output the current LED state to the serial port
 * Limit with count, or pass currentLedCount for all LEDs
 */
void outputLedState(int count) {
  Serial.println();
  Serial.println();
  Serial.println("LED State");
  Serial.println("#,r,g,b,br");
  LED *led;
  for(int i = 0 ; i < currentLedCount && i < count ; i++) {
    led = &leds[i];
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

/**
 * Update the configuration state
 */
void updateConfig(int ledCount) {
  boolean reallocate = currentLedCount != ledCount;
  currentLedCount = ledCount;
  if(reallocate) {
    freeMemory();
    allocateMemory();
  }
  updateLeds();
}

/**
 * Send commands to LED hardware to update lights
 */
void updateLeds() {
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

