package edu.buffalo.cse.blue.recordreplay.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

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
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "RecordReplay.db";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Creating database");
		db.execSQL(Models.Location.SQL_CREATE_ENTIRES);
		Log.v(TAG, "Created database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "Deleteing tables.");
		// For now, since we're not in production, we can just drop our data when we upgrade
		db.execSQL(Models.Location.SQL_DELETE_ENTRIES);
		// And recreate everything
		onCreate(db);
	}
	
	private String transformTimestampToDate(long timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date(timestamp);
		return dateFormat.format(date);
	}
	
	private void serializeBundle(Bundle b) {
		JSONObject serialized;
		try {
			serialized = new JSONObject(b.toString());
			Log.v(TAG, serialized.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertLocation(Location loc) {
		Log.v(TAG, "Inserting locatoin into db.");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
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
		Log.v(TAG, loc.getExtras().toString());
		serializeBundle(loc.getExtras());
		db.insert(Models.Location.TABLE_NAME, null, values);
		db.close();
		Log.v(TAG, "Successfully inserted locatoin into db.");
	}
	
	public int getLocationCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + Models.Location.TABLE_NAME, null);
		int ret = cursor.getCount();
		cursor.close();
		return ret;
	}

}
