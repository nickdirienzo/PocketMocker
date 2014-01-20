package edu.buffalo.cse.blue.recordreplay.models;

import java.util.Date;

public class Objective extends TimestampModel {

	private long id;
	private String name;
	private Date creationDate;
	private Date lastModifiedDate;
	private Recording recording;

	// SQL Helpers
	public static final String TABLE_NAME = "objectives";
	public static final String COL_ID = "id";
	public static final String COL_NAME = "name";
	public static final String COL_CREATION_DATE = "creation_date";
	public static final String COL_LAST_MODIFIED_DATE = "last_modified_date";
	public static final String COL_RECORDING = "rec_id";
	public static final String COL_RECORDING_FK = FK + OPEN_PAREN
			+ COL_RECORDING + CLOSE_PAREN + REFS + Recording.TABLE_NAME
			+ OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
	public static final String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME
			+ OPEN_PAREN + COL_ID + INT + PK + AUTO_INC + COMMA + COL_NAME
			+ TEXT + COMMA + COL_CREATION_DATE + TEXT + COMMA
			+ COL_LAST_MODIFIED_DATE + TEXT + COMMA + COL_RECORDING + INT
			+ COMMA + COL_RECORDING_FK;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);

	public Objective(long id, String name, Recording recording) {
		this.id = id;
		this.name = name;
		this.creationDate = new Date();
		this.recording = recording;
		this.lastModifiedDate = creationDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getCreationDateSqlString() {
		return this.serializeDateToSqlString(creationDate);
	}

	public void setCreationDate(Date d) {
		creationDate = d;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public String getLastModifiedDateString() {
		return this.serializeDateToSqlString(lastModifiedDate);
	}

	public void setLastModifiedDate(Date d) {
		lastModifiedDate = d;
	}

	public Recording getRecordings() {
		return recording;
	}

	public void setRecording(Recording recording) {
		this.recording = recording;
	}

}
