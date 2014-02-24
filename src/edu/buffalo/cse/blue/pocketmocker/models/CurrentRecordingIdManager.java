package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CurrentRecordingIdManager extends ModelManager {

	private static CurrentRecordingIdManager sInstance;

	public static CurrentRecordingIdManager getInstance(Context c) {
		if (sInstance == null) {
			sInstance = new CurrentRecordingIdManager(c);
		}
		return sInstance;
	}

	protected CurrentRecordingIdManager(Context c) {
		super(c);
	}

	// We're just going to use an append-only table so we can safely ignore sync
	// issues
	private static int index = 0;
	public static final String TABLE_NAME = "current_rec_id";
	public static final String COL_REC_ID = "rec_id";
	public static final int COL_REC_ID_INDEX = index++;
	public static final String COL_TIMESTAMP = "timestamp";
	public static final int COL_TIMESTAMP_INDEX = index++;
	public static final String CREATE_TABLE_CMD = Model.CREATE_TABLE + TABLE_NAME
			+ Model.OPEN_PAREN + COL_REC_ID + Model.INT + Model.COMMA + COL_TIMESTAMP
			+ Model.INT + Model.CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = Model.dropTable(TABLE_NAME);

	public void setCurrentRecordingId(long id) {
		ContentValues values = new ContentValues();
		values.put(COL_REC_ID, id);
		values.put(COL_TIMESTAMP, System.currentTimeMillis());
		this.insert(values, TABLE_NAME);
	}

	public long getCurrentRecordingId() {
		SQLiteDatabase sql = manager.openDatabase();
		Cursor cursor = sql.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_TIMESTAMP
				+ " DESC LIMIT 1", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		long recId = this.getLong(cursor, COL_REC_ID_INDEX);
		manager.closeDatabase();
		return recId;
	}

}
