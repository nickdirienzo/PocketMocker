package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class ModelManager {
	
	protected Database manager;
		
	protected ModelManager(Context c) {
		manager = Database.getInstance(c);
	}
	
	protected boolean getBoolean(Cursor c, int index) {
		String ret = c.getString(index);
		if(ret.equals("0")) return false;
		else return true;
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
	
	protected String getString(Cursor c, int index) {
	    return c.getString(index);
	}
	
	protected int getInt(Cursor c, int index) {
	    return Integer.parseInt(c.getString(index));
	}
	
	protected void insert(ContentValues values, String tableName) {
		new Thread(new InsertTask(values, tableName)).run();
	}
	
	protected class InsertTask implements Runnable {
		
		private ContentValues values;
		private String tableName;
		
		public InsertTask(ContentValues values, String tableName) {
			this.values = values;
			this.tableName = tableName;
		}

		@Override
		public void run() {
			SQLiteDatabase db = manager.openDatabase();
			db.insert(tableName, null, values);
			manager.closeDatabase();
		}
		
	}
	
	protected void update(String tableName, ContentValues values, String where, String[] whereArgs) {
		new Thread(new UpdateTask(tableName, values, where, whereArgs)).run();
	}
	
	protected class UpdateTask implements Runnable {
		
		private String tableName;
		private ContentValues values;
		private String where;
		private String[] whereArgs;
		
		public UpdateTask(String tableName, ContentValues values, String where, String[] whereArgs) {
			this.tableName = tableName;
			this.values = values;
			this.where = where;
			this.whereArgs = whereArgs;
		}

		@Override
		public void run() {
			SQLiteDatabase db = manager.openDatabase();
			db.update(tableName, values, where, whereArgs);
			manager.closeDatabase();
		}
		
	}

}
