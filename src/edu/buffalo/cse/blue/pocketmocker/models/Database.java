package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Thread-safe database manager. Thanks:
 * https://github.com/dmytrodanylyk/dmytrodanylyk
 * /blob/gh-pages/articles/Concurrent%20Database%20Access.md
 * 
 * @author nvd
 * 
 */
public class Database extends SQLiteOpenHelper {

	private static final int DB_VERSION = 4;
	private static final String DB_NAME = "PocketMocker.db";

	private static Database sInstance;
	private SQLiteDatabase database;
	private AtomicInteger dbRefCount;

	public static synchronized Database getInstance(Context c) {
		if (sInstance == null) {
			sInstance = new Database(c.getApplicationContext());
		}
		return sInstance;
	}

	public synchronized SQLiteDatabase openDatabase() {
		if(dbRefCount.incrementAndGet() == 1) {
			database = sInstance.getWritableDatabase();
		}
		return database;
	}
	
	public synchronized void closeDatabase() {
		if(dbRefCount.decrementAndGet() == 0) {
			database.close();
		}
	}

	private Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		dbRefCount = new AtomicInteger();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Objective.CREATE_TABLE_CMD);
		db.execSQL(Recording.CREATE_TABLE_CMD);
		db.execSQL(MockLocation.CREATE_TABLE_CMD);
		db.execSQL(CurrentRecordingIdManager.CREATE_TABLE_CMD);
		db.execSQL(RecordReplayManager.CREATE_TABLE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Our current policy is wipe and start new.
		db.execSQL(Objective.DROP_TABLE_CMD);
		db.execSQL(Recording.DROP_TABLE_CMD);
		db.execSQL(MockLocation.DROP_TABLE_CMD);
		db.execSQL(CurrentRecordingIdManager.DROP_TABLE_CMD);
		db.execSQL(RecordReplayManager.DROP_TABLE_CMD);
		onCreate(db);
	}

}
