package edu.buffalo.cse.blue.pocketmocker;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;

public class RecordManager {

	private final String TAG = "RR_REC_FRAG";

	private MainActivity activity;

	private boolean recording = false;

	public RecordManager(MainActivity a) {
		activity = a;
		recording = false;
	}

	public Location prepareToRecord() {
		activity.toggleRecordingButton();
		if (recording) {
			Location lastLoc = activity.getLocationManager()
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastLoc == null) {
				Log.v(TAG, "LT: " + activity.getLocationText());
				activity.updateLocationText("Waiting for location...");
			} else {
				activity.updateLocationText(lastLoc);
			}
			return lastLoc;
		} else {
			activity.resetLocationText();
			return null;
		}
	}

	public boolean isRecording() {
		return recording;
	}

	private void toggleRecording() {
		recording = !recording;
	}

	/**
	 * Record the user's location until they stop recording.
	 * 
	 * @param view
	 *            the Record Button pressed.
	 */
	public void record(View view) {
		toggleRecording();
		Location lastLoc = prepareToRecord();
//		if (lastLoc != null)
//			activity.getDatabaseHandler().insertLocation(lastLoc,
//					activity.getActivePathId());
	}

}
