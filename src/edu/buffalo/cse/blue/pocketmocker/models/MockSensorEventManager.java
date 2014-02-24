
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MainActivity;
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

    private void insertMockSensorEvent(MockSensorEvent mockEvent) {
        ContentValues values = new ContentValues();
        values.put(MockSensorEvent.COL_CREATION_DATE, mockEvent.getCreationDateSqlString());
        values.put(MockSensorEvent.COL_RECORDING, mockEvent.getRecordingId());
        values.put(MockSensorEvent.COL_EVENT_ACCURACY, mockEvent.getEventAccuracy());
        values.put(MockSensorEvent.COL_EVENT_SENSOR, mockEvent.getEventSensorType());
        values.put(MockSensorEvent.COL_EVENT_TIMESTAMP, mockEvent.getEventTimestamp());
        values.put(MockSensorEvent.COL_EVENT_VALUES, this.serialize(mockEvent.getEventValues()));
        values.put(MockSensorEvent.COL_EVENT_TYPE, mockEvent.getEventType());
        insert(values, MockSensorEvent.TABLE_NAME);
        Log.v(MainActivity.TAG, "Inserted " + values.toString() + " into db.");
    }

    public void addSensorEvent(SensorEvent event) {
        MockSensorEvent m = new MockSensorEvent(event, app.getCurrentRecordingId(),
                "onSensorChanged");
        insertMockSensorEvent(m);
    }

    public void addAccuracyChange(Sensor sensor, int accuracy) {
        MockSensorEvent m = new MockSensorEvent(sensor, accuracy, app.getCurrentRecordingId(),
                "onAccuracyChanged");
        insertMockSensorEvent(m);
    }

    public void addTrigerEvent(TriggerEvent event) {
        MockSensorEvent m = new MockSensorEvent(event, app.getCurrentRecordingId(), "onTrigger");
        insertMockSensorEvent(m);
    }

}
