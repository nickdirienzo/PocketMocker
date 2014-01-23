package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "PocketMocker.db";


	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Objective.CREATE_TABLE_CMD);
		db.execSQL(Recording.CREATE_TABLE_CMD);
		db.execSQL(MockLocation.CREATE_TABLE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Our current policy is wipe and start new.
		db.execSQL(Objective.DROP_TABLE_CMD);
		db.execSQL(Recording.DROP_TABLE_CMD);
		db.execSQL(MockLocation.DROP_TABLE_CMD);
		onCreate(db);
	}

}
