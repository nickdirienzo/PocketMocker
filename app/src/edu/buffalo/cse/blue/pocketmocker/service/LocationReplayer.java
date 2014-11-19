
package edu.buffalo.cse.blue.pocketmocker.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MockerService;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocation;
import edu.buffalo.cse.blue.pocketmocker.models.MockLocationManager;

import java.util.ArrayList;

public class LocationReplayer implements Runnable {

    public static final String TAG = MockerService.TAG + MockerService.PACKAGE_SUFFIX_DELIM
            + MockerService.LOCATION_SUFFIX;

    private MockerService mMockerService;
    private MockLocationManager mMockLocationManager;

    public LocationReplayer(MockerService m) {
        mMockerService = m;
        mMockLocationManager = MockLocationManager.getInstance(mMockerService
                .getApplicationContext());
        mMockerService.signalAlive(TAG);
    }

    /**
     * Guarantees the following keys: bool hasLocation, long mockId. If
     * hasLocation is true, then we are also replaying. If hasLocation is false,
     * we are not replaying.
     * 
     * @param mLoc
     * @param m
     * @param isReplaying
     */
    private void sendMockLocation(MockLocation mLoc, Messenger m) {
        Bundle data = new Bundle();
        Log.v(TAG, "We have a location to mock!");
        data = mLoc.toBundle(System.currentTimeMillis());
        data.putBoolean("hasLocation", true);
        data.putBoolean(MockerService.IS_REPLAYING, true);
        long id = mLoc.getId();
        data.putLong("mockId", id);
        Message reply = Message.obtain();
        reply.setData(data);
        try {
            Log.v(TAG, "Sending location for mock: " + id);
            m.send(reply);
        } catch (RemoteException e) {
            // TODO: Handle android.os.DeadObjectException better.
            Log.v(TAG, "RemoteException: " + e.toString());
        }
    }

    private void broadcastMockLocation(MockLocation m) {
        for (String clientPkg : mMockerService.getLocationClients().keySet()) {
            Log.v(TAG, "Sending message to: " + clientPkg);
            sendMockLocation(m, mMockerService.getLocationClients().get(clientPkg));
        }
    }

    @Override
    public void run() {
        ArrayList<MockLocation> mockLocations = mMockLocationManager
                .getMockLocationsForCurrentRecording();
        long timeDelta = 0;
        for (int i = 0; i < mockLocations.size(); i++) {
            broadcastMockLocation(mockLocations.get(i));
            if (i != mockLocations.size() - 1) {
                timeDelta = mockLocations.get(i + 1).getRealLocation().getTime()
                        - mockLocations.get(i).getRealLocation().getTime();
                Log.v(TAG, "Waiting for: " + timeDelta);
                try {
                    Thread.sleep(timeDelta);
                } catch (InterruptedException e) {
                    mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
                    break;
                }
            }
        }
        Log.v(TAG, "Died .");
        mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
    }

}
