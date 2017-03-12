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
configdb.maybeInitDatabase()

@app.route('/configuration', methods=['GET'])
def getConfiguration():
    data = configdb.getConfig()
    return json.dumps(data)

@app.route('/configuration', methods=['PUT'])
def storeConfiguration():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in configdb.getConfigColumns()):
        abort(400)
    configdb.storeConfiguration(request.json)
    return "configuration updated", 201

@app.route('/leds', methods=['PUT'])
def updateLed():
    if not request.json:
        abort(400)
    if not any(x in request.json for x in LED_UPDATE_FIELDS):
        abort(400)
    ucontroller.updateLed(request.json)
    return "LED updated", 201

if __name__ == '__main__':
    app.run(debug=True)