package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RecordReplayManager extends ModelManager {

	private static RecordReplayManager sInstance;

	public static RecordReplayManager getInstance(Context c) {
		if (sInstance == null) {
			sInstance = new RecordReplayManager(c);
		}
		return sInstance;
	}

	protected RecordReplayManager(Context c) {
		super(c);
	}

	// We're just going to use an append-only table so we can safely ignore sync
	// issues
	private static int index = 0;
	public static final String TABLE_NAME = "rec_replay";
	public static final String COL_ID = "_id";
	public static final int COL_ID_INDEX = index++;
	public static final String COL_IS_REC = "is_rec";
	public static final int COL_IS_REC_INDEX = index++;
	public static final String COL_IS_REPLAY = "is_replay";
	public static final int COL_IS_REPLAY_INDEX = index++;
	public static final String COL_TIMESTAMP = "timestamp";
	public static final int COL_TIMESTAMP_INDEX = index++;
	public static final String CREATE_TABLE_CMD = Model.CREATE_TABLE + TABLE_NAME
			+ Model.OPEN_PAREN + COL_ID + Model.INT + Model.PK + Model.COMMA + COL_IS_REC
			+ Model.INT + Model.COMMA + COL_IS_REPLAY + Model.INT + Model.COMMA + COL_TIMESTAMP
			+ Model.INT + Model.CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = Model.dropTable(TABLE_NAME);

	public void setIsRecording(boolean isRecording) {
		ContentValues values = new ContentValues();
		values.put(COL_IS_REC, isRecording);
		// Just to be safe
		values.put(COL_IS_REPLAY, false);
		values.put(COL_TIMESTAMP, System.currentTimeMillis());
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.insert(TABLE_NAME, null, values);
		sql.close();
	}

	public void toggleRecording() {
		boolean isRecording = this.isRecording();
		Log.v("REC", "Is Recording: " + isRecording);
		ContentValues values = new ContentValues();
		values.put(COL_IS_REC, !isRecording);
		// Just to be safe
		values.put(COL_IS_REPLAY, false);
		values.put(COL_TIMESTAMP, System.currentTimeMillis());
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.insert(TABLE_NAME, null, values);
		sql.close();
	}

	public void setIsReplaying(boolean isReplaying) {
		ContentValues values = new ContentValues();
		values.put(COL_IS_REC, false);
		values.put(COL_IS_REPLAY, isReplaying);
		values.put(COL_TIMESTAMP, System.currentTimeMillis());
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.insert(TABLE_NAME, null, values);
		sql.close();
	}

	public void toggleReplaying() {
		boolean isReplaying = this.isReplaying();
		ContentValues values = new ContentValues();
		values.put(COL_IS_REC, false);
		values.put(COL_IS_REPLAY, !isReplaying);
		values.put(COL_TIMESTAMP, System.currentTimeMillis());
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.insert(TABLE_NAME, null, values);
		sql.close();
	}

	public boolean isRecording() {
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_TIMESTAMP
				+ " DESC LIMIT 1", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		boolean isRecording = this.getBoolean(cursor, COL_IS_REC_INDEX);
		sql.close();
		return isRecording;
	}

	public boolean isReplaying() {
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_TIMESTAMP
				+ " DESC LIMIT 1", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		boolean isReplaying = this.getBoolean(cursor, COL_IS_REPLAY_INDEX);
		sql.close();
		return isReplaying;
	}

}
