package edu.buffalo.cse.blue.pocketmocker;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void displayMockingToast() {
		String isMockingText = "Mocking: " + mLocationManager.isMocking();
		Toast.makeText(getApplicationContext(), isMockingText, Toast.LENGTH_SHORT).show();
	}
	
	public void mockAction(View view) {
		mLocationManager.setMocking(!mLocationManager.isMocking());
		displayMockingToast();
	}
	
	public void submit(View view) {
		Log.v("PM", "hi");
		TextView mockedLat = (TextView)findViewById(R.id.edit_lat);
		TextView mockedLong = (TextView)findViewById(R.id.edit_long);
		String toDisplay = "Lat: " + mockedLat.getText() + " Long: " + mockedLong.getText();
		Toast.makeText(getApplicationContext(), toDisplay, Toast.LENGTH_SHORT).show();
	}

}