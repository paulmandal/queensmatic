#!flask/bin/python
import sqlite3

DATABASE_FILE = "api-db.sqlite"
TABLE_NAME = "led_configuration"

db = sqlite3.connect(DATABASE_FILE, check_same_thread=False)

def dict_factory(cursor, row):
    d = {}
    for idx, col in enumerate(cursor.description):
        d[col[0]] = row[idx]
    return d

# Check if the configuration database exists, if it does not create it
def maybeInitDatabase():
    cursor = db.cursor()
    cursor.execute('SELECT name FROM sqlite_master WHERE type="table" AND name=?;', [TABLE_NAME])
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
        """ % (TABLE_NAME, TABLE_NAME))
    return

def getConfig():
    cursor = db.cursor()
    db.row_factory = dict_factory
    cursor.execute("SELECT * FROM %s" % TABLE_NAME)
    data = cursor.fetchone()
    return data