
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MainActivity;

public abstract class ModelManager {

    protected Database manager;

    protected ModelManager(Context c) {
        manager = Database.getInstance(c);
    }

    public static boolean getBoolean(Cursor c, int index) {
        String ret = c.getString(index);
        if (ret.equals("0"))
            return false;
        else
            return true;
    }

    public static float getFloat(Cursor c, int index) {
        return Float.parseFloat(c.getString(index));
    }

    public static double getDouble(Cursor c, int index) {
        return Double.parseDouble(c.getString(index));
    }

    public static long getLong(Cursor c, int index) {
        return Long.parseLong(c.getString(index));
    }

    public static String getString(Cursor c, int index) {
        return c.getString(index);
    }

    public static int getInt(Cursor c, int index) {
        return Integer.parseInt(c.getString(index));
    }

    public static String serialize(float[] a) {
        String s = "";
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                if (i != a.length - 1) {
                    s += a[i] + ",";
                } else {
                    s += a[i];
                }
            }
        }
        return s;
    }
    
    public static float[] deserialize(String a) {
        String[] strValues = a.split(",");
        float[] values = new float[strValues.length];
        for(int i = 0; i < values.length; i++) {
            values[i] = Float.parseFloat(strValues[i]);
        }
        return values;
    }

    protected void insert(ContentValues values, String tableName) {
        SQLiteDatabase db = manager.openDatabase();
        long id = db.insertOrThrow(tableName, null, values);
        Log.v(MainActivity.TAG, "Inserted thing into " + tableName + " : " + id);
        manager.closeDatabase();
    }

    protected void update(String tableName, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = manager.openDatabase();
        db.update(tableName, values, where, whereArgs);
        manager.closeDatabase();
    }

}
