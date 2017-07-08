#!py3.5/bin/python

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
def maybe_init_database():
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
                      startupBlue INT);
                    INSERT INTO %s VALUES (
                        58,
                        39,
                        53,
                        43,
                        31,
                        255,
                        255,
                        255
                    );
                """ % (CONFIG_TABLE_NAME, CONFIG_TABLE_NAME))
        db.commit()
    return


def get_config():
    db.row_factory = dict_factory
    cursor = db.cursor()
    cursor.execute("SELECT * FROM %s" % CONFIG_TABLE_NAME)
    data = cursor.fetchone()
    return data


def get_config_columns():
    db.row_factory = None
    cursor = db.cursor()
    cursor.execute("SELECT * FROM %s" % CONFIG_TABLE_NAME)
    data = cursor.fetchone()
    columns = []
    for col in cursor.description:
        columns.append(col[0])
    return columns


def store_config(json):
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
        startupBlue = ?
    """ % CONFIG_TABLE_NAME, [
                                json['topLedCount'],
                                json['rightLedCount'],
                                json['bottomLedCount'],
                                json['leftLedCount'],
                                json['startupBrightness'],
                                json['startupRed'],
                                json['startupGreen'],
                                json['startupBlue']
                              ])
    db.commit()
    return




