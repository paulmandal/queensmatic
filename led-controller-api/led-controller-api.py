#!flask/bin/python

#
# LED Controller API
#

from constants import *
from flask import Flask, request, abort
import json
import configdb
import ucontroller

app = Flask(__name__)
configdb.maybe_init_database()
ucontroller.set_config(configdb.get_config(), startup=True)


@app.route('/configuration', methods=['GET'])
def get_configuration():
    data = configdb.get_config()
    return json.dumps(data)


@app.route('/configuration', methods=['PUT'])
def store_configuration():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in configdb.get_config_columns()):
        abort(400)
    configdb.store_config(request.json)
    ucontroller.set_config(request.json)
    return "configuration updated", 201


@app.route('/leds', methods=['PUT'])
def update_led():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in LED_UPDATE_FIELDS):
        abort(400)
    ucontroller.update_led(json['ledNumber'], json['red'], json['green'], json['blue'], json['brightness'])
    return "LED updated", 201

if __name__ == '__main__':
    app.run(debug=True)