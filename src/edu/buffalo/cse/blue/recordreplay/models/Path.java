package edu.buffalo.cse.blue.recordreplay.models;


public class Path {
	
	private String name;
	private long timestamp;
	
	public Path(String name) {
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
