# To Do
- Update SwaggerHub definition with response object

## Android App
- LED Updating Screen
  - Touch & intersection detection

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
- LED Updating Screen - Skeleton

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
- General - First End-to-End Test
- LED Updating Screen - Color & brightness sliders
- LED Updating Screen - Color Preview & other UI
- LED Updating Screen - LED display
- LED Updating Screen - API integration

# Icebox

- LED Controller API - /leds endpoint - GET
