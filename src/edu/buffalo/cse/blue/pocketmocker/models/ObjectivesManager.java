package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.buffalo.cse.blue.pocketmocker.MainActivity;

public class ObjectivesManager extends ModelManager {

	private Objective addNewObjectiveMock;
	private String mockObjectiveString;

	public ObjectivesManager(MainActivity a) {
		super(a);
		mockObjectiveString = "Add New Objective...";
		addNewObjectiveMock = new Objective(Objective.UNKNOWN_ID, mockObjectiveString,
				Objective.UNKNOWN_ID);
	}

	public String getMockObjectiveString() {
		return mockObjectiveString;
	}

	public void addObjective(Objective o) {
		ContentValues values = new ContentValues();
		values.put(Objective.COL_NAME, o.getName());
		values.put(Objective.COL_CREATION_DATE, o.getCreationDateSqlString());
		values.put(Objective.COL_LAST_MODIFIED_DATE, o.getLastModifiedDateSqlString());
		// New objectives do not have a recording
		if (o.getRecordingId() == -1) {
			long recId = activity.getRecordingManager().addRecording(new Recording());
			values.put(Objective.COL_RECORDING, recId);
			activity.setCurrentRecordingId(recId);
		} else {
			values.put(Objective.COL_RECORDING, o.getRecordingId());
		}
		SQLiteDatabase sql = activity.getDatabase().getWritableDatabase();
		sql.insert(Objective.TABLE_NAME, null, values);
		sql.close();
	}

	public Objective getObjectiveByName(String name) {
		SQLiteDatabase sql = activity.getDatabase().getReadableDatabase();
		Objective objective;
		Cursor cursor = sql.query(Objective.TABLE_NAME, Objective.ALL_COLS, Objective.COL_NAME
				+ "=?", new String[] { name }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		objective = new Objective();
		objective.setId(Long.parseLong(cursor.getString(Objective.COL_ID_INDEX)));
		objective.setName(cursor.getString(Objective.COL_NAME_INDEX));
		objective.setCreationDate(cursor.getString(Objective.COL_CREATION_DATE_INDEX));
		objective.setLastModifiedDate(cursor.getString(Objective.COL_LAST_MODIFIED_DATE_INDEX));
		objective.setRecordingId(Long.parseLong(cursor.getString(Objective.COL_RECORDING_INDEX)));
		sql.close();
		return objective;
	}

	public ArrayList<Objective> getObjectives() {
		ArrayList<Objective> objectives = new ArrayList<Objective>();
		SQLiteDatabase sql = activity.getDatabase().getWritableDatabase();
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
		sql.close();
		objectives.add(addNewObjectiveMock);
		return objectives;
	}

	public String[] getObjectivesNames() {
		List<Objective> objectives = this.getObjectives();
		String[] objectivesNames = new String[objectives.size()];
		for (int i = 0; i < objectivesNames.length; i++) {
			objectivesNames[i] = objectives.get(i).getName();
		}
		return objectivesNames;
	}

	public boolean hasExistingRecording(String objectiveName) {
		Objective o = this.getObjectiveByName(objectiveName);
		Log.v(MainActivity.TAG, "Looking up locations for (Objective " + o.getId() + ") recording "
				+ o.getRecordingId());
		return activity.getRecordingManager().hasLocations(o.getRecordingId());
	}

}