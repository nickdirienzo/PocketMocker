package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.buffalo.cse.blue.pocketmocker.MainActivity;
import edu.buffalo.cse.blue.pocketmocker.PocketMockerApplication;

public class RecordingManager extends ModelManager {

	private static RecordingManager sInstance;
	private CurrentRecordingManager curRecordingManager;
	private PocketMockerApplication app;

	public static RecordingManager getInstance(Context c) {
		if (sInstance == null) {
			Log.v("REC", "Creating new recording manager");
			sInstance = new RecordingManager(c);
		}
		return sInstance;
	}

	private RecordingManager(Context c) {
		super(c);
		app = (PocketMockerApplication) c;
		curRecordingManager = CurrentRecordingManager.getInstance(c);
	}
	
	public void setCurrentRecordingId(long recId) {
		curRecordingManager.setCurrentRecordingId(recId);
		Log.v("REC", "set cur rec id: " + recId);
	}
	
	public long getCurrentRecordingId() {
		long recId = curRecordingManager.getCurrentRecordingId();
		Log.v("REC", "get cur rec id: " + recId);
		return recId;
	}

	public long addRecording(Recording r) {
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Recording.COL_CREATION_DATE, r.getCreationDateSqlString());
		long id = sql.insert(Recording.TABLE_NAME, null, values);
		Log.v("REC", "Insertedd recording: " + id);
		sql.close();
		return id;
	}

	public boolean hasLocations(long id) {
		ArrayList<MockLocation> mockLocations = MockLocationManager.getInstance(app)
				.getMockLocationsForRecording(id);
		Log.v(MainActivity.TAG, "There are " + mockLocations.size() + " locations for Recording "
				+ id + ".");
		return mockLocations.size() > 0;
	}
}
