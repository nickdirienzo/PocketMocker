package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.buffalo.cse.blue.pocketmocker.MainActivity;

public class RecordingManager extends ModelManager {

	public RecordingManager(MainActivity a) {
		super(a);
	}

	public long addRecording(Recording r) {
		SQLiteDatabase sql = activity.getDatabase().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Recording.COL_CREATION_DATE, r.getCreationDateSqlString());
		long id = sql.insert(Recording.TABLE_NAME, null, values);
		Log.v("REC", "Insertedd recording: " + id);
		sql.close();
		return id;
	}

	public boolean hasLocations(long id) {
		ArrayList<MockLocation> mockLocations = activity.getMockLocationManager()
				.getMockLocationsForRecording(id);
		Log.v(MainActivity.TAG, "There are " + mockLocations.size() + " locations for Recording "
				+ id + ".");
		return mockLocations.size() > 0;
	}
}
