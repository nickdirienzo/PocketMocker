package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class TimestampModel extends Model {

	protected String serializeDateToSqlString(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return dateFormat.format(d);
	}

	protected Date serializeSqlStringToDate(String s) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
					.parse(s);
		} catch (ParseException e) {
			return null;
		}
	}
	
	protected String serialize(float[] a) {
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
    
    protected float[] deserialize(String a) {
        String[] strValues = a.split(",");
        float[] values = new float[strValues.length];
        for(int i = 0; i < values.length; i++) {
            values[i] = Float.parseFloat(strValues[i]);
        }
        return values;
    }
	
	public abstract ContentValues toContentValues();

}
