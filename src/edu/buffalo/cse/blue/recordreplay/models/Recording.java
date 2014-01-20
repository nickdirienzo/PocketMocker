package edu.buffalo.cse.blue.recordreplay.models;

import java.util.ArrayList;
import java.util.Date;

public class Recording extends TimestampModel {

	private long id;
	private Date creationDate;
	private ArrayList<Location> locations;

	// SQL Helpers
	public static final String TABLE_NAME = "recordings";
	public static final String COL_ID = "id";
	public static final String COL_CREATION_DATE = "creation_date";
	public static final String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME
			+ OPEN_PAREN + COL_ID + INT + PK + AUTO_INC + COMMA
			+ COL_CREATION_DATE + TEXT + COMMA;
	public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);
	
	public Recording(long id, ArrayList<Location> locations) {
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
	
	public String getCreationDateString() {
		return this.serializeDateToSqlString(creationDate);
	}
	
	public void setCreationDate(Date d) {
		creationDate = d;
	}
	
	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}
}
