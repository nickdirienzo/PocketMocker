package edu.buffalo.cse.blue.recordreplay.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ObjectivesManager {
	
	private Database db;
	private Objective addNewObjectiveMock;
	private String mockObjectiveString;
	
	public ObjectivesManager(Database d) {
		db = d;
		mockObjectiveString = "Add New Objective...";
		addNewObjectiveMock = new Objective(-1, mockObjectiveString, null);
	}
	
	public String getMockObjectiveString() {
		return mockObjectiveString;
	}
	
	public void addObjective(Objective o) {
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Objective.COL_NAME, o.getName());
		values.put(Objective.COL_CREATION_DATE, o.getCreationDateSqlString());
		values.put(Objective.COL_LAST_MODIFIED_DATE, o.getLastModifiedDateSqlString());
		// New objectives do not have a recording
		if (o.getRecording() == null) {
			values.put(Objective.COL_RECORDING, -1);
		} else {
			values.put(Objective.COL_RECORDING, o.getRecording().getId());
		}
		sql.insert(Objective.TABLE_NAME, null, values);
		sql.close();
	}
	
	public Objective getObjectiveByName(String name) {
		SQLiteDatabase sql = db.getReadableDatabase();
		Objective objective = null;
		Cursor cursor = sql.query(Objective.TABLE_NAME, Objective.ALL_COLS, Objective.COL_NAME
				+ "=?", null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		objective = new Objective();
		objective.setId(Long.parseLong(cursor.getString(Objective.COL_ID_INDEX)));
		objective.setName(cursor.getString(Objective.COL_NAME_INDEX));
		objective.setCreationDate(cursor.getString(Objective.COL_CREATION_DATE_INDEX));
		objective.setLastModifiedDate(cursor.getString(Objective.COL_LAST_MODIFIED_DATE_INDEX));
		return objective;
	}

	public ArrayList<Objective> getObjectives() {
		ArrayList<Objective> objectives = new ArrayList<Objective>();
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.rawQuery(Objective.SELECT_ALL, null);
		if (cursor.moveToFirst()) {
			do {
				Objective objective = new Objective();
				objective.setId(Long.parseLong(cursor.getString(Objective.COL_ID_INDEX)));
				objective.setName(cursor.getString(Objective.COL_NAME_INDEX));
				objective.setCreationDate(cursor.getString(Objective.COL_CREATION_DATE_INDEX));
				objective.setLastModifiedDate(cursor
						.getString(Objective.COL_LAST_MODIFIED_DATE_INDEX));
				// TODO: Get recording
				objectives.add(objective);
			} while (cursor.moveToNext());
		}
		objectives.add(addNewObjectiveMock);
		return objectives;
	}

}
