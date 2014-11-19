
package edu.buffalo.cse.blue.pocketmocker.service;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import edu.buffalo.cse.blue.pocketmocker.MockerService;
import edu.buffalo.cse.blue.pocketmocker.models.MockCellLocation;
import edu.buffalo.cse.blue.pocketmocker.models.MockCellLocationManager;

import java.util.ArrayList;

public class CellLocationReplayer implements Runnable {

    public static final String TAG = MockerService.TAG + MockerService.PACKAGE_SUFFIX_DELIM
            + MockerService.TELE_SUFFIX;

    private MockerService mMockerService;
    private MockCellLocationManager mMockCellLocationManager;

    public CellLocationReplayer(MockerService m) {
        mMockerService = m;
        mMockCellLocationManager = MockCellLocationManager.getInstance(mMockerService
                .getApplicationContext());
        mMockerService.signalAlive(TAG);
    }

    private void sendMockCellLocation(MockCellLocation mock, Messenger m) {
        Bundle data = mock.toBundle();
        data.putBoolean(MockerService.IS_REPLAYING, true);
        Message mockMsg = Message.obtain();
        mockMsg.setData(data);
        try {
            m.send(mockMsg);
        } catch (RemoteException e) {
            Log.v(TAG, "Having a hard time sending msg", e);
            e.printStackTrace();
        }
    }

    private void broadcast(MockCellLocation m) {
        for (String client : mMockerService.getTeleClients().keySet()) {
            sendMockCellLocation(m, mMockerService.getTeleClients().get(client));
            Log.v(TAG, "Sending tele mock to: " + client);
        }
    }

    @Override
    public void run() {
        ArrayList<MockCellLocation> cellLocations = mMockCellLocationManager
                .getCellLocationsForCurrentRecording();
        long timeDelta = 0;
        for (int i = 0; i < cellLocations.size(); i++) {
            broadcast(cellLocations.get(i));
            if (i != cellLocations.size() - 1) {
                Log.v(TAG, "Next: " + cellLocations.get(i + 1).getId() + " Cur: "
                        + cellLocations.get(i).getId());
                timeDelta = cellLocations.get(i + 1).getCreationTimestamp()
                        - cellLocations.get(i).getCreationTimestamp();
                Log.v(TAG, "Waiting for: " + timeDelta);
                try {
                    Thread.sleep(timeDelta);
                } catch (InterruptedException e) {
                    mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
                    break;
                }
            }
        }
        Log.v(TAG, "Dead");
        mMockerService.signalDeathAndMaybeBroadcastTermination(TAG);
    }

}
