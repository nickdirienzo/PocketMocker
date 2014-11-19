
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.cdma.CdmaCellLocation;

import java.util.ArrayList;

public class MockCellLocationManager extends ModelManager {

    private static MockCellLocationManager sInstance;
    private RecordingManager recordingManager;

    public static MockCellLocationManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new MockCellLocationManager(c);
        }
        return sInstance;
    }

    protected MockCellLocationManager(Context c) {
        super(c);
        recordingManager = RecordingManager.getInstance(c);
    }

    public void addCellLocation(CdmaCellLocation c) {
        insert(new MockCellLocation(c, recordingManager.getCurrentRecordingId()).toContentValues(),
                MockCellLocation.TABLE_NAME);
    }

    public ArrayList<MockCellLocation> getCellLocationsForRecording(long recId) {
        ArrayList<MockCellLocation> cellLocations = new ArrayList<MockCellLocation>();
        SQLiteDatabase db = manager.openDatabase();
        Cursor c = db.query(MockCellLocation.TABLE_NAME, null, MockCellLocation.COL_RECORDING
                + "=?", new String[] {
            String.valueOf(recId)
        }, null, null, MockCellLocation.COL_CREATION_TIMESTAMP);
        if(c.moveToFirst()) {
            do {
                cellLocations.add(new MockCellLocation(c));
            } while(c.moveToNext());
        }
        manager.closeDatabase();
        return cellLocations;
    }
    
    public ArrayList<MockCellLocation> getCellLocationsForCurrentRecording() {
        return getCellLocationsForRecording(recordingManager.getCurrentRecordingId());
    }

}
