#!/usr/bin/env python
# Using exported data from http://www.geoplaner.com
import argparse
import datetime
import os
import time
import xml.dom.minidom

PM_DB_PATH = '/data/data/edu.buffalo.cse.blue.pocketmocker/databases/PocketMocker.db'
LAT_INDEX = 0
LONG_INDEX = 1
ALT_INDEX = 1
OUTSIDE_DAVIS_WALK_ID = 1
SECONDS = 15

current_id = os.system(' '.join(['adb shell', '"echo \'select count(*) from locations;\' | sqlite3 ' + PM_DB_PATH + '"']))

class MockLocation(object):
    def __init__(self, _id, lat, lon, alt, time_shift):
        self._id = _id
        ts = time.time() + time_shift
        self.creation_date = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
        self.rec_id = OUTSIDE_DAVIS_WALK_ID
        self.timestamp = ts
        self.lon = lon
        self.lat = lon
        self.has_alt = 1
        self.alt = alt
        self.has_speed = 1
        self.speed = 6.80000019073486 # From real data
        self.has_bearing = 1
        self.bearing = 171.100006103516 # From real data
        self.has_acc = 1
        self.acc = 48.0 # From real data
        self.extras = 'Bundle[mParcelledData.dataSize=44]'
        self.provider = 'gps'

    def get_fields(self):
        return [self._id, '\\"%s\\"' % self.creation_date, self.rec_id, self.timestamp, self.lon, 
        self.lat, self.has_alt, self.alt, self.has_speed, self.speed, 
        self.has_bearing, self.bearing, self.has_acc, self.acc, '\\"%s\\"' % self.extras, '\\"%s\\"' % self.provider]


parser = argparse.ArgumentParser(description='Load GPS waypoints exported from http://www.geoplaner.com into the connected Android device\'s PocketMocker database.')
parser.add_argument('source', metavar='source', type=str, help='the path to the waypoints file.')
args = parser.parse_args()
with open(args.source, 'r') as waypoints:
    dom = xml.dom.minidom.parse(waypoints)
    waypoints = dom.getElementsByTagName('wpt')
    time_shift = 0
    for index, waypoint in enumerate(waypoints):
        current_id += 1
        time_shift += 1
        lat = waypoint.attributes.item(LAT_INDEX).value
        lon = waypoint.attributes.item(LONG_INDEX).value
        # This whole script is a hack anyway
        alt = waypoint.childNodes[ALT_INDEX].firstChild.data
        print '%s/%s Lat: %s Long: %s Altitude: %s' % (index + 1, len(waypoints), lat, lon, alt)
        mock = MockLocation(current_id, lat, lon, alt, time_shift * SECONDS)
        values = ', '.join([str(item) for item in mock.get_fields()])
        sql = 'insert into locations values (%s);' % values
        cmd = '"echo \'%s\' | sqlite3 %s"' % (sql, PM_DB_PATH)
        print ' '.join(['adb shell', cmd])
        os.system(' '.join(['adb shell', cmd]))
