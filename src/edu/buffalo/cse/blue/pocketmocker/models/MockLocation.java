package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.Date;

import android.location.Location;

public class MockLocation extends TimestampModel {

	private long id;
	private Date creationDate;
	private long recordingId;
	private Location realLocation;

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
			+ COL_TIMESTAMP + INT + COMMA + COL_LONGITUDE + REAL + COMMA + COL_LATITUDE + REAL
			+ COMMA + COL_HAS_ALTITUDE + INT + COMMA + COL_ALTITUDE + REAL + COMMA + COL_HAS_SPEED
			+ INT + COMMA + COL_SPEED + REAL + COMMA + COL_HAS_BEARING + INT + COMMA + COL_BEARING
			+ REAL + COMMA + COL_HAS_ACCURACY + INT + COMMA + COL_ACCURACY + REAL + COMMA
			+ COL_EXTRAS + TEXT + COMMA + COL_PROVIDER + TEXT + COMMA + COL_RECORDING_FK
			+ CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);
	public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

	public MockLocation() {
		this.creationDate = new Date();
	}
	
	public MockLocation(Location l, long recId) {
		this.creationDate = new Date();
		this.realLocation = l;
		this.recordingId = recId;
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

	public String getCreationDateSqlString() {
		return this.serializeDateToSqlString(creationDate);
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setCreationDate(String sqlStringDate) {
		this.creationDate = this.serializeSqlStringToDate(sqlStringDate);
	}

	/**
	 * @return the recording
	 */
	public long getRecordingId() {
		return recordingId;
	}

	/**
	 * @param recording
	 *            the recording to set
	 */
	public void setRecordingId(long recId) {
		this.recordingId = recId;
	}

	public Location getRealLocation() {
		return realLocation;
	}

	public void setRealLocation(Location location) {
		this.realLocation = location;
	}

}
