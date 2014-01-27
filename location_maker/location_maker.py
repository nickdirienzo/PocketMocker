#!/usr/bin/env python
# Using exported data from http://www.geoplaner.com
import argparse
import datetime
import time
import xml.dom.minidom

PM_DB_PATH = '/data/data/edu.buffalo.cse.blue.pocketmocker/databases/PocketMocker.db'
LAT_INDEX = 0
LONG_INDEX = 1

class MockLocation(object):
    def __init__(self, lat, lon, alt):
        self.lat = lon
        self.lon = lon
        self.alt = alt
        self.timestamp = time.time()
        self.inserted_time = datetime.datetime.fromtimestamp(self.timestamp).strftime('%Y-%m-%d %H:%M:%S')



parser = argparse.ArgumentParser(description='Load GPS waypoints exported from http://www.geoplaner.com into the connected Android device\'s PocketMocker database.')
parser.add_argument('source', metavar='source', type=str, help='the path to the waypoints file.')
args = parser.parse_args()

with open(args.source, 'r') as waypoints:
    dom = xml.dom.minidom.parse(waypoints)
    waypoints = dom.getElementsByTagName('wpt')
    for waypoint in waypoints:
        print waypoint.attributes.item(0).value
