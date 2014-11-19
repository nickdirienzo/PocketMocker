
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.os.Bundle;

public class MockScanResult extends TimestampModel {

    private long id;
    private long creationTimestamp;
    private long recordingId;
    private int groupId;

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
    public static final String COL_CREATION_TIMESTAMP = "creation_timestamp";
    public static final int COL_CREATION_TIMESTAMP_INDEX = index++;
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
    // More fields
    public static final String COL_GROUP = "group_id";
    public static final int COL_GROUP_INDEX = index++;

    public static final String COL_RECORDING_FK = FK + OPEN_PAREN + COL_RECORDING + CLOSE_PAREN +
            REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
    public static String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID + INT
            + PK + COMMA
            + COL_CREATION_TIMESTAMP + INT + COMMA + COL_RECORDING + INT + COMMA
            + COL_BSSID + TEXT + COMMA + COL_SSID + TEXT + COMMA + COL_CAPABILITIES + TEXT + COMMA
            + COL_FREQUENCY + INT + COMMA + COL_LEVEL + INT + COMMA + COL_TIMESTAMP + INT + COMMA
            + COL_GROUP + INT + COMMA + COL_RECORDING_FK + CLOSE_PAREN;
    public static final String DROP_TABLE_CMD = dropTable(TABLE_NAME);

    public MockScanResult(ScanResult s, long recId, int groupId) {
        creationTimestamp = System.currentTimeMillis();
        recordingId = recId;
        bssid = s.BSSID;
        ssid = s.SSID;
        capabilities = s.capabilities;
        frequency = s.frequency;
        level = s.level;
        timestamp = s.timestamp;
        this.groupId = groupId;
    }

    /**
     * Create a MockScanResult from the current row in Cursor c
     * 
     * @param c
     */
    public MockScanResult(Cursor c) {
        id = ModelManager.getInt(c, COL_ID_INDEX);
        creationTimestamp = ModelManager.getLong(c, COL_CREATION_TIMESTAMP_INDEX);
        recordingId = ModelManager.getLong(c, COL_RECORDING_INDEX);
        bssid = ModelManager.getString(c, COL_BSSID_INDEX);
        ssid = ModelManager.getString(c, COL_SSID_INDEX);
        capabilities = ModelManager.getString(c, COL_CAPABILITIES_INDEX);
        frequency = ModelManager.getInt(c, COL_FREQUENCY_INDEX);
        level = ModelManager.getInt(c, COL_LEVEL_INDEX);
        timestamp = ModelManager.getLong(c, COL_TIMESTAMP_INDEX);
        groupId = ModelManager.getInt(c, COL_GROUP_INDEX);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(long recordingId) {
        this.recordingId = recordingId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_CREATION_TIMESTAMP, creationTimestamp);
        values.put(COL_RECORDING, recordingId);
        values.put(COL_BSSID, bssid);
        values.put(COL_SSID, ssid);
        values.put(COL_CAPABILITIES, capabilities);
        values.put(COL_FREQUENCY, frequency);
        values.put(COL_LEVEL, level);
        values.put(COL_TIMESTAMP, timestamp);
        values.put(COL_GROUP, groupId);
        return values;
    }
    
    public Bundle toBundle(long time) {
        Bundle data = new Bundle();
        data.putString("BSSID", bssid);
        data.putString("SSID", ssid);
        data.putString("capabilities", capabilities);
        data.putInt("frequency", frequency);
        data.putInt("level", level);
        data.putLong("timestamp", time);
        return data;
    }

}
