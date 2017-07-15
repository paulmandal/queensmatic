# Queensmatic

This project is a wall-mounted NAS with controllable LED strips attached to its case.

# Requirements

- [Python](https://www.python.org/downloads/)
- [Docker CE](https://www.docker.com/community-edition#/download)
- [Android Studio + SDK](https://developer.android.com/studio/index.html)
- [Arduino](https://www.arduino.cc/en/Main/Software)
- [Eagle](https://www.autodesk.com/products/eagle/free-download)

# Table of Contents

| Directory | Contents |
|---|----|
| QueensmaticLEDController | Android LED controller application with simple color picker and touchscreen LED updating |
| hardware/circuit | Eagle schematics for the LED controller PCB |
| led-controller-uservice | Microservice used for communication between the Android app (or apps) and the LED controller hardware |
| ucontroller | Microcontroller code - receives messages from the API via serial and updates the LED strips |
| pictures | Some pictures of the building process |

# Running the API

```
cd led-controller-uservice
virtualenv venv
source venv/bin/activate
pip install docker-compose
# Update docker-compose.yml with the serial port for your microcontroller board -- under "devices"
docker-compose up
```
