package edu.buffalo.cse.blue.recordreplay.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class TimestampModel extends Model {
	
	protected String serializeDateToSqlString(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return dateFormat.format(d);
	}
	
}
