package edu.buffalo.cse.blue.recordreplay.models;

import android.provider.BaseColumns;

public final class Models  {
	
	// Global constants
	private static final String INT = " INT";
	private static final String REAL = " REAL";
	private static final String TEXT = " TEXT";
	private static final String COMMA = ",";
	private static final String PK = " PRIMARY KEY";
	
	public Models() {}
	
	public static abstract class Location implements BaseColumns {
		
		public static final String TABLE_NAME = "locations";
		// Columns
		// From android.location.Locatoin
		public static final String LOC_PROVIDER = "loc_provider";
		public static final String LOC_TIME = "loc_time";
		public static final String LOC_LAT = "loc_lat";
		public static final String LOC_LONG = "loc_long";
		public static final String LOC_HAS_ALTITUDE = "loc_has_altitude";
		public static final String LOC_ALTITUDE = "loc_altitude";
		public static final String LOC_HAS_SPEED = "loc_has_speed";
		public static final String LOC_SPEED = "loc_speed";
		public static final String LOC_HAS_BEARING = "loc_has_bearing";
		public static final String LOC_BEARING = "loc_bearing";
		public static final String LOC_HAS_ACCURACY = "loc_has_accuracy";
		public static final String LOC_ACCURACY = "loc_accuracy";
		// Seems that the only extra from the GPS is a Bundle for the data size.
		// From LogCat: Bundle[mParcelledData.dataSize=44]
		//
		// We can serialize the class to JSON, store it as a String in SQLite,
		// then recreate it from the string
		public static final String LOC_EXTRAS = "loc_extras";
		// PocketMocker required columns
		public static final String LOC_PATH_ID = "loc_path_id";
		
		// Helpers
		public static final String SQL_CREATE_ENTIRES = 
				"CREATE TABLE " + Location.TABLE_NAME + " (" +
				Location._ID + INT + PK + COMMA +
				Location.LOC_PROVIDER + TEXT + COMMA + 
				Location.LOC_TIME + TEXT + COMMA + 
				Location.LOC_LAT + REAL + COMMA + 
				Location.LOC_LONG + REAL + COMMA + 
				Location.LOC_HAS_ALTITUDE + INT + COMMA + 
				Location.LOC_ALTITUDE + REAL + COMMA + 
				Location.LOC_HAS_SPEED + INT + COMMA + 
				Location.LOC_SPEED + REAL + COMMA + 
				Location.LOC_HAS_BEARING + INT + COMMA +
				Location.LOC_BEARING + REAL + COMMA + 
				Location.LOC_HAS_ACCURACY + INT + COMMA + 
				Location.LOC_ACCURACY + REAL + COMMA +
				Location.LOC_EXTRAS + TEXT + COMMA +
				Location.LOC_PATH_ID + INT +
				" )";
		
		public static final String SQL_DELETE_ENTRIES = 
				"DROP TABLE IF EXISTS " + Location.TABLE_NAME;
				
	}
	
	public static abstract class Objective implements BaseColumns {
		public static final String TABLE_NAME = "objective";
		// Columns
		public static final String NAME = "objective_name";
		public static final String CREATED_TIMESTAMP = "objective_created_timestamp";
		public static final String DESCRIPTION = "objective_description";
		// Helper queries
		public static final String SQL_CREATE_TABLE = 
				"CREATE TABLE " + Objective.TABLE_NAME + "( " +
				Objective._ID + INT + PK + COMMA +
				Objective.NAME + TEXT + COMMA +
				Objective.CREATED_TIMESTAMP + TEXT + 
				Objective.DESCRIPTION + TEXT + " )";
		
		public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + Objective.TABLE_NAME;
		
	}

}
