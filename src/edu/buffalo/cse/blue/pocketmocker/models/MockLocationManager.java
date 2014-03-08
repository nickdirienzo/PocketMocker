package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MockLocationManager extends ModelManager {

	private static MockLocationManager sInstance;
	
	private RecordingManager recordingManager;
	private ArrayList<MockLocation> mockLocations;

	public static MockLocationManager getInstance(Context c) {
		if (sInstance == null) {
			sInstance = new MockLocationManager(c);
		}
		return sInstance;
	}

	private MockLocationManager(Context c) {
		super(c);
		mockLocations = new ArrayList<MockLocation>();
		recordingManager = RecordingManager.getInstance(c);
	}

	public void addLocation(Location l, String eventType, int status) {
		MockLocation m = new MockLocation(l, recordingManager.getCurrentRecordingId());

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
		try {
		    // Holy hacks, Batman.
		    JSONObject json = new JSONObject();
		    for(String key: realLoc.getExtras().keySet()) {
		        Object obj = realLoc.getExtras().get(key);
		        json.put(key, obj);
		    }
            values.put(MockLocation.COL_EXTRAS, json.toString());
        } catch (JSONException e) {
            // If we can't serialize to JSON, might as well store it as a string anyway
            values.put(MockLocation.COL_EXTRAS, realLoc.getExtras().toString());
        }
		values.put(MockLocation.COL_PROVIDER, realLoc.getProvider());
		values.put(MockLocation.COL_EVENT_TYPE, eventType);
		// Set status to -1 for all other callbacks besides onStatusChanged
		values.put(MockLocation.COL_STATUS, status);
		this.insert(values, MockLocation.TABLE_NAME);
	}

	public ArrayList<MockLocation> getMockLocationsForRecording(long recId) {
		ArrayList<MockLocation> mockLocations = new ArrayList<MockLocation>();
		SQLiteDatabase sql = manager.openDatabase();
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
				if (getBoolean(cursor, MockLocation.COL_HAS_ALTITUDE_INDEX)) {
					loc.setAltitude(getDouble(cursor, MockLocation.COL_ALTITUDE_INDEX));
				}
				if (getBoolean(cursor, MockLocation.COL_HAS_SPEED_INDEX)) {
					loc.setSpeed(getFloat(cursor, MockLocation.COL_SPEED_INDEX));
				}
				if (getBoolean(cursor, MockLocation.COL_HAS_BEARING_INDEX)) {
					loc.setBearing(getFloat(cursor, MockLocation.COL_BEARING_INDEX));
				}
				if (getBoolean(cursor, MockLocation.COL_HAS_ACCURACY_INDEX)) {
					loc.setAccuracy(getFloat(cursor, MockLocation.COL_ACCURACY_INDEX));
				}
				try {
				    // Holy hacks, Batman.
				    Bundle extras = new Bundle();
                    JSONObject jsonBlob = new JSONObject(getString(cursor, MockLocation.COL_EXTRAS_INDEX));
                    @SuppressWarnings("unchecked")
                    Iterator<String> extrasIt = jsonBlob.keys();
                    String key;
                    while(extrasIt.hasNext()) {
                        key = extrasIt.next();
                        Object obj = jsonBlob.get(key);
                        if(obj instanceof Integer) {
                            extras.putInt(key, (Integer) obj);
                        } else if (obj instanceof Float) {
                            extras.putFloat(key, (Float) obj);
                        } else if (obj instanceof String) {
                            extras.putString(key, (String) obj);
                        }
                    }
                    loc.setExtras(extras);
                } catch (JSONException e) {
                    Log.v("PM_MLM", "Can't parse into JSON. Screw it.");
                }
				// real Location
				ml.setRealLocation(loc);
				ml.setEventType(getString(cursor, MockLocation.COL_EVENT_TYPE_INDEX));
				ml.setStatus(getInt(cursor, MockLocation.COL_STATUS_INDEX));
				mockLocations.add(ml);
			} while (cursor.moveToNext());
		}
		manager.closeDatabase();
		return mockLocations;
	}
	
	public void init() {
		if (mockLocations.size() == 0) {
			Log.v("REC", "getting locations for rec_id: " + recordingManager.getCurrentRecordingId());
			mockLocations = getMockLocationsForRecording(recordingManager.getCurrentRecordingId());
		}
	}
	
	public boolean isReady() {
		return mockLocations.size() != 0;
	}

	public MockLocation getNext() {
		if (hasNext()) {
			return mockLocations.remove(0);
		} else {
			return null;
		}
	}

	public boolean hasNext() {
		return mockLocations.size() != 0;
	}
	
	// When you have hacks like this, you should go to sleep.
	public void kill() {
	    mockLocations.clear();
	}

}
