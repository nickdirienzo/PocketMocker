package edu.buffalo.cse.blue.pocketmocker;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.TextView;

import edu.buffalo.cse.blue.pocketmocker.models.MockWifiManager;

import java.util.List;


public class WifiScanResultTask implements Runnable {
    
    private MockWifiManager mMockWifiManager;
    private WifiManager mWifiManager;
    private TextView mLog;
    
    public WifiScanResultTask(MockWifiManager mock, WifiManager wm, TextView tv) {
        mMockWifiManager = mock;
        mWifiManager = wm;
        mLog = tv;
    }
    
    @Override
    public void run() {
        final List<ScanResult> scanResults = mWifiManager.getScanResults();
        mMockWifiManager.addScanResults(scanResults);
        mLog.post(new Runnable() {

            @Override
            public void run() {
                mLog.append("Found " + scanResults.size() + " wifi networks.");
            }
            
        });
    }

}
