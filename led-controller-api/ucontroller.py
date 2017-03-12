#!flask/bin/python

#
# Sends LED updates to the microcontroller
#

from constants import *
from threading import Lock

serialPort = open(USB_SERIAL, 'w')
lock = Lock()

def setConfig(config, startup=False):
    ledCount = config['topLedCount'] + config['rightLedCount'] + config['bottomLedCount'] + config['leftLedCount']
    configCommand = "C:%d" % ledCount
    send(configCommand)
    if startup:
        updateLed(config['startupRed'], config['startupGreen'], config['startupBlue'], config['startupBrightness'])
    return

def updateLed(ledNumber, red, green, blue, brightness):
    updateCommand = "U:%d,%d,%d,%d,%d" % (ledNumber, red, green, blue, brightness)
    send(updateCommand)
    return

def send(command):
    with lock:
        serialPort.write(command)
    return