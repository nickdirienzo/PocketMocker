package edu.buffalo.cse.blue.pocketmocker.models;

import android.database.Cursor;
import edu.buffalo.cse.blue.pocketmocker.MainActivity;

public class ModelManager {
	
	protected MainActivity activity;
	
	public ModelManager(MainActivity a) {
		activity = a;
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
