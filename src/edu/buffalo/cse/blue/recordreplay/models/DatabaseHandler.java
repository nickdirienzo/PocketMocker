package edu.buffalo.cse.blue.recordreplay.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final String TAG = "REC_DB";
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "RecordReplay.db";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Creating databases");
		db.execSQL(Models.Location.SQL_CREATE_ENTIRES);
		db.execSQL(Models.Objective.SQL_CREATE_TABLE);
		Log.v(TAG, "Created database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "Deleteing tables.");
		// For now, since we're not in production, we can just drop our data when we upgrade
		db.execSQL(Models.Location.SQL_DELETE_ENTRIES);
		db.execSQL(Models.Objective.SQL_DROP_TABLE);
		// And recreate everything
		onCreate(db);
	}
	
	private String transformTimestampToDate(long timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date(timestamp);
		return dateFormat.format(date);
	}
	
	private String serializeBundle(Bundle b) {
		// TODO: Design a serialization strategy so we can create Bundles from Strings
		return b.toString();
	}
	
	public void insertLocation(Location loc, String activePathId) {
		Log.v(TAG, "Inserting locatoin into db.");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Models.Location.LOC_PATH_ID, activePathId);
		values.put(Models.Location.LOC_PROVIDER, loc.getProvider());
		values.put(Models.Location.LOC_TIME, transformTimestampToDate(loc.getTime()));
		values.put(Models.Location.LOC_LAT, loc.getLatitude());
		values.put(Models.Location.LOC_LONG, loc.getLongitude());
		values.put(Models.Location.LOC_HAS_ALTITUDE, loc.hasAltitude());
		values.put(Models.Location.LOC_ALTITUDE, loc.getAltitude());
		values.put(Models.Location.LOC_HAS_SPEED, loc.hasSpeed());
		values.put(Models.Location.LOC_SPEED, loc.getSpeed());
		values.put(Models.Location.LOC_HAS_BEARING, loc.hasBearing());
		values.put(Models.Location.LOC_BEARING, loc.getBearing());
		values.put(Models.Location.LOC_HAS_ACCURACY, loc.hasAccuracy());
		values.put(Models.Location.LOC_ACCURACY, loc.getAccuracy());
		values.put(Models.Location.LOC_EXTRAS, serializeBundle(loc.getExtras()));
		db.insert(Models.Location.TABLE_NAME, null, values);
		db.close();
		Log.v(TAG, "Successfully inserted locatoin into db: " + values.toString());
	}
	
	public int getLocationCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + Models.Location.TABLE_NAME, null);
		int ret = cursor.getCount();
		cursor.close();
		return ret;
	}
	
	/**
	 * Insert a path.
	 * @param p the path to insert
	 * @return the rowid of the path
	 */
	public long insertPath(Objective p) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Models.Objective.NAME, p.getName());
		values.put(Models.Objective.CREATED_TIMESTAMP, transformTimestampToDate(p.getTimestamp()));
		long ret = db.insert(Models.Objective.TABLE_NAME, null, values);
		db.close();
		return ret;
	}
	
	public int getPathCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + Models.Objective.TABLE_NAME, null);
		int ret = cursor.getCount();
		cursor.close();
		return ret;
	}

}
