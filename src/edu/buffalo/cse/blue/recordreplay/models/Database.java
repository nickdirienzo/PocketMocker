package edu.buffalo.cse.blue.recordreplay.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;

	public Database(Context context, String name) {
		super(context, name, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Objective.CREATE_TABLE_CMD);
		db.execSQL(Recording.CREATE_TABLE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Our current policy is wipe and start new.
		db.execSQL(Objective.DROP_TABLE_CMD);
		db.execSQL(Recording.DROP_TABLE_CMD);
		onCreate(db);
	}
	
	public void addObjective(Objective o) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Objective.COL_NAME, o.getName());
		values.put(Objective.COL_CREATION_DATE, o.getCreationDateSqlString());
		values.put(Objective.COL_LAST_MODIFIED_DATE, o.getLastModifiedDateSqlString());
		// New objectives do not have a recording
		if(o.getRecording() == null) {
			values.put(Objective.COL_RECORDING, -1);
		} else {
			values.put(Objective.COL_RECORDING, o.getRecording().getId());
		}
		db.insert(Objective.TABLE_NAME, null, values);
		db.close();
	}

	public ArrayList<Objective> getObjectives() {
		ArrayList<Objective> objectives = new ArrayList<Objective>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(Objective.SELECT_ALL, null);
		if (cursor.moveToFirst()) {
			do {
				Objective objective = new Objective();
				objective.setId(Long.parseLong(cursor
						.getString(Objective.COL_ID_INDEX)));
				objective.setName(cursor.getString(Objective.COL_NAME_INDEX));
				objective.setCreationDate(cursor
						.getString(Objective.COL_CREATION_DATE_INDEX));
				objective.setLastModifiedDate(cursor
						.getString(Objective.COL_LAST_MODIFIED_DATE_INDEX));
				// TODO: Get recording
				objectives.add(objective);
			} while (cursor.moveToNext());
		}
		return objectives;
	}

}
