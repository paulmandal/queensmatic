/**
 * LED Controller:
 *  - Receives configuration updates over serial
 *  - Receives LED state updates over serial
 *  - Updates attached LED strip
 */

// Default # of LEDs this controller is driving
const int DEFAULT_LED_COUNT = 180;

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

void setup() {
  Serial.begin(115200);
  Serial.println("This is a butts test");
  allocateMemory();  
  updateLeds();
}

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

void allocateMemory() {
  leds = (LED*)calloc(currentLedCount, sizeof(LED));
  msgBuf = (char*)calloc(MSG_BUFFER_SIZE, sizeof(char));
}

void freeMemory() {
  free(leds);
  free(msgBuf);
}

/**
 * Process a command and update the uController state
 */
boolean processCommand(char command[], int commandLength) {
  if(command[0] == 'C') {
    // Configuration update
    char *seperator = strchr(command, ':');
    if(seperator != NULL) {
      seperator++;      
      int ledCount = atoi(seperator);
      if(ledCount > 0) {
        updateConfig(ledCount);
      }
    }
  } else if(command[0] == 'U') {
    // LED update
    // TODO: LED update
    return true;
  }
  return false;
}

void updateLed(int ledNumber, int red, int green, int blue, int brightness) {
  // TODO: update LED
}

void updateConfig(int ledCount) {
  boolean reallocate = currentLedCount != ledCount;
  currentLedCount = ledCount;
  if(reallocate) {
    freeMemory();
    allocateMemory();
  }
}

void updateLeds() {
  // TODO: update LED strip
}

