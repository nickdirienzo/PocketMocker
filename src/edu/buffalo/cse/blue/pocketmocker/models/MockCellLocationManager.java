
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.telephony.cdma.CdmaCellLocation;

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
    
}
