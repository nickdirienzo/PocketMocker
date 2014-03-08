
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.net.wifi.ScanResult;

import java.util.Date;

public class MockScanResult extends TimestampModel {

    private long id;
    private Date creationDate;
    private long recordingId;

    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;
    private long timestamp;

    public static final String TABLE_NAME = "wifi_results";
    private static int index = 0;
    // PocketMocker required
    public static final String COL_ID = "_id";
    public static final int COL_ID_INDEX = index++;
    public static final String COL_CREATION_DATE = "creation_date";
    public static final int COL_CREATION_DATE_INDEX = index++;
    public static final String COL_RECORDING = "rec_id";
    public static final int COL_RECORDING_INDEX = index++;
    // Serialized android.net.wifi.ScanResult properties
    public static final String COL_BSSID = "bssid";
    public static final int COL_BSSID_INDEX = index++;
    public static final String COL_SSID = "ssid";
    public static final int COL_SSID_INDEX = index++;
    public static final String COL_CAPABILITIES = "capabilities";
    public static final int COL_CAPABILITIES_INDEX = index++;
    public static final String COL_FREQUENCY = "frequency";
    public static final int COL_FREQUENCY_INDEX = index++;
    public static final String COL_LEVEL = "level";
    public static final int COL_LEVEL_INDEX = index++;
    public static final String COL_TIMESTAMP = "timestamp";
    public static final int COL_TIMESTAMP_INDEX = index++;

    public static final String COL_RECORDING_FK = FK + OPEN_PAREN + COL_RECORDING + CLOSE_PAREN +
            REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
    public static String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID + INT
            + PK + COMMA
            + COL_CREATION_DATE + TEXT + COMMA + COL_RECORDING + INT + COMMA
            + COL_BSSID + TEXT + COMMA + COL_SSID + TEXT + COMMA + COL_CAPABILITIES + TEXT + COMMA
            + COL_FREQUENCY + INT + COMMA + COL_LEVEL + INT + COMMA + COL_TIMESTAMP + INT + COMMA
            + COL_RECORDING_FK + CLOSE_PAREN;
    public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);
    
    public MockScanResult(ScanResult s, long recId) {
        creationDate = new Date();
        recordingId = recId;
        bssid = s.BSSID;
        ssid = s.SSID;
        capabilities = s.capabilities;
        frequency = s.frequency;
        level = s.level;
        timestamp = s.timestamp;
    }
    
    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_CREATION_DATE, this.serializeDateToSqlString(creationDate));
        values.put(COL_RECORDING, recordingId);
        values.put(COL_BSSID, bssid);
        values.put(COL_SSID, ssid);
        values.put(COL_CAPABILITIES, capabilities);
        values.put(COL_FREQUENCY, frequency);
        values.put(COL_LEVEL, level);
        values.put(COL_TIMESTAMP, timestamp);
        return values;
    }

}
