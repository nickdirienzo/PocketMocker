
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MainActivity;
import edu.buffalo.cse.blue.pocketmocker.PocketMockerApplication;

import java.util.LinkedList;
import java.util.List;

public class MockSensorEventManager extends ModelManager {

    private static MockSensorEventManager sInstance;
    private RecordingManager recordingManager;

    private static final String ON_SENSOR_CHANGED = "onSensorChanged";
    private static final String ON_ACCURACY_CHANGED = "onAccuracyChanged";
    private static final String ON_TRIGGER = "onTrigger";

    private List<MockSensorEvent> mSensorEvents;

    public static MockSensorEventManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new MockSensorEventManager(c);
        }
        return sInstance;
    }

    protected MockSensorEventManager(Context c) {
        super(c);
        recordingManager = RecordingManager.getInstance(c);
        mSensorEvents = new LinkedList<MockSensorEvent>();
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
        MockSensorEvent m = new MockSensorEvent(event, recordingManager.getCurrentRecordingId(),
                ON_SENSOR_CHANGED);
        insertMockSensorEvent(m);
    }

    public void addAccuracyChange(Sensor sensor, int accuracy) {
        MockSensorEvent m = new MockSensorEvent(sensor, accuracy,
                recordingManager.getCurrentRecordingId(),
                ON_ACCURACY_CHANGED);
        insertMockSensorEvent(m);
    }

    public void addTrigerEvent(TriggerEvent event) {
        MockSensorEvent m = new MockSensorEvent(event, recordingManager.getCurrentRecordingId(),
                ON_TRIGGER);
        insertMockSensorEvent(m);
    }

    public List<MockSensorEvent> getMockSensorEventsForRecording(long id) {
        List<MockSensorEvent> sensorEvents = new LinkedList<MockSensorEvent>();
        SQLiteDatabase db = manager.openDatabase();
        Cursor c = db.query(MockSensorEvent.TABLE_NAME, null, MockSensorEvent.COL_RECORDING + "=?",
                new String[] {
                    String.valueOf(id)
                }, null,
                null, MockSensorEvent.COL_EVENT_TIMESTAMP);
        if (c.moveToFirst()) {
            do {
                String eventType = getString(c, MockSensorEvent.COL_EVENT_TYPE_INDEX);
                int accuracy = getInt(c, MockSensorEvent.COL_EVENT_ACCURACY_INDEX);
                int sensorType = getInt(c, MockSensorEvent.COL_EVENT_SENSOR_INDEX);
                long timestamp = getLong(c, MockSensorEvent.COL_EVENT_TIMESTAMP_INDEX);
                float[] values = deserialize(c.getString(MockSensorEvent.COL_EVENT_VALUES_INDEX));
                sensorEvents.add(new MockSensorEvent(accuracy, sensorType,
                        timestamp, values, id, eventType));
            } while (c.moveToNext());
        }
        manager.closeDatabase();
        return sensorEvents;
    }

    public void init() {
        if (mSensorEvents.size() == 0) {
            mSensorEvents = getMockSensorEventsForRecording(recordingManager
                    .getCurrentRecordingId());
        }
    }
    
    public MockSensorEvent getNext() {
        if(hasNext()) {
            return mSensorEvents.remove(0);
        }
        return null;
    }
    
    public boolean hasNext() {
        return mSensorEvents.size() != 0;
    }
    
    public boolean isReady() {
        return hasNext();
    }
}
