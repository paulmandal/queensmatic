#!flask/bin/python

#
# LED Controller API
#

from constants import *
from flask import Flask, request, abort, jsonify
import json
import configdb
import ucontroller

app = Flask(__name__)
configdb.maybe_init_database()
ucontroller.set_config(configdb.get_config(), startup=True)


@app.route('/configuration', methods=['GET'])
def get_configuration():
    data = configdb.get_config()
    return jsonify(data)


@app.route('/configuration', methods=['PUT'])
def store_configuration():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in configdb.get_config_columns()):
        abort(400)
    configdb.store_config(request.json)
    ucontroller.set_config(request.json)
    return jsonify(message="configuration updated"), 201


@app.route('/leds', methods=['PUT'])
def update_led():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in LED_UPDATE_FIELDS):
        abort(400)
    json = request.json
    ucontroller.update_led(json['ledNumber'], json['red'], json['green'], json['blue'], json['brightness'])
    return jsonify(message="LED updated"), 201


@app.route('/status', methods=['GET'])
def get_status():
    status = ucontroller.get_status()
    return jsonify(status)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=31337)