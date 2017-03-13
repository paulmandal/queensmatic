/**
 * LED Controller:
 *  - Receives configuration updates over serial
 *  - Receives LED state updates over serial
 *  - Updates attached LED strip
 */

// Default # of LEDs this controller is driving
const int DEFAULT_LED_COUNT = 180;

// LED update value count (index, red, green, blue, brightness)
const int LED_UPDATE_VALUE_COUNT = 5;

// Message buffer size
const int MSG_BUFFER_SIZE = 64;

// Current # of LEDs this controller is driving
int currentLedCount = DEFAULT_LED_COUNT;

// An LED on our light strip
struct LED {
  int red        = 0;
  int green      = 0;
  int blue       = 0;
  int brightness = 0;
};

// Current LED state
LED *leds;

// Message buffer
char *msgBuf;
int bufferPos;

// Last read byte from serial
char readByte;

/**
 * Init code
 */
void setup() {
  Serial.begin(115200);
  allocateMemory();  
  updateLeds();
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
 * Process a command and update the uController state
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
 * Output the current LED state to the serial port
 * Limit with count, or pass currentLedCount for all LEDs
 */
void outputLedState(int count) {
  Serial.println();
  Serial.println();
  Serial.println("LED State");
  Serial.println("#,r,g,b,br");
  for(int i = 0 ; i < currentLedCount && i < count ; i++) {
    Serial.print("LED: ");
    Serial.print(i);
    Serial.print(", ");
    Serial.print(leds[i].red);
    Serial.print(", ");
    Serial.print(leds[i].green);
    Serial.print(", ");
    Serial.print(leds[i].blue);
    Serial.print(", ");
    Serial.print(leds[i].brightness);
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
}

/**
 * Send commands to LED hardware to update lights
 */
void updateLeds() {
  // TODO: update LED strip
}

