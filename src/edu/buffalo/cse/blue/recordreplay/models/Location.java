package edu.buffalo.cse.blue.recordreplay.models;

import java.util.Date;

public class Location extends TimestampModel {

	private long id;
	private Date creationDate;
	private Recording recording;
	// Copied fields form android.location.Location
	private long timestamp;
	private double longitude;
	private double latitude;
	private boolean hasAltitude;
	private double altitude;
	private boolean hasSpeed;
	private float speed;
	private boolean hasBearing;
	private float bearing;
	private boolean hasAccuracy;
	private float accuracy;
	private String provider;
	// We're going to serialize the Bundle to a String when we store it in the
	// database, and it's somewhat hard to go back according to the Internet,
	// but meh, doubt it.
	private String extras;

	public static final String TABLE_NAME = "locations";
	// SQL Columns
	public static final String COL_ID = "_id";
	public static final int COL_ID_INDEX = 0;
	public static final String COL_CREATION_DATE = "creation_date";
	public static final int COL_CREATION_DATE_INDEX = 1;
	public static final String COL_TIMESTAMP = "loc_timestamp";
	public static final int COL_TIMESTAMP_INDEX = 2;
	public static final String COL_LONGITUDE = "loc_longitude";
	public static final int COL_LONGITUDE_INDEX = 3;
	public static final String COL_LATITUDE = "loc_latitude";
	public static final int COL_LATITUDE_INDEX = 4;
	public static final String COL_HAS_ALTITUDE = "loc_has_altitude";
	public static final int COL_HAS_ALTITUDE_INDEX = 5;
	public static final String COL_ALTITUDE = "loc_altitude";
	public static final int COL_ALTITUDE_INDEX = 6;
	public static final String COL_HAS_SPEED = "loc_has_speed";
	public static final int COL_HAS_SPEED_INDEX = 7;
	public static final String COL_SPEED = "loc_speed";
	public static final int COL_SPEED_INDEX = 8;
	public static final String COL_HAS_BEARING = "loc_has_bearing";
	public static final int COL_HAS_BEARING_INDEX = 9;
	public static final String COL_BEARING = "loc_bearing";
	public static final int COL_BEARING_INDEX = 10;
	public static final String COL_HAS_ACCURACY = "loc_has_accuracy";
	public static final int COL_HAS_ACCURACY_INDEX = 11;
	public static final String COL_ACCURACY = "loc_accuracy";
	public static final int COL_ACCURACY_INDEX = 12;
	public static final String COL_EXTRAS = "loc_extras";
	public static final int COL_EXTRAS_INDEX = 13;
	public static final String COL_RECORDING = "rec_id";
	public static final int COL_RECORDING_INDEX = 14;
	public static final String COL_PROVIDER = "loc_provider";
	public static final int COL_PROVIDER_INDEX = 15;

	// SQL Helpers
	public static final String COL_RECORDING_FK = FK + OPEN_PAREN + COL_RECORDING + CLOSE_PAREN
			+ REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
	public static final String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID
			+ INT + PK + COMMA + COL_CREATION_DATE + TEXT + COMMA + COL_TIMESTAMP + TEXT + COMMA
			+ COL_LONGITUDE + REAL + COMMA + COL_LATITUDE + REAL + COMMA + COL_HAS_ALTITUDE + INT
			+ COMMA + COL_ALTITUDE + REAL + COMMA + COL_HAS_SPEED + INT + COMMA + COL_SPEED + REAL
			+ COMMA + COL_HAS_BEARING + INT + COMMA + COL_BEARING + REAL + COMMA + COL_HAS_ACCURACY
			+ INT + COMMA + COL_ACCURACY + REAL + COMMA + COL_EXTRAS + TEXT + COMMA + COL_RECORDING
			+ INT + COMMA + COL_PROVIDER + TEXT + COMMA + COL_RECORDING_FK + CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);
	public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
	
	public Location() {}

}
