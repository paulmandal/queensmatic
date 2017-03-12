# To Do

## LED uController Software
- Read LED update commands from serial port (format: LED#,R,G,B - uint8_t)
- Update LED strip

## Android App
- App Skeleton - Main Screen
- Configuration fetching & parsing
- Configuration Screen (host, top/right/bottom/left LED counts)
- LED Updating Screen
  - Color wheel
  - Brightness slider
  - LED
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

# In Progress
- LED uController Software - Read config from serial port

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

# Icebox

- LED Controller API - /leds endpoint - GET
