package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.Date;

public class Location extends TimestampModel {

	private long id;
	private Date creationDate;
	private Recording recording;
	private android.location.Location location;

	public static final String TABLE_NAME = "locations";
	// SQL Columns
	private static int index = 0; // Because I'm lazy
	public static final String COL_ID = "_id";
	public static final int COL_ID_INDEX = index++;
	public static final String COL_CREATION_DATE = "creation_date";
	public static final int COL_CREATION_DATE_INDEX = index++;
	public static final String COL_RECORDING = "rec_id";
	public static final int COL_RECORDING_INDEX = index++;
	public static final String COL_TIMESTAMP = "loc_timestamp";
	public static final int COL_TIMESTAMP_INDEX = index++;
	public static final String COL_LONGITUDE = "loc_longitude";
	public static final int COL_LONGITUDE_INDEX = index++;
	public static final String COL_LATITUDE = "loc_latitude";
	public static final int COL_LATITUDE_INDEX = index++;
	public static final String COL_HAS_ALTITUDE = "loc_has_altitude";
	public static final int COL_HAS_ALTITUDE_INDEX = index++;
	public static final String COL_ALTITUDE = "loc_altitude";
	public static final int COL_ALTITUDE_INDEX = index++;
	public static final String COL_HAS_SPEED = "loc_has_speed";
	public static final int COL_HAS_SPEED_INDEX = index++;
	public static final String COL_SPEED = "loc_speed";
	public static final int COL_SPEED_INDEX = index++;
	public static final String COL_HAS_BEARING = "loc_has_bearing";
	public static final int COL_HAS_BEARING_INDEX = index++;
	public static final String COL_BEARING = "loc_bearing";
	public static final int COL_BEARING_INDEX = index++;
	public static final String COL_HAS_ACCURACY = "loc_has_accuracy";
	public static final int COL_HAS_ACCURACY_INDEX = index++;
	public static final String COL_ACCURACY = "loc_accuracy";
	public static final int COL_ACCURACY_INDEX = index++;
	public static final String COL_EXTRAS = "loc_extras";
	public static final int COL_EXTRAS_INDEX = index++;
	public static final String COL_PROVIDER = "loc_provider";
	public static final int COL_PROVIDER_INDEX = index++;

	// SQL Helpers
	public static final String COL_RECORDING_FK = FK + OPEN_PAREN + COL_RECORDING + CLOSE_PAREN
			+ REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
	public static final String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID
			+ INT + PK + COMMA + COL_CREATION_DATE + TEXT + COMMA + COL_RECORDING + INT + COMMA
			+ COL_TIMESTAMP + TEXT + COMMA + COL_LONGITUDE + REAL + COMMA + COL_LATITUDE + REAL
			+ COMMA + COL_HAS_ALTITUDE + INT + COMMA + COL_ALTITUDE + REAL + COMMA + COL_HAS_SPEED
			+ INT + COMMA + COL_SPEED + REAL + COMMA + COL_HAS_BEARING + INT + COMMA + COL_BEARING
			+ REAL + COMMA + COL_HAS_ACCURACY + INT + COMMA + COL_ACCURACY + REAL + COMMA
			+ COL_EXTRAS + TEXT + COMMA + COL_PROVIDER + TEXT + COMMA + COL_RECORDING_FK
			+ CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);
	public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

	public Location() {
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the recording
	 */
	public Recording getRecording() {
		return recording;
	}

	/**
	 * @param recording
	 *            the recording to set
	 */
	public void setRecording(Recording recording) {
		this.recording = recording;
	}

	public android.location.Location getLocation() {
		return location;
	}

	public void setLocation(android.location.Location location) {
		this.location = location;
	}

}
