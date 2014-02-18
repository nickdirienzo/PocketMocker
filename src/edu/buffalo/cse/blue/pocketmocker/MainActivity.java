
package edu.buffalo.cse.blue.pocketmocker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEventManager;
import edu.buffalo.cse.blue.pocketmocker.models.Objective;
import edu.buffalo.cse.blue.pocketmocker.models.ObjectivesManager;
import edu.buffalo.cse.blue.pocketmocker.models.RecordReplayManager;
import edu.buffalo.cse.blue.pocketmocker.models.Recording;
import edu.buffalo.cse.blue.pocketmocker.models.RecordingManager;

import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    public static final String TAG = "REC";

    private PocketMockerApplication app;

    private TextView locationText;
    private Button recordButton;
    private String locationPrefix;

    private Spinner objectivesSpinner;
    private boolean spinnerInitFlag;

    private ObjectivesManager objectivesManager;
    private RecordingManager recordingManager;
    private MockLocationManager mockLocationManager;
    private RecordReplayManager recordReplayManager;
    private MockSensorEventManager mockSensorEventManager;

    private LocationManager locationManager;
    // We use lastLocation as the location to add when we have other updates
    // that don't receive a Location as a parameter in the callback
    private Location lastLocation;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (PocketMockerApplication) getApplicationContext();

        objectivesManager = ObjectivesManager.getInstance(getApplicationContext());
        recordingManager = RecordingManager.getInstance(getApplicationContext());
        mockLocationManager = MockLocationManager.getInstance(getApplicationContext());
        recordReplayManager = RecordReplayManager.getInstance(getApplicationContext());
        mockSensorEventManager = MockSensorEventManager.getInstance(getApplicationContext());
        recordReplayManager.setIsRecording(false);
        app.setIsRecording(false);

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
                    recordReplayManager.setIsRecording(false);
                    app.setIsRecording(false);
                    toggleRecordingButton();
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
        List<Objective> objectives = objectivesManager.getObjectives();
        if (objectives.size() > 1) {
            Log.v(TAG, "Setting current rec id to: " + objectives.get(0).getId());
            recordingManager.setCurrentRecordingId(objectives.get(0).getId());
        }

        locationPrefix = this.getString(R.string.loc_prefix);
        locationText = (TextView) this.findViewById(R.id.locationText);
        recordButton = (Button) this.findViewById(R.id.record_button);

        initLocationManager();
        initSensorManager();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initLocationManager() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.v(TAG, "Requesting location updates...");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {

                    @Override
                    public void onLocationChanged(Location loc) {
                        if (app.isRecording()) {
                            // Logging only when recording because otherwise
                            // it's a huge mess in LogCat
                            Log.v(TAG, "LocatoinChanged.");
                            lastLocation = loc;
                            mockLocationManager.addLocation(loc, "onLocationChanged", -1);
                            String displayLoc = app.buildLocationDisplayString(loc);
                            locationText.setText(locationPrefix + displayLoc);
                            // Log.v(TAG, "Location count: "
                            // + dbHandler.getLocationCount());
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.v(TAG,
                                "Provider disabled. Alert user that we need this turned on to function.");
                        lastLocation.setProvider(provider);
                        mockLocationManager.addLocation(lastLocation, "onProviderDisabled", -1);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.v(TAG, "Provider enabled. Woot, we can log things.");
                        lastLocation.setProvider(provider);
                        mockLocationManager.addLocation(lastLocation, "onProviderEnabled", -1);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.v(TAG, "onStatusChanged. Nothing to do here yet.");
                        lastLocation.setExtras(extras);
                        lastLocation.setProvider(provider);
                        mockLocationManager.addLocation(lastLocation, "onStatusChanged", status);
                    }

                });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initSensorManager() {
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        for (Sensor s : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            Log.v(TAG, "Registering listener for sensor: " + s.getName());
            if (s.getType() != Sensor.TYPE_SIGNIFICANT_MOTION) {
                sensorManager.registerListener(new SensorEventListener() {

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        if (app.isRecording()) {
                            Log.v(TAG, "Accuracy changed for sensor " +
                                    sensor.getName()
                                    + ". Accuracy: " + accuracy);
                            mockSensorEventManager.addAccuracyChange(sensor, accuracy);
                            
                        }
                    }

                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (app.isRecording()) {
                            Log.v(TAG, "Sensor " + event.sensor.getName() +
                                    " changed. Acc: "
                                    + event.accuracy + " timestamp: " + event.timestamp
                                    + " values: " + event.values);
                            mockSensorEventManager.addSensorEvent(event);
                        }
                    }

                }, s, SensorManager.SENSOR_DELAY_FASTEST);
            } else if (s.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    sensorManager.requestTriggerSensor(new TriggerEventListener() {

                        @Override
                        public void onTrigger(TriggerEvent event) {
                            if (app.isRecording()) {
                                Log.v(TAG, "onTrigger for sensor: " +
                                        event.sensor.getName()
                                        + " values: " + event.values);
                                mockSensorEventManager.addTrigerEvent(event);
                            }
                        }
                    }, s);
                }
            }
        }
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

    public TextView getLocationText() {
        return locationText;
    }

    public String getLocationPrefix() {
        return locationPrefix;
    }

    public void updateLocationText(Location loc) {
        String displayLoc = app.buildLocationDisplayString(loc);
        locationText.setText(getLocationPrefix() + displayLoc);
    }

    public void updateLocationText(String s) {
        locationText.setText(s);
    }

    public void resetLocationText() {
        locationText.setText(this.getString(R.string.loc_placeholder));
    }

    public void toggleRecordingButton() {
        if (app.isRecording()) {
            recordButton.setText(R.string.stop_record);
        } else {
            recordButton.setText(R.string.record);
        }
        Log.v(TAG, "Recording: " + app.isRecording());
    }

    private void prepareToRecord() {
        toggleRecordingButton();
        if (app.isRecording()) {
            Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLoc == null) {
                updateLocationText("Waiting for location...");
            } else {
                lastLocation = lastLoc;
                mockLocationManager.addLocation(lastLoc, "onLocationChanged", -1);
                updateLocationText(lastLoc);
            }
        } else {
            resetLocationText();
        }
    }

    public void recordButtonClicked(View v) {
        Log.v(MainActivity.TAG, "Checking if objective (" + getSelectedObjectiveName()
                + ") already has a recording.");
        if (objectivesManager.hasExistingRecording(getSelectedObjectiveName())) {
            showOverwriteRecordingDialog();
        } else {
            recordReplayManager.toggleRecording();
            app.toggleIsRecording();
            prepareToRecord();
        }
    }

    public void replayButtonClicked(View v) {
        Button replayButton = (Button) v;
        if (replayButton.getText().equals(this.getString(R.string.replay))) {
            Log.v(TAG, "Stop replaying.");
            replayButton.setText(R.string.stop_replaying);
        } else {
            Log.v(TAG, "Start replaying.");
            replayButton.setText(R.string.replay);
        }
        recordReplayManager.toggleReplaying();
    }

    public void showOverwriteRecordingDialog() {
        OverwriteRecordingDialog dialog = new OverwriteRecordingDialog();
        Bundle b = new Bundle();
        b.putString("objective", this.getSelectedObjectiveName());
        dialog.setArguments(b);
        dialog.show(this.getFragmentManager(), TAG);
    }

    public void overwriteRecording() {
        long recId = recordingManager.addRecording(new Recording());
        Objective o = objectivesManager.getObjectiveByName(this.getSelectedObjectiveName());
        o.setRecordingId(recId);
        o.setLastModifiedDate(new Date());
        objectivesManager.updateObjective(o);
        recordingManager.setCurrentRecordingId(recId);
        recordReplayManager.setIsRecording(true);
        app.setIsRecording(true);
        this.toggleRecordingButton();
    }

    public void openTestMockerServiceActivity(View view) {
        Intent intent = new Intent(this, TestMockerServiceActivity.class);
        this.startActivity(intent);
    }

}
