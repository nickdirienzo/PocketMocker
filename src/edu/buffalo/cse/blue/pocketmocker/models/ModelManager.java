package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.database.Cursor;

public abstract class ModelManager {
	
	protected Database db;
		
	protected ModelManager(Context c) {
		db = Database.getInstance(c);
	}
	
	protected boolean isTrue(Cursor c, int index) {
		return Boolean.parseBoolean(c.getString(index));
	}

	protected float getFloat(Cursor c, int index) {
		return Float.parseFloat(c.getString(index));
	}

	protected double getDouble(Cursor c, int index) {
		return Double.parseDouble(c.getString(index));
	}

	protected long getLong(Cursor c, int index) {
		return Long.parseLong(c.getString(index));
	}

}
