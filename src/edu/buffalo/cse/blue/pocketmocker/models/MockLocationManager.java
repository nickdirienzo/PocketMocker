package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class MockLocationManager {

	private Database db;

	public MockLocationManager(Database d) {
		db = d;
	}

	public void addLocation(Location l, long currentRecordingId) {
		MockLocation m = new MockLocation(l, currentRecordingId);
		
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		Location realLoc = m.getRealLocation();
		values.put(MockLocation.COL_CREATION_DATE, m.getCreationDateSqlString());
		values.put(MockLocation.COL_RECORDING, m.getRecordingId());
		values.put(MockLocation.COL_TIMESTAMP, realLoc.getTime());
		values.put(MockLocation.COL_LONGITUDE, realLoc.getLongitude());
		values.put(MockLocation.COL_LATITUDE, realLoc.getLatitude());
		values.put(MockLocation.COL_HAS_ALTITUDE, realLoc.hasAltitude());
		values.put(MockLocation.COL_ALTITUDE, realLoc.getAltitude());
		values.put(MockLocation.COL_HAS_SPEED, realLoc.hasSpeed());
		values.put(MockLocation.COL_SPEED, realLoc.getSpeed());
		values.put(MockLocation.COL_HAS_BEARING, realLoc.hasBearing());
		values.put(MockLocation.COL_BEARING, realLoc.getBearing());
		values.put(MockLocation.COL_HAS_ACCURACY, realLoc.hasAccuracy());
		values.put(MockLocation.COL_ACCURACY, realLoc.getAccuracy());
		// Not sure what to do with the bundle, so this will do for now.
		values.put(MockLocation.COL_EXTRAS, realLoc.getExtras().toString());
		values.put(MockLocation.COL_PROVIDER, realLoc.getProvider());
		sql.insert(MockLocation.TABLE_NAME, null, values);
		sql.close();
	}
}
