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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import edu.buffalo.cse.blue.recordreplay.models.DatabaseHandler;

public class MainActivity extends Activity {
	
	private String TAG = "REC";
	
	private boolean recording = false;
	private TextView locationText;
	private Spinner pathSpinner;
	private String locationPrefix;
	private String activePathId;
	
	private LocationManager locationManager;
	private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationText = (TextView)this.findViewById(R.id.locationText);
        locationPrefix = this.getString(R.string.loc_prefix);
        
        db = new DatabaseHandler(this);
        
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
        		new LocationListener() {

    		@Override
    		public void onLocationChanged(Location loc) {
    			if(recording) {
    				// Logging only when recording because otherwise it's a huge mess in LogCat
    				Log.v(TAG, "LocatoinChanged. Loc: " + loc.toString());
    				// Do we need to log every location change?
    				db.insertLocation(loc, activePathId);
	    			String displayLoc = MainActivity.buildLocationDisplayString(loc);
	    			locationText.setText(locationPrefix + displayLoc);
	    			Log.v(TAG, "Location count: " + db.getLocationCount());
    			}
    		}

    		@Override
    		public void onProviderDisabled(String arg0) {
    			Log.v(TAG, "Provider disabled. Alert user that we need this turned on to function.");
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
    
    private static String buildLocationDisplayString(Location loc) {
    	return "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";
    }
    
    /**
     * Record the user's location until they stop recording.
     * @param view the Record Button pressed.
     */
    public void recordLocation(View view) {
    	Button recordButton = (Button)view;
    	recording = !recording;
    	if(recording) {
    		Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		String displayLoc = MainActivity.buildLocationDisplayString(lastLoc);
    		db.insertLocation(lastLoc, activePathId);
    		recordButton.setText(R.string.stop_record);
    		locationText.setText(locationPrefix + displayLoc);
    	} else {
    		recordButton.setText(R.string.record);
    		locationText.setText(this.getString(R.string.loc_placeholder));
    	}
    }
    
}
