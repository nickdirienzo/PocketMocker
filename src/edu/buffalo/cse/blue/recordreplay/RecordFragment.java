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
	private TextView locationText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstance) {
		View view = inflater.inflate(R.layout.record_fragment, container, false);
		activity = (MainActivity) this.getActivity();
		Button b = (Button)view.findViewById(R.id.record_button);
		b.setOnClickListener(this);
		locationText = (TextView) view.findViewById(R.id.locationText);
		activity.setLocationText(locationText);
		return view;
	}
	
	public TextView getLocationText() {
		return locationText;
	}

	

	/**
	 * Record the user's location until they stop recording.
	 * 
	 * @param view
	 *            the Record Button pressed.
	 */
	public void record(View view) {
		Log.v("MEOW", "YOSUP");
		Button recordButton = (Button) view;
		activity.toggleRecording();
		if (activity.isRecording()) {
			Location lastLoc = activity.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(lastLoc == null) {
				Log.v(TAG, "LT: " + activity.getLocationText());
				activity.getLocationText().setText("Waiting for location...");
			} else {
				String displayLoc = MainActivity.buildLocationDisplayString(lastLoc);
				activity.getDatabase().insertLocation(lastLoc, activity.getActivePathId());
				activity.getLocationText().setText(activity.getLocationPrefix() + displayLoc);
			}
			recordButton.setText(R.string.stop_record);
		} else {
			recordButton.setText(R.string.record);
			activity.getLocationText().setText(this.getString(R.string.loc_placeholder));
		}
	}



	@Override
	public void onClick(View v) {
		Log.v(TAG, "HI");
		record(v);
	}

}
