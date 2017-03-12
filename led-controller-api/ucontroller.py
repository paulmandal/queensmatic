#!flask/bin/python

#
# Sends LED updates to the microcontroller
#

from constants import *
from threading import Lock

serialPort = open(USB_SERIAL, 'w')
lock = Lock()


def set_config(config, startup=False):
    led_count = config['topLedCount'] + config['rightLedCount'] + config['bottomLedCount'] + config['leftLedCount']
    config_command = "C:%d" % led_count
    send(config_command)
    if startup:
        for i in range(0, led_count):
            update_led(i, config['startupRed'], config['startupGreen'], config['startupBlue'],
                       config['startupBrightness'])
    return


def update_led(led_number, red, green, blue, brightness):
    update_command = "U:%d,%d,%d,%d,%d" % (led_number, red, green, blue, brightness)
    send(update_command)
    return


def send(command):
    with lock:
        serialPort.write(command)
    return
