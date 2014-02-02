package edu.buffalo.cse.blue.pocketmocker;

import android.app.Application;
import android.location.Location;
import android.util.Log;

public class PocketMockerApplication extends Application {
	
	public static final boolean DO_RECORD = true;
	public static final boolean DO_NOT_RECORD = false;
	public static final boolean DO_REPLAY = true;
	public static final boolean DO_NOT_REPLAY = false;
	
	private boolean isRecording;
	private boolean isReplaying;
	
	@Override
	public void onCreate() {
		super.onCreate();	
		isRecording = false;
		isReplaying = false;
	}
	
	public boolean isRecording() {
		return isRecording;
	}
	
	public void toggleIsRecording() {
		isRecording = !isRecording;
	}
	
	public void setIsRecording(boolean b) {
		isRecording = b;
	}
	
	public boolean isReplaying() {
		return isReplaying;
	}
	
	public void toggleIsReplaying() {
		isReplaying = !isReplaying;
	}
	
	public void setIsReplaying(boolean b) {
		isReplaying = b;
	}
	
	public String buildLocationDisplayString(Location loc) {
		return "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";
	}

}
