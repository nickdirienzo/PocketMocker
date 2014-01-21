package edu.buffalo.cse.blue.pocketmocker.models;

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

}
