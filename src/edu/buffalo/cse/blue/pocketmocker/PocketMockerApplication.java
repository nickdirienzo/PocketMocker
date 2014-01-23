package edu.buffalo.cse.blue.pocketmocker;

import android.app.Application;

public class PocketMockerApplication extends Application {
	
	private long currentRecordingId;
	
	@Override
	public void onCreate() {
		super.onCreate();	
		currentRecordingId = 0;
	}
	
	public void setCurrentRecordingId(long id) {
		currentRecordingId = id;
	}
	
	public long getCurrentRecordingId() {
		return currentRecordingId;
	}

}
