#!flask/bin/python

#
# Sends LED updates to the microcontroller
#

from constants import *
from threading import Lock
import serial
import time
import re

serial_port = serial.Serial(USB_SERIAL, 115200)
lock = Lock()
status_pattern = re.compile("P:(?P<power_state>[0-9]+),T:(?P<mosfet_temperature>[.0-9]+)")


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


def get_status():
    send('R')
    time.sleep(0.01)
    status_line = serial_port.readline()
    matcher = status_pattern.match(status_line)
    return {
        'powerState': bool(matcher.group('power_state')),
        'mosfetTemperature': float(matcher.group('mosfet_temperature'))
    }


def update_power(power_state):
    command = 'P:%s' % ('1' if power_state else '0')
    send(command)
    return


def send(command):
    with lock:
        serial_port.write(command.encode())
        serial_port.write(b'\n')
    return
