# To Do
- Update SwaggerHub definition with response object

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
- Order circuit board & SMD parts

# In Progress
- Some refactors

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
- Android App - LED Updating Screen - Skeleton
- Android App - LED Updating Screen - Color & brightness sliders
- Android App - LED Updating Screen - Color Preview & other UI
- Android App - LED Updating Screen - LED display
- Android App - LED Updating Screen - API integration
- Android App - LED Updating Screen - Touch & intersection detection
- Android App - LED Updating Screen - Better abstraction in LED drawing (LayoutParams stuff)
- Hardware - Design circuit in Eagle
- LED Controller API - Add /status endpoint with GET/PUT, wire up to R and P commands in uController, update SwaggerHub
- Hardware - prototype circuit on breadboards - need a SMD soldering iron tip for breakout
- Hardware - Lay out PCB in Eagle
- Android App - Hook up power button/switch

# Icebox

- LED Controller API - /leds endpoint - GET
