package edu.buffalo.cse.blue.pocketmocker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import edu.buffalo.cse.blue.pocketmocker.models.Database;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;
import edu.buffalo.cse.blue.pocketmocker.models.ObjectivesManager;
import edu.buffalo.cse.blue.pocketmocker.models.RecordingManager;

public class MainActivity extends Activity {

	public static final String TAG = "REC";

	private TextView locationText;
	private Button recordButton;
	private String locationPrefix;
	private long currentRecordingId;

	private Spinner objectivesSpinner;
	private boolean spinnerInitFlag;

	private RecordManager recordManager;
	private ObjectivesManager objectivesManager;
	private MockLocationManager mockLocationManager;
	private RecordingManager recordingManager;

	private Database db;
	private String dbName;

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbName = "PocketMocker.db";
		db = new Database(this, dbName);

		recordManager = new RecordManager(this);
		objectivesManager = new ObjectivesManager(this);
		mockLocationManager = new MockLocationManager(this);
		recordingManager = new RecordingManager(this);

		this.checkFirstTimeUse();

		spinnerInitFlag = false;
		objectivesSpinner = (Spinner) this.findViewById(R.id.objectives_spinner);
		objectivesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (!spinnerInitFlag) {
					// Workaround for when this gets called when the
					// view is initially rendered
					spinnerInitFlag = true;
				} else {
					String selectedText = objectivesSpinner.getSelectedItem().toString();
					if (selectedText.equals(objectivesManager.getMockObjectiveString())) {
						displayNewObjectiveDialog();
					}
					Log.v(TAG, "Selected: " + objectivesSpinner.getSelectedItem().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				Log.v(TAG, "Spinner nothing.");
			}
		});
		this.populateObjectivesSpinner();

		locationPrefix = this.getString(R.string.loc_prefix);
		locationText = (TextView) this.findViewById(R.id.locationText);
		recordButton = (Button) this.findViewById(R.id.record_button);

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				new LocationListener() {

					@Override
					public void onLocationChanged(Location loc) {
						if (recordManager.isRecording()) {
							// Logging only when recording because otherwise
							// it's a huge mess in LogCat
							Log.v(TAG, "LocatoinChanged.");
							mockLocationManager.addLocation(loc, currentRecordingId);
							String displayLoc = MainActivity.buildLocationDisplayString(loc);
							locationText.setText(locationPrefix + displayLoc);
							// Log.v(TAG, "Location count: "
							// + dbHandler.getLocationCount());
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
					public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
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

	private void displayNewObjectiveDialog() {
		NewObjectiveDialog dialog = new NewObjectiveDialog();
		dialog.show(getFragmentManager(), TAG);
	}

	private void checkFirstTimeUse() {
		// No existing objectives besides the mock, so we can assume it's the
		// first time the user is using the app.
		if (objectivesManager.getObjectives().size() == 1) {
			NewObjectiveDialog dialog = new NewObjectiveDialog();
			Bundle b = new Bundle();
			b.putBoolean(NewObjectiveDialog.FIRST_KEY, true);
			dialog.setArguments(b);
			dialog.show(getFragmentManager(), TAG);
		}
	}

	public void populateObjectivesSpinner() {
		ArrayAdapter<String> objectivesSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, objectivesManager.getObjectivesNames());
		objectivesSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		objectivesSpinner.setAdapter(objectivesSpinnerAdapter);
	}
	
	public String getSelectedObjectiveName() {
		return objectivesSpinner.getSelectedItem().toString();
	}

	public static String buildLocationDisplayString(Location loc) {
		return "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";
	}

	public Database getDatabase() {
		return db;
	}
	
	public void setCurrentRecordingId(long i) {
		this.currentRecordingId = i;
	}
	
	public long getCurrentRecordingId() {
		return this.currentRecordingId;
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

	public ObjectivesManager getObjectivesManager() {
		return objectivesManager;
	}
	
	public MockLocationManager getMockLocationManager() {
		return mockLocationManager;
	}
	
	public RecordingManager getRecordingManager() {
		return recordingManager;
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
		if (recordManager.isRecording()) {
			recordButton.setText(R.string.stop_record);
		} else {
			recordButton.setText(R.string.record);
		}
	}

	public void recordButtonClicked(View v) {
		recordManager.record(v);
	}

	public void showOverwriteRecordingDialog() {
		OverwriteRecordingDialog dialog = new OverwriteRecordingDialog();
		Bundle b = new Bundle();
		b.putString("objective", this.getSelectedObjectiveName());
		dialog.setArguments(b);
		dialog.show(this.getFragmentManager(), TAG);
	}
	
	public void overwriteRecording() {
		Log.v(TAG, "To implement");
	}

}
