
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import edu.buffalo.cse.blue.pocketmocker.PocketMockerApplication;

public class MockSensorEventManager extends ModelManager {

    private static MockSensorEventManager sInstance;
    private PocketMockerApplication app;

    public static MockSensorEventManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new MockSensorEventManager(c);
        }
        return sInstance;
    }

    protected MockSensorEventManager(Context c) {
        super(c);
        app = (PocketMockerApplication) c.getApplicationContext();
    }
    
    public void addSensorEvent(SensorEvent event) {
    	MockSensorEvent mockEvent = new MockSensorEvent(event, app.getCurrentRecordingId());
    	
    }
    
    public void addAccuracyChange(Sensor sensor, int accuracy) {
    	
    }
    
    public void addTrigerEvent(TriggerEvent event) {
    	
    }

}
