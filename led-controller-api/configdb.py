#!flask/bin/python

#
# Configuration Database
#

from constants import *
import sqlite3

db = sqlite3.connect(DATABASE_FILE, check_same_thread=False)

def dict_factory(cursor, row):
    d = {}
    for idx, col in enumerate(cursor.description):
        d[col[0]] = row[idx]
    return d

# Check if the configuration database exists, if it does not create it
def maybeInitDatabase():
    cursor = db.cursor()
    cursor.execute('SELECT name FROM sqlite_master WHERE type="table" AND name=?;', [CONFIG_TABLE_NAME])
    data = cursor.fetchone()
    if not data:
        cursor.executescript("""
                    CREATE TABLE %s(
                      topLedCount INT,
                      rightLedCount INT,
                      bottomLedCount INT,
                      leftLedCount INT,
                      startupBrightness INT,
                      startupRed INT,
                      startupGreen INT,
                      startupBlue INT,
                      softwareMaxColor INT,
                      softwareMaxBrightness INT,
                      hardwareMaxColor INT,
                      hardwareMaxBrightness INT);
                    INSERT INTO %s VALUES (
                        60,
                        30,
                        60,
                        30,
                        100,
                        255,
                        255,
                        255,
                        256,
                        100,
                        256,
                        32
                    );
                """ % (CONFIG_TABLE_NAME, CONFIG_TABLE_NAME))
        db.commit()
    return

def getConfig():
    db.row_factory = dict_factory
    cursor = db.cursor()
    cursor.execute("SELECT * FROM %s" % CONFIG_TABLE_NAME)
    data = cursor.fetchone()
    return data

def getConfigColumns():
    db.row_factory = None
    cursor = db.cursor()
    cursor.execute("SELECT * FROM %s" % CONFIG_TABLE_NAME)
    data = cursor.fetchone()
    columns = []
    for col in cursor.description:
        columns.append(col[0])
    return columns


def storeConfiguration(json):
    cursor = db.cursor()
    cursor.execute("""
    UPDATE %s SET
        topLedCount = ?,
        rightLedCount = ?,
        bottomLedCount = ?,
        leftLedCount = ?,
        startupBrightness = ?,
        startupRed = ?,
        startupGreen = ?,
        startupBlue = ?,
        softwareMaxColor = ?,
        softwareMaxBrightness = ?,
        hardwareMaxColor = ?,
        hardwareMaxBrightness = ?
    """ % CONFIG_TABLE_NAME, [
                                json['topLedCount'],
                                json['rightLedCount'],
                                json['bottomLedCount'],
                                json['leftLedCount'],
                                json['startupBrightness'],
                                json['startupRed'],
                                json['startupGreen'],
                                json['startupBlue'],
                                json['softwareMaxColor'],
                                json['softwareMaxBrightness'],
                                json['hardwareMaxColor'],
                                json['hardwareMaxBrightness']
                              ])
    db.commit()
    return




