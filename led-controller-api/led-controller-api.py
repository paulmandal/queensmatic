#!flask/bin/python
from flask import Flask
import json
import configdb

app = Flask(__name__)
configdb.maybeInitDatabase()

@app.route('/configuration', methods=['GET'])
def getConfiguration():
    data = configdb.getConfig()
    return json.dumps(data)

if __name__ == '__main__':
    app.run(debug=True)
