package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import edu.buffalo.cse.blue.pocketmocker.PocketMockerApplication;

public class MockLocationManager extends ModelManager {
	
	private static MockLocationManager sInstance;
	private PocketMockerApplication app;
	
	public static MockLocationManager getInstance(Context c) {
		if(sInstance == null) {
			sInstance = new MockLocationManager(c);
		}
		return sInstance;
	}
	
	private MockLocationManager(Context c) {
		super(c);
		app = (PocketMockerApplication) c;
	}

	public void addLocation(Location l) {
		MockLocation m = new MockLocation(l, app.getCurrentRecordingId());

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

	public ArrayList<MockLocation> getMockLocationsForRecording(long recId) {
		ArrayList<MockLocation> mockLocations = new ArrayList<MockLocation>();
		SQLiteDatabase sql = db.getReadableDatabase();
		String query = MockLocation.SELECT_ALL + " WHERE " + MockLocation.COL_RECORDING + " = '"
				+ recId + "'";
		Cursor cursor = sql.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				MockLocation ml = new MockLocation();
				ml.setId(getLong(cursor, MockLocation.COL_ID_INDEX));
				ml.setCreationDate(cursor.getString(MockLocation.COL_CREATION_DATE_INDEX));
				ml.setRecordingId(getLong(cursor, MockLocation.COL_RECORDING_INDEX));

				String provider = cursor.getString(MockLocation.COL_PROVIDER_INDEX);
				Location loc = new Location(provider);
				loc.setTime(getLong(cursor, MockLocation.COL_TIMESTAMP_INDEX));
				loc.setLongitude(getDouble(cursor, MockLocation.COL_LONGITUDE_INDEX));
				loc.setLatitude(getDouble(cursor, MockLocation.COL_LATITUDE_INDEX));
				if (isTrue(cursor, MockLocation.COL_HAS_ALTITUDE_INDEX)) {
					loc.setAltitude(getDouble(cursor, MockLocation.COL_ALTITUDE_INDEX));
				}
				if (isTrue(cursor, MockLocation.COL_HAS_SPEED_INDEX)) {
					loc.setSpeed(getFloat(cursor, MockLocation.COL_SPEED_INDEX));
				}
				if (isTrue(cursor, MockLocation.COL_HAS_BEARING_INDEX)) {
					loc.setBearing(getFloat(cursor, MockLocation.COL_BEARING_INDEX));
				}
				if (isTrue(cursor, MockLocation.COL_HAS_ACCURACY_INDEX)) {
					loc.setAccuracy(getFloat(cursor, MockLocation.COL_ACCURACY_INDEX));
				}
				// TODO: Create Bundle from a String and set it on our real
				// Location
				ml.setRealLocation(loc);
				mockLocations.add(ml);
			} while (cursor.moveToNext());
		}
		sql.close();
		return mockLocations;
	}
}
