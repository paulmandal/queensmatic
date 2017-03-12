
/**
 * LED Controller:
 *  - Receives configuration updates over serial
 *  - Receives LED state updates over serial
 *  - Updates attached LED strip
 */

// Default # of LEDs this controller is driving
const int DEFAULT_LED_COUNT = 180;

// Current # of LEDs this controller is driving
int currentLedCount = DEFAULT_LED_COUNT;

// An LED on our light strip
struct LED {
  int red        = 0;
  int green      = 0;
  int blue       = 0;
  int brightness = 0;
}

LED *leds;

void setup() {
  Serial.begin(115200);
  allocateMemory();  
  updateLeds();
}

void loop() {
  while(Serial.available > 0) {
    string command = Serial.readString();
    if(processCommand(command)) {
      updateLeds();
    }    
  }
}

void allocateMemory() {
  leds = (LED*)calloc(currentLedCount, sizeof(LED));
}

void freeMemory() {
  free(leds);
}

/**
 * Process a command and update the uController state
 */
boolean processCommand(string command) {
  
}

void updateLeds(int ledNumber, int red, int green, int blue, int brightness);

void updateConfig(int ledCount) {
  
}

