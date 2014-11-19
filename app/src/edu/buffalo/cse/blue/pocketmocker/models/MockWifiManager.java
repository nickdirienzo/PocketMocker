
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class MockWifiManager extends ModelManager {

    private static MockWifiManager sInstance;
    private RecordingManager recordingManager;
    private int groupId;

    public static MockWifiManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new MockWifiManager(c);
        }
        return sInstance;
    }

    protected MockWifiManager(Context c) {
        super(c);
        recordingManager = RecordingManager.getInstance(c);
        groupId = 0;
    }

    public void enableGrouping() {
        groupId = 0;
    }

    public void disableGrouping() {
        groupId = 0;
    }

    private void insertMockScanResult(MockScanResult mockScanResult) {
        insert(mockScanResult.toContentValues(), MockScanResult.TABLE_NAME);
    }

    public void addScanResults(List<ScanResult> scanResults) {
        long recId = recordingManager.getCurrentRecordingId();
        for (ScanResult s : scanResults) {
            MockScanResult m = new MockScanResult(s, recId, groupId);
            insertMockScanResult(m);
        }
        groupId++;
    }

    public long[] getScanResultGroupsForRecording(long recId) {
        SQLiteDatabase db = manager.openDatabase();
        Cursor c = db
                .rawQuery(
                        "SELECT min(" + MockScanResult.COL_ID
                                + "), " + MockScanResult.COL_CREATION_TIMESTAMP + " FROM "
                                + MockScanResult.TABLE_NAME + " WHERE "
                                + MockScanResult.COL_RECORDING + "=? GROUP BY "
                                + MockScanResult.COL_GROUP,
                        new String[] {
                            String.valueOf(recId)
                        });
        int groups = c.getCount();
        long[] resultGroups = new long[groups];
        int counter = 0;
        if (c.moveToFirst()) {
            do {
                long timestamp = getLong(c, MockScanResult.COL_CREATION_TIMESTAMP_INDEX);
                resultGroups[counter] = timestamp;
                counter++;
            } while (c.moveToNext());
        }
        manager.closeDatabase();
        return resultGroups;
    }

    public long[] getScanResultGroupsForCurrentRecording() {
        return getScanResultGroupsForRecording(recordingManager.getCurrentRecordingId());
    }

    /**
     * Get all scan results within our group for this recording
     * 
     * @param recId
     * @param groupId
     * @return
     */
    public ArrayList<MockScanResult> getScanResults(long recId, long groupId) {
        ArrayList<MockScanResult> scanResults = new ArrayList<MockScanResult>();
        SQLiteDatabase db = manager.openDatabase();
        Cursor c = db.query(MockScanResult.TABLE_NAME, null, MockScanResult.COL_RECORDING
                + "=? AND " + MockScanResult.COL_GROUP + "=?", new String[] {
                String.valueOf(recId), String.valueOf(groupId)
        }, null, null,
                MockScanResult.COL_ID);
        if (c.moveToFirst()) {
            do {
                scanResults.add(new MockScanResult(c));
            } while (c.moveToNext());
        }
        manager.closeDatabase();
        return scanResults;
    }

    public ArrayList<MockScanResult> getCurrentRecordingScanResultsForGroup(long groupId) {
        return getScanResults(recordingManager.getCurrentRecordingId(), groupId);
    }

    /**
     * Returns a List containing all scan results sorted by groupId
     * 
     * @param recId
     * @return
     */
    public ArrayList<MockScanResult> getScanResultsForRecording(long recId) {
        ArrayList<MockScanResult> scanResults = new ArrayList<MockScanResult>();
        SQLiteDatabase db = manager.openDatabase();
        Cursor c = db.query(MockScanResult.TABLE_NAME, null, MockScanResult.COL_RECORDING + "=?",
                new String[] {
                    String.valueOf(recId)
                }, null, null, MockScanResult.COL_GROUP);
        if (c.moveToFirst()) {
            do {
                scanResults.add(new MockScanResult(c));
            } while (c.moveToNext());
        }
        manager.closeDatabase();
        return scanResults;
    }

    public ArrayList<MockScanResult> getScanResultsForCurrentRecording() {
        return getScanResultsForRecording(recordingManager.getCurrentRecordingId());
    }

}
