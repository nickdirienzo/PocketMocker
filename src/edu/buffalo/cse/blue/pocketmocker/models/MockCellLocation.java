
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.ContentValues;
import android.os.Bundle;
import android.telephony.cdma.CdmaCellLocation;

/**
 * Mocks CDMACellLocation
 * 
 * @author nvd
 */
public class MockCellLocation extends TimestampModel {

    // pm required
    private long id;
    private long creationTimestamp;
    private long recId;

    // cdma cell location fields
    private int baseStationId;
    private int baseStationLat;
    private int baseStationLong;
    private int systemId;
    private int networkId;
    // these keys are copied from the Bundle constructor so we can just create
    // the object at the platform level from this object
    private final static String BASE_STATION_ID = "baseStationId";
    private final static String BASE_STATION_LAT = "baseStationLatitude";
    private final static String BASE_STATION_LONG = "baseStationLongitude";
    private final static String SYSTEM_ID = "systemId";
    private final static String NETWORK_ID = "networkId";

    public static final String TABLE_NAME = "cell_locations";
    private static int index = 0;
    // pm required
    public static final String COL_ID = "_id";
    public static final int COL_ID_INDEX = index++;
    public static final String COL_CREATION_TIMESTAMP = "creation_timestamp";
    public static final int COL_CREATION_TIMESTAMP_INDEX = index++;
    public static final String COL_RECORDING = "rec_id";
    public static final int COL_RECORDING_INDEX = index++;
    // cdmacelllocation
    public static final String COL_STATION_ID = BASE_STATION_ID;
    public static final int COL_STATION_ID_INDEX = index++;
    public static final String COL_STATION_LAT = BASE_STATION_LAT;
    public static final int COL_STATION_LAT_INDEX = index++;
    public static final String COL_STATION_LONG = BASE_STATION_LONG;
    public static final int COL_STATION_LONG_INDEX = index++;
    public static final String COL_SYSTEM_ID = SYSTEM_ID;
    public static final int COL_SYSTEM_ID_INDEX = index++;
    public static final String COL_NETWORK_ID = NETWORK_ID;
    public static final int COL_NETWORK_ID_INDEX = index++;

    public static final String COL_RECORDING_FK = FK + OPEN_PAREN + COL_RECORDING + CLOSE_PAREN +
            REFS + Recording.TABLE_NAME + OPEN_PAREN + Recording.COL_ID + CLOSE_PAREN;
    public static String CREATE_TABLE_CMD = CREATE_TABLE + TABLE_NAME + OPEN_PAREN + COL_ID + INT
            + PK + COMMA
            + COL_CREATION_TIMESTAMP + INT + COMMA + COL_RECORDING + INT + COMMA
            + COL_STATION_ID + INT + COMMA + COL_STATION_LAT + INT + COMMA + COL_STATION_LONG + INT
            + COMMA +
            COL_SYSTEM_ID + INT + COMMA + COL_NETWORK_ID + INT + COMMA + COL_RECORDING_FK
            + CLOSE_PAREN;
    public static String DROP_TABLE_CMD = dropTable(TABLE_NAME);
    
    public MockCellLocation(CdmaCellLocation loc, long recId) {
        creationTimestamp = System.currentTimeMillis();
        this.recId = recId;
        baseStationId = loc.getBaseStationId();
        baseStationLat = loc.getBaseStationLatitude();
        baseStationLong = loc.getBaseStationLongitude();
        systemId = loc.getSystemId();
        networkId = loc.getNetworkId();
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

    public long getRecId() {
        return recId;
    }

    public void setRecId(long recId) {
        this.recId = recId;
    }

    public int getBaseStationId() {
        return baseStationId;
    }

    public void setBaseStationId(int baseStationId) {
        this.baseStationId = baseStationId;
    }

    public int getBaseStationLat() {
        return baseStationLat;
    }

    public void setBaseStationLat(int baseStationLat) {
        this.baseStationLat = baseStationLat;
    }

    public int getBaseStationLong() {
        return baseStationLong;
    }

    public void setBaseStationLong(int baseStationLong) {
        this.baseStationLong = baseStationLong;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public Bundle toBundle() {
        Bundle data = new Bundle();
        data.putInt(BASE_STATION_ID, baseStationId);
        data.putInt(BASE_STATION_LAT, baseStationLat);
        data.putInt(BASE_STATION_LONG, baseStationLong);
        data.putInt(SYSTEM_ID, systemId);
        data.putInt(NETWORK_ID, networkId);
        return data;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_CREATION_TIMESTAMP, creationTimestamp);
        values.put(COL_RECORDING, recId);
        values.put(COL_STATION_ID, baseStationId);
        values.put(COL_STATION_LAT, baseStationLat);
        values.put(COL_STATION_LONG, baseStationLong);
        values.put(COL_SYSTEM_ID, systemId);
        values.put(COL_NETWORK_ID, networkId);
        return values;
    }

}
