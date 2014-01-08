package edu.buffalo.cse.blue.recordreplay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private String TAG = "REC";
	
	private boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Record the user's location until they stop recording.
     * @param view the Record Button pressed.
     */
    public void recordLocation(View view) {
    	Button recordButton = (Button)view;
    	recording = !recording;
    	Log.v(TAG, "Recording: " + recording);
    	if(recording) {
    		recordButton.setText(R.string.stop_record);
    	} else {
    		recordButton.setText(R.string.record);
    	}
    }
    
}
