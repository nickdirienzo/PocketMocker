
package edu.buffalo.cse.blue.pocketmocker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;
import edu.buffalo.cse.blue.pocketmocker.models.MockSensorEventManager;
import edu.buffalo.cse.blue.pocketmocker.models.MockWifiManager;
import edu.buffalo.cse.blue.pocketmocker.models.Objective;
import edu.buffalo.cse.blue.pocketmocker.models.ObjectivesManager;
import edu.buffalo.cse.blue.pocketmocker.models.RecordReplayManager;
import edu.buffalo.cse.blue.pocketmocker.models.Recording;
import edu.buffalo.cse.blue.pocketmocker.models.RecordingManager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    public static final String TAG = "PM";

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
    private MockWifiManager mockScanResultManager;

    private LocationManager locationManager;
    private HandlerThread locationHandlerThread;
    private LocationListener locationListener;
    // We use lastLocation as the location to add when we have other updates
    // that don't receive a Location as a parameter in the callback
    private Location lastLocation;

    private SensorManager sensorManager;
    private HandlerThread sensorHandlerThread;
    private SensorEventListener sensorEventListener;

    private WifiManager mWifiManager;

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
        mockScanResultManager = MockWifiManager.getInstance(getApplicationContext());
        recordReplayManager.setIsRecording(false);
        app.setIsRecording(false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        HandlerThread wifiReceiverThread = new HandlerThread("WifiReceiverThread");
        wifiReceiverThread.start();
        Handler wifiReceiverHandler = new Handler(wifiReceiverThread.getLooper());
        BroadcastReceiver wifiReceiver = new WifiManagerReceiver(getApplicationContext());
        this.registerReceiver(wifiReceiver, filter, "", wifiReceiverHandler);

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
        // locationText = (TextView) this.findViewById(R.id.locationText);
        recordButton = (Button) this.findViewById(R.id.record_button);

        initLocationManager();
        initSensorManager();
        initWifiManager();

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        List<NeighboringCellInfo> towers = tel.getNeighboringCellInfo();
        Log.v(TAG, "neighbor info: " + towers.size() + " crap");
        CdmaCellLocation cellLoc = (CdmaCellLocation) tel.getCellLocation();
        Log.v(TAG,
                "stationId: " + cellLoc.getBaseStationId() + " statLat: "
                        + cellLoc.getBaseStationLatitude() + " statLong: "
                        + cellLoc.getBaseStationLongitude());
        for (NeighboringCellInfo info : towers) {
            Log.v(TAG,
                    "cid: " + info.getCid() + " lac: " + info.getLac() + " ntype: "
                            + info.getNetworkType() + " psc: " + info.getPsc() + " rssi: "
                            + info.getRssi());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This is safe. We shouldn't record if the RecordActivity isn't in the
        // foreground.
        this.stopLocationListener();
        this.stopSensorListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.about_detail);
                builder.create().show();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSensorText(final String s) {
        // findViewById(R.id.sensorText).post(new Runnable() {
        //
        // @Override
        // public void run() {
        // TextView t = (TextView) findViewById(R.id.sensorText);
        // t.setText(s);
        // }
        //
        // });
    }

    private void initSensorManager() {
        // Starts when the recording process begins
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorHandlerThread = new HandlerThread("SensorHandlerThread");
        sensorHandlerThread.start();
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Don't record sensor data until we have location data (for
                // now)
                if (lastLocation != null) {
                    Log.v(TAG, "Accuracy change: " + accuracy);
                    mockSensorEventManager.addAccuracyChange(sensor, accuracy);
                    updateSensorText("Accuracy changed!");
                } else {
                    updateSensorText("Waiting for location updates.");
                }
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // Don't record sensor data until we have location data (for
                // now)
                if (lastLocation != null) {
                    Log.v(TAG, "Sensor changed: " + Arrays.toString(event.values));
                    mockSensorEventManager.addSensorEvent(event);
                    updateSensorText("Recording sensorChanged...");

                } else {
                    updateSensorText("Waiting for location updates.");
                }
            }
        };
    }

    public void startSensorListener() {
        Log.v(TAG, "Listening for sensor updates.");
        for (Sensor s : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            sensorManager.registerListener(sensorEventListener,
                    s, SensorManager.SENSOR_DELAY_GAME,
                    new Handler(sensorHandlerThread.getLooper()));
        }
    }

    public void stopSensorListener() {
        Log.v(TAG, "Stopping listening for sensor updates.");
        updateSensorText("Not listenting for sensor updates.");
        // Unregister our listener for all sensors
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void initLocationManager() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationHandlerThread = new HandlerThread("LocationHandlerThread");
        locationHandlerThread.start();
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {
                lastLocation = loc;
                mockLocationManager.addLocation(loc, "onLocationChanged", -1);
                final String displayLoc = app.buildLocationDisplayString(loc);
                locationText.post(new Runnable() {

                    @Override
                    public void run() {
                        locationText.setText(locationPrefix + displayLoc);
                    }

                });
                // TODO: This seems like a good point to update the Wifi scan
                // results

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

        };
    }

    public void startLocationListener() {
        Log.v(TAG, "Listening for location updates.");
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,
                locationHandlerThread.getLooper());
    }

    public void stopLocationListener() {
        Log.v(TAG, "Stopping listening for location updates.");
        locationManager.removeUpdates(locationListener);
    }

    private void initWifiManager() {
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networks = mWifiManager.getScanResults();
        Log.v(TAG, "Networks: " + networks);
        // mockScanResultManager.enableGrouping();
        // mockScanResultManager.addScanResults(mWifiManager.getScanResults());
        // mockScanResultManager.disableGrouping();
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
            // LOLWAT
            startSensorListener();
            startLocationListener();
        } else {
            recordButton.setText(R.string.record);
            // LOLWAT
            stopSensorListener();
            stopLocationListener();
        }
        lastLocation = null;
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
            updateSensorText("Not listenting for sensor updates.");
        }
    }

    public void recordButtonClicked(View v) {
        Log.v(MainActivity.TAG, "Checking if objective (" + getSelectedObjectiveName()
                + ") already has a recording.");
        if (!app.isRecording()) {
            if (objectivesManager.hasMocks(getSelectedObjectiveName())) {
                showOverwriteRecordingDialog();
            } else {
                recordReplayManager.toggleRecording();
                app.toggleIsRecording();
            }
        } else {
            recordReplayManager.setIsRecording(false);
            app.setIsRecording(false);
        }
        prepareToRecord();
    }

    public void replayButtonClicked(View v) {
        // TODO: Set up pub-sub between MainActivity and MockerService
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

    public void insertWifiScanResults(View view) {
        mockScanResultManager.enableGrouping();
        mockScanResultManager.addScanResults(mWifiManager.getScanResults());
        mockScanResultManager.disableGrouping();
    }

}
