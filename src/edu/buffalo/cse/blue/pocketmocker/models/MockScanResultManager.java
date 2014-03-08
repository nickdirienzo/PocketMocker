package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.List;

public class MockScanResultManager extends ModelManager {
    
    private static MockScanResultManager sInstance;
    private RecordingManager recordingManager;

    public static MockScanResultManager getInstance(Context c) {
        if(sInstance == null) {
            sInstance = new MockScanResultManager(c);
        }
        return sInstance;
    }
    
    protected MockScanResultManager(Context c) {
        super(c);
        recordingManager = RecordingManager.getInstance(c);
    }
    
    private void insertMockScanResult(MockScanResult mockScanResult) {
        insert(mockScanResult.toContentValues(), MockScanResult.TABLE_NAME);
    }
    
    public void addScanResults(List<ScanResult> scanResults) {
        long recId = recordingManager.getCurrentRecordingId();
        for(ScanResult s: scanResults) {
            MockScanResult m = new MockScanResult(s, recId);
            insertMockScanResult(m);
        }
    }

}
