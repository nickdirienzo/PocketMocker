package edu.buffalo.cse.blue.recordreplay;

import android.app.Application;
import edu.buffalo.cse.blue.recordreplay.models.DatabaseHandler;

public class RecordReplayApplication extends Application {
	
	private boolean recording;
	private DatabaseHandler db;
	
	@Override
	public void onCreate() {
		super.onCreate();
		recording = false;
		db = new DatabaseHandler(this);
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	public void toggleRecording() {
		recording = !recording;
	}
	
	public DatabaseHandler getDatabase() {
		return db;
	}

}
