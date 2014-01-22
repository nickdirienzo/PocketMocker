package edu.buffalo.cse.blue.pocketmocker;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

	public void prepareToRecord() {
		activity.toggleRecordingButton();
		if (recording) {
			Location lastLoc = activity.getLocationManager().getLastKnownLocation(
					LocationManager.GPS_PROVIDER);
			if (lastLoc == null) {
				activity.updateLocationText("Waiting for location...");
			} else {
				activity.getMockLocationManager().addLocation(lastLoc,
						activity.getCurrentRecordingId());
				activity.updateLocationText(lastLoc);
			}
		} else {
			activity.resetLocationText();
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
		Log.v(MainActivity.TAG, "Checking if objective (" + activity.getSelectedObjectiveName()
				+ ") already has a recording.");
		if (activity.getObjectivesManager().hasExistingRecording(
				activity.getSelectedObjectiveName())) {
			activity.showOverwriteRecordingDialog();
		} else {
			toggleRecording();
			prepareToRecord();
		}
	}
}
