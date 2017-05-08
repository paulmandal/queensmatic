#!py3.5/bin/python

import serial
import sys
import random
import time

port_name = sys.argv[1]
led_count = int(sys.argv[2])

print('spamming {} with updates for {} LEDs'.format(port_name, led_count))

serial_port = serial.Serial(port_name, 115200)
serial_port.write('P:1'.encode())
serial_port.write(b'\n')
serial_port.write('C:{}'.format(led_count).encode())
serial_port.write(b'\n')
serial_port.close()

i = 0
while True:
    update_str = 'U:{},{},{},{},{}'.format(random.randint(0,led_count), random.randint(0,255), random.randint(0,255), random.randint(0,255), random.randint(0,31))
    print('{}: '.format(i, update_str))
    serial_port = serial.Serial(port_name, 115200)
    serial_port.write(update_str.encode())
    serial_port.write(b'\n')
    serial_port.close()
    i = i + 1

