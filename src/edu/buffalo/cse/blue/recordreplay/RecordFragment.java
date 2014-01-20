package edu.buffalo.cse.blue.recordreplay;

import android.app.Fragment;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RecordFragment extends Fragment implements OnClickListener {

	private final String TAG = "RR_REC_FRAG";

	private MainActivity activity;
	private RecordReplayApplication app;

	// Views
	private TextView locationText;
	private Button recordButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstance) {
		View view = inflater
				.inflate(R.layout.record_fragment, container, false);
		activity = (MainActivity) this.getActivity();
		app = activity.getApp();

		// Install ourselves as the onClickListener
		recordButton = (Button) view.findViewById(R.id.record_button);
		recordButton.setOnClickListener(this);

		// Views
		locationText = (TextView) view.findViewById(R.id.locationText);
		// This is stupid
		activity.setLocationText(locationText);
		// Render the correct UI. We assume the last location has already been
		// logged while the user switched between tabs.
		drawAndReturnLocation();
		return view;
	}

	public TextView getLocationText() {
		return locationText;
	}

	public Location drawAndReturnLocation() {
		if (app.isRecording()) {
			Location lastLoc = activity.getLocationManager()
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastLoc == null) {
				Log.v(TAG, "LT: " + activity.getLocationText());
				locationText.setText("Waiting for location...");
			} else {
				String displayLoc = MainActivity
						.buildLocationDisplayString(lastLoc);
				locationText.setText(activity.getLocationPrefix() + displayLoc);
			}
			recordButton.setText(R.string.stop_record);
			return lastLoc;
		} else {
			recordButton.setText(R.string.record);
			locationText.setText(this.getString(R.string.loc_placeholder));
			return null;
		}
	}

	/**
	 * Record the user's location until they stop recording.
	 * 
	 * @param view
	 *            the Record Button pressed.
	 */
	public void record(View view) {
		app.toggleRecording();
		Location lastLoc = drawAndReturnLocation();
		if (lastLoc != null)
			app.getDatabase().insertLocation(lastLoc,
					activity.getActivePathId());
	}

	@Override
	public void onClick(View v) {
		Log.v(TAG, "HI");
		record(v);
	}

}
