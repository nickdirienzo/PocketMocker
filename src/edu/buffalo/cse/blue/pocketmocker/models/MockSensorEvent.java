
package edu.buffalo.cse.blue.pocketmocker.models;

import android.hardware.Sensor;
import android.os.Bundle;

import java.util.Date;

public class MockSensorEvent extends TimestampModel {

    private long id;
    private Date creationDate;
    private long recordingId;

    // SensorEvent Fields
    // We can recreate the SensorEvent object in the platform, so we can just
    // leave it like this.
    private int eventAccuracy;
    private int eventSensorType;
    private long eventTimestamp;
    // Saved as a comma-separated string in the DB
    private float[] eventValues;

    // SQL things
    private static final String TABLE_NAME = "sensor_events";
    private static int index = 0;
    public static final String COL_ID = "_id";
    public static final int COL_ID_INDEX = index++;
    public static final String COL_CREATION_DATE = "creation_date";
    public static final int COL_CREATION_DATE_INDEX = index++;
    public static final String COL_RECORDING = "rec_id";
    public static final int COL_RECORDING_INDEX = index++;
    public static final String COL_EVENT_ACCURACY = "event_accuracy";
    public static final int COL_EVENT_ACCURACY_INDEX = index++;
    // This is just the int Type of the Sensor so we can get it later in the
    // platform
    public static final String COL_EVENT_SENSOR = "event_sensord_id";
    public static final int COL_EVENT_SENSOR_INDEX = index++;
    public static final String COL_EVENT_TIMESTAMP = "event_timestamp";
    public static final int COL_EVENT_TIMESTAMP_INDEX = index++;
    public static final String COL_EVENT_VALUES = "event_values";
    public static final int COL_EVENT_VALUES_INDEX = index++;

    public static final String COL_RECORDING_FK = OPEN_PAREN + COL_RECORDING + CLOSE_PAREN +
            REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
    public static String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID
            + INT + PK + COMMA + COL_CREATION_DATE + TEXT + COMMA + COL_RECORDING + INT + COMMA
            + COL_EVENT_ACCURACY + INT + COMMA + COL_EVENT_SENSOR + INT + COMMA + COL_EVENT_VALUES
            + TEXT + CLOSE_PAREN;
    public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);

    public MockSensorEvent() {
        creationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreationDateSqlString() {
        return this.serializeDateToSqlString(creationDate);
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationDate(String sqlStringDate) {
        this.creationDate = this.serializeSqlStringToDate(sqlStringDate);
    }

    public long getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(long recordingId) {
        this.recordingId = recordingId;
    }

    public int getEventAccuracy() {
        return eventAccuracy;
    }

    public void setEventAccuracy(int eventAccuracy) {
        this.eventAccuracy = eventAccuracy;
    }

    public int getEventSensorType() {
        return eventSensorType;
    }

    public void setEventSensorType(int eventSensorType) {
        this.eventSensorType = eventSensorType;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public float[] getEventValues() {
        return eventValues;
    }

    public void setEventValues(float[] eventValues) {
        this.eventValues = eventValues;
    }

    /**
     * Bundle representing an android.hardware.SensorEvent — with the exception
     * of the Sensor object which will be built on the platform side
     * 
     * @param time
     * @return
     */
    public Bundle toBundle(long time) {
        Bundle data = new Bundle();
        data.putInt("accuracy", eventAccuracy);
        data.putInt("sensorType", eventSensorType);
        data.putFloatArray("values", eventValues);
        data.putLong("timestamp", time);
        return data;
    }

}
