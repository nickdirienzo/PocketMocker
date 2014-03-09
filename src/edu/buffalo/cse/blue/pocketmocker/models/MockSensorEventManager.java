
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockSensorEventManager extends ModelManager {

    private static MockSensorEventManager sInstance;
    private RecordingManager recordingManager;

    private static final String ON_SENSOR_CHANGED = "onSensorChanged";
    private static final String ON_ACCURACY_CHANGED = "onAccuracyChanged";

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
        insert(mockEvent.toContentValues(), MockSensorEvent.TABLE_NAME);
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

    public ArrayList<MockSensorEvent> getMockSensorEventsForRecording(long id) {
        ArrayList<MockSensorEvent> sensorEvents = new ArrayList<MockSensorEvent>();
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

    public ArrayList<MockSensorEvent> getMockSensorEventsForCurrentRecording() {
        return this.getMockSensorEventsForRecording(recordingManager.getCurrentRecordingId());
    }

    public void init() {
        if (mSensorEvents.size() == 0) {
            mSensorEvents = getMockSensorEventsForRecording(recordingManager
                    .getCurrentRecordingId());
        }
    }

    public MockSensorEvent getNext() {
        if (hasNext()) {
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
