package edu.buffalo.cse.blue.recordreplay;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.buffalo.cse.blue.recordreplay.models.Database;
public class MainActivity extends Activity {

	private String TAG = "REC";

	private TextView locationText;
	private Button recordButton;
	private String locationPrefix;
	private String activePathId;

	private RecordManager recordManager;
	
	private Database db;
	private String dbName;

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		recordManager = new RecordManager(this);
		dbName = "PocketMocker1.db";
		db = new Database(this, dbName);

		locationPrefix = this.getString(R.string.loc_prefix);
		locationText = (TextView) this.findViewById(R.id.locationText);
		recordButton = (Button) this.findViewById(R.id.record_button);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new LocationListener() {

					@Override
					public void onLocationChanged(Location loc) {
						if (recordManager.isRecording()) {
							// Logging only when recording because otherwise
							// it's a huge mess in LogCat
							Log.v(TAG,
									"LocatoinChanged. Loc: " + loc.toString());
							// Do we need to log every location change?
							//dbHandler.insertLocation(loc, activePathId);
							String displayLoc = MainActivity
									.buildLocationDisplayString(loc);
							locationText.setText(locationPrefix + displayLoc);
//							Log.v(TAG, "Location count: "
//									+ dbHandler.getLocationCount());
						}
					}

					@Override
					public void onProviderDisabled(String arg0) {
						Log.v(TAG,
								"Provider disabled. Alert user that we need this turned on to function.");
					}

					@Override
					public void onProviderEnabled(String arg0) {
						Log.v(TAG, "Provider enabled. Woot, we can log things.");
					}

					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {
						Log.v(TAG, "onStatusChanged. Nothing to do here yet.");
					}

				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static String buildLocationDisplayString(Location loc) {
		return "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";
	}
	
	public Database getDatabase() {
		return db;
	}

	public String getActivePathId() {
		return activePathId;
	}

	public TextView getLocationText() {
		return locationText;
	}

	public String getLocationPrefix() {
		return locationPrefix;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void updateLocationText(Location loc) {
		String displayLoc = MainActivity.buildLocationDisplayString(loc);
		locationText.setText(getLocationPrefix() + displayLoc);
	}
	
	public void updateLocationText(String s) {
		locationText.setText(s);
	}
	
	public void resetLocationText() {
		locationText.setText(this.getString(R.string.loc_placeholder));
	}

	public void toggleRecordingButton() {
		if(recordManager.isRecording()) {
			recordButton.setText(R.string.stop_record);
		} else {
			recordButton.setText(R.string.record);
		}
	}

	public void recordButtonClicked(View v) {
		recordManager.record(v);
	}

}
