#!flask/bin/python

#
# Sends LED updates to the microcontroller
#

from constants import *
from threading import Lock

serialPort = open(USB_SERIAL, 'w')
lock = Lock()

def updateLed(json):
    updateCommand = "U:%d,%d,%d,%d,%d" % (json['ledNumber'], json['red'], json['green'], json['blue'], json['brightness'])
    with lock:
        serialPort.write(updateCommand)
    return
