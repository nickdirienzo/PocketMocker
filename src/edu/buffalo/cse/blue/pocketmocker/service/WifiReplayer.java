
package edu.buffalo.cse.blue.pocketmocker.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MockerService;
import edu.buffalo.cse.blue.pocketmocker.models.MockScanResult;
import edu.buffalo.cse.blue.pocketmocker.models.MockWifiManager;

import java.util.ArrayList;

public class WifiReplayer implements Runnable {

    public static final String TAG = MockerService.TAG + MockerService.PACKAGE_SUFFIX_DELIM
            + MockerService.WIFI_SUFFIX;

    private MockerService mMockerService;
    private MockWifiManager mMockWifiManager;

    public WifiReplayer(MockerService m) {
        mMockerService = m;
        mMockWifiManager = MockWifiManager.getInstance(mMockerService.getApplicationContext());
        mMockerService.signalAlive(TAG);
    }

    private void sendMockScanResults(ArrayList<MockScanResult> result, Messenger m) {
        Bundle data = new Bundle();
        data.putInt("size", result.size());
        data.putBoolean(MockerService.IS_REPLAYING, true);
        for (int i = 0; i < result.size(); i++) {
            data.putBundle(String.valueOf(i), result.get(i)
                    .toBundle(System.currentTimeMillis()));
        }
        Message mockMsg = Message.obtain();
        mockMsg.setData(data);
        try {
            m.send(mockMsg);
        } catch (RemoteException e) {
            Log.v(TAG, "Having a hard time sending msg", e);
            e.printStackTrace();
        }
    }

    private void broadcast(ArrayList<MockScanResult> scanResults) {
        for (String client : mMockerService.getWifiClients().keySet()) {
            sendMockScanResults(scanResults, mMockerService.getWifiClients().get(client));
            Log.v(TAG, "Sending mock scan results: " + scanResults.toString());
        }
    }

    @Override
    public void run() {
        long[] groupStartTimestamps = mMockWifiManager.getScanResultGroupsForCurrentRecording();
        ArrayList<MockScanResult> scanResults;
        long timeDelta;
        Log.v(TAG, "Group timestamps: " + groupStartTimestamps.length);
        // We only need to run for as long as we have scan results to send
        for (int i = 0; i < groupStartTimestamps.length; i++) {
            scanResults = mMockWifiManager.getCurrentRecordingScanResultsForGroup(i);
            broadcast(scanResults);
            if (i != groupStartTimestamps.length - 1) {
                timeDelta = groupStartTimestamps[i + 1] - groupStartTimestamps[i];
                Log.v(TAG, "Wifi wait: " + timeDelta);
                try {
                    Thread.sleep(timeDelta);
                } catch (InterruptedException e) {
                    mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
                    break;
                }
            }
        }
        Log.v(TAG, "Died.");
        mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
    }
}
