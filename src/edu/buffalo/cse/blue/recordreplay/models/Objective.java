package edu.buffalo.cse.blue.recordreplay.models;


public class Objective {
	
	private String name;
	private long timestamp;
	
	public Objective(String name) {
		this.name = name;
		this.timestamp = System.currentTimeMillis();
	}
	
	public String getName() {
		return name;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}
