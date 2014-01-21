package edu.buffalo.cse.blue.pocketmocker.models;

import java.util.ArrayList;
import java.util.Date;

public class Recording extends TimestampModel {

	private long id;
	private Date creationDate;
	private ArrayList<MockLocation> locations;

	// SQL Helpers
	public static final String TABLE_NAME = "recordings";
	public static final String COL_ID = "_id";
	public static final String COL_CREATION_DATE = "creation_date";
	public static final String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME
			+ OPEN_PAREN + COL_ID + INT + PK + COMMA + COL_CREATION_DATE + TEXT
			+ CLOSE_PAREN;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);

	public Recording() {
		this.creationDate = new Date();
	}
	
	public Recording(long id, ArrayList<MockLocation> locations) {
		this.id = id;
		this.creationDate = new Date();
		this.locations = locations;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public ArrayList<MockLocation> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<MockLocation> locations) {
		this.locations = locations;
	}
}
