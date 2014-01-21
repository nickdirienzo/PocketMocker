package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RecordingManager {
	
	private Database db;
	
	public RecordingManager(Database d) {
		db = d;
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

}
