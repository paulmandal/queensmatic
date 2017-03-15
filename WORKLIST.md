# To Do

## Android App
- LED Updating Screen
  - Color sliders
  - Brightness slider
  - LED display
  - Touch & intersection detection
  - API integration

## Hardware
- OS Setup
- Design Case
  - Airflow
  - Air filtration
  - Computer component placement
  - LED mounting
  - Wall mounting
  - Light-bleed blocking?
- Source screws, dampening grommets, air filters
- Design circuit in Eagle
- Prototype circuit on breadboards
- Solder final circuit

# In Progress
- General - First End-to-End Test

# Blocked
- Hardware - Build NAS - possible faulty RAM

# Finished
- Hardware - Select & order hardware
- LED Controller API - Define API in SwaggerHub
- LED Controller API - /configuration endpoint, GET
- LED Controller API - Database / datastore for config
- LED Controller API - /configuration endpoint, PUT
- LED Controller API - /leds endpoint - PUT
- LED Controller API - Init uController with config
- LED uController Software - - Basic skeleton
- LED uController Software - Read config from serial port
- LED uController Software - Read LED update commands from serial port (format: LED#,R,G,B - uint8_t)
- LED uController Software - Update LED Strip
- Android App - App Skeleton - Main Screen
- Android App - Configuration fetching & parsing
- Android App - Configuration Screen (host, top/right/bottom/left LED counts)

# Icebox

- LED Controller API - /leds endpoint - GET
